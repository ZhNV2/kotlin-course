package ru.spbau.mit.execution

import ru.spbau.mit.ast.*
import ru.spbau.mit.exception.AlreadyFinishedException
import ru.spbau.mit.exception.AlreadyRunException
import ru.spbau.mit.exception.IllegalNumberOfArguments
import kotlin.coroutines.experimental.*

class AstExecVisitor private constructor(private var scope: Scope) : AstVisitor<Int?>, Debugger {

    private var nextContinuation: Continuation<Unit>? = null
    private var debugMode = true
    private val breakPoints: MutableMap<Int, ExpressionNode> = mutableMapOf()
    private val breakPointsText: MutableMap<Int, String> = mutableMapOf()
    private var outputBuilder = StringBuilder()
    private var hasRun = false

    val output
        get() = outputBuilder.toString()

    override fun load(node: AstNode) {
        stop()
        launch {
            node.accept(this)
        }

    }

    private fun launch(suspendLambda: suspend () -> Unit) {
        nextContinuation = suspendLambda.createCoroutine(object : SimpleContinuation<Unit>() {
            override fun resume(value: Unit) {}
        })
    }

    override fun breakPoint(line: Int) {
        condition(line, LiteralNode(1, 1), "1")
    }

    override fun condition(line: Int, expression: ExpressionNode, text: String) {
        breakPoints.put(line, expression)
        breakPointsText.put(line, text)
    }

    override fun list() {
        for (entries in breakPointsText.entries) {
            println("line=${entries.key}, condition=${entries.value}")
        }
    }

    override fun remove(line: Int) {
        breakPoints.remove(line)
        breakPointsText.remove(line)
    }

    override suspend fun evaluateIndependently(expression: ExpressionNode): Int {
        val copyScope = scope.copy()
        return AstExecVisitor(copyScope).evaluate(expression)
    }

    private suspend fun evaluate(expression: ExpressionNode): Int {
        return expression.accept(ExpressionExecVisitor())
    }

    override fun stop() {
        scope = Scope.empty()
        breakPoints.clear()
        breakPointsText.clear()
        debugMode = true
        outputBuilder = StringBuilder()
        hasRun = false
        nextContinuation = null
    }

    override fun continueDebug() {
        val step = nextContinuation ?: throw AlreadyFinishedException()
        outputBuilder = StringBuilder()
        nextContinuation = null
        step.resume(Unit)
    }


    override fun run() {
        if (hasRun) {
            throw AlreadyRunException()
        }
        hasRun = true
        continueDebug()
    }

    override fun lastRunOutput() = output


    private suspend fun pauseDebug() {
        return  suspendCoroutine {
            continuation -> nextContinuation = continuation
        }
    }


    private var lastLine = -1

    private suspend fun checkBreakPoint(node: AstNode) {
        if (debugMode && node.line != lastLine) {
            val expression = breakPoints[node.line]
            if (expression != null && toBoolean(evaluateIndependently(expression))) {
                pauseDebug()
            }
        }
        lastLine = node.line

    }

    override suspend fun visit(fileNode: FileNode): Int? {
        checkBreakPoint(fileNode)
        return fileNode.block.accept(this)
    }

    override suspend fun visit(blockNode: BlockNode): Int? {
        checkBreakPoint(blockNode)
        scope = Scope.from(scope)
        for (statement in blockNode.statements) {
            statement.accept(this)?.let {
                return it
            }
        }
        scope = scope.prev
        return null
    }

    override suspend fun visit(printlnNode: PrintlnNode): Int? {
        checkBreakPoint(printlnNode)
        val out = printlnNode.args.map { it.accept(this) }.joinToString(" ")
        println(out)
        outputBuilder.append(out).append("\n")
        return null
    }

    override suspend fun visit(functionNode: FunctionNode): Int? {
        checkBreakPoint(functionNode)
        val funcName = functionNode.name
        val argNames = functionNode.argNames
        scope.putFunc(funcName, Function(scope, functionNode.body, argNames))
        return null
    }


    override suspend fun visit(variableNode: VariableNode): Int? {
        checkBreakPoint(variableNode)
        val value = variableNode.varValue.accept(this) as Int
        scope.putVar(variableNode.varName, value, false)
        return null
    }

    override suspend fun visit(whileNode: WhileNode): Int? {
        checkBreakPoint(whileNode)
        while (checkCondition(whileNode.condition)) {
            whileNode.body.accept(this)?.let {
                return it
            }
        }
        return null
    }

    private suspend fun checkCondition(expressionNode: ExpressionNode): Boolean {
        val value = expressionNode.accept(this) as Int
        return value != 0
    }

    override suspend fun visit(ifNodeNode: IfNode): Int? {
        checkBreakPoint(ifNodeNode)
        if (checkCondition(ifNodeNode.condition)) {
            return ifNodeNode.ifBody.accept(this)
        } else if (ifNodeNode.elseBody != null) {
            return ifNodeNode.elseBody.accept(this)
        }
        return null
    }

    override suspend fun visit(assignmentNode: AssignmentNode): Int? {
        checkBreakPoint(assignmentNode)
        val newValue = assignmentNode.newValue.accept(this) as Int
        scope.putVar(assignmentNode.varName, newValue, true)
        return null
    }

    override suspend fun visit(returnNode: ReturnNode): Int? {
        return returnNode.value.accept(ExpressionExecVisitor())
    }

    override suspend fun visit(literalNode: LiteralNode): Int? {
        return literalNode.accept(ExpressionExecVisitor())
    }

    override suspend fun visit(binaryExpressionNode: BinaryExpressionNode): Int? {
        return binaryExpressionNode.accept(ExpressionExecVisitor())
    }

    override suspend fun visit(functionCallNode: FunctionCallNode): Int? {
        return functionCallNode.accept(ExpressionExecVisitor())
    }

    override suspend fun visit(varNode: VarNode): Int? {
        return varNode.accept(ExpressionExecVisitor())
    }

    inner class ExpressionExecVisitor : ExpressionVisitor<Int> {
        override suspend fun visit(literalNode: LiteralNode): Int {
            checkBreakPoint(literalNode)
            return literalNode.value
        }

        override suspend fun visit(binaryExpressionNode: BinaryExpressionNode): Int {
            checkBreakPoint(binaryExpressionNode)
            val left = binaryExpressionNode.left.accept(this)
            val right = binaryExpressionNode.right.accept(this)
            return when (binaryExpressionNode.op) {
                "*" -> left * right
                "/" -> left / right
                "%" -> left % right
                "+" -> left + right
                "-" -> left - right
                "<" -> toInt(left < right)
                ">" -> toInt(left > right)
                "<=" -> toInt(left <= right)
                ">=" -> toInt(left >= right)
                "==" -> toInt(left == right)
                "!=" -> toInt(left != right)
                "||" -> toInt(toBoolean(left) || toBoolean(right))
                "&&" -> toInt(toBoolean(left) && toBoolean(right))
                else -> throw IllegalStateException("unknown operation $binaryExpressionNode.op")
            }
        }

        override suspend fun visit(functionCallNode: FunctionCallNode): Int {
            checkBreakPoint(functionCallNode)
            val func = scope.getFunc(functionCallNode.name)
            val args = functionCallNode.args.map { it.accept(this) }
            if (func.argNames.size != args.size) {
                throw IllegalNumberOfArguments("${functionCallNode.name} was called with invalid number of params")
            }
            val funcCtx = Scope.from(func.scope)
            for (i in 0 until func.argNames.size) {
                funcCtx.putVar(func.argNames[i], args[i], false)
            }
            val execVisitor = AstExecVisitor(funcCtx)
            val res = func.block.accept(execVisitor) ?: 0
            outputBuilder.append(execVisitor.outputBuilder)
            return res
        }

        override suspend fun visit(varNode: VarNode): Int {
            checkBreakPoint(varNode)
            return scope.getVar(varNode.name)
        }

    }

    private fun toInt(boolean: Boolean): Int {
        return if (boolean) 1 else 0
    }

    private fun toBoolean(int: Int): Boolean {
        return int != 0
    }

    companion object {
        fun build() = AstExecVisitor(Scope.empty())
    }

}