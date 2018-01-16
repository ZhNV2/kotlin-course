package ru.spbau.mit.execution

import ru.spbau.mit.ast.*
import ru.spbau.mit.exception.AlreadyFinishedException
import ru.spbau.mit.exception.AlreadyRunException
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.createCoroutine
import kotlin.coroutines.experimental.suspendCoroutine


open class DebugVisitor protected constructor(scope: Scope) : AstExecVisitor(scope),  Debugger {

    private val breakPoints: MutableMap<Int, ExpressionNode> = mutableMapOf()
    private val breakPointsText: MutableMap<Int, String> = mutableMapOf()

    private var nextContinuation: Continuation<Unit>? = null
    private var debugMode = true
    private var hasRun = false

    override fun load(node: AstNode) {
        stop()

        val visitNodeLambda: suspend () -> Unit = {
            node.accept(this)
        }

        nextContinuation = visitNodeLambda.createCoroutine(object : SimpleContinuation<Unit>() {
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
        return DebugVisitor(copyScope).evaluate(expression)
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

    suspend override fun visit(fileNode: FileNode): Int? {
        checkBreakPoint(fileNode)
        return super.visit(fileNode)
    }

    suspend override fun visit(blockNode: BlockNode): Int? {
        checkBreakPoint(blockNode)
        return super.visit(blockNode)
    }

    suspend override fun visit(printlnNode: PrintlnNode): Int? {
        checkBreakPoint(printlnNode)
        return super.visit(printlnNode)
    }

    suspend override fun visit(functionNode: FunctionNode): Int? {
        checkBreakPoint(functionNode)
        return super.visit(functionNode)
    }

    suspend override fun visit(variableNode: VariableNode): Int? {
        checkBreakPoint(variableNode)
        return super.visit(variableNode)
    }

    suspend override fun visit(whileNode: WhileNode): Int? {
        checkBreakPoint(whileNode)
        return super.visit(whileNode)
    }

    suspend override fun visit(ifNodeNode: IfNode): Int? {
        checkBreakPoint(ifNodeNode)
        return super.visit(ifNodeNode)
    }

    suspend override fun visit(assignmentNode: AssignmentNode): Int? {
        checkBreakPoint(assignmentNode)
        return super.visit(assignmentNode)
    }

    suspend override fun visit(returnNode: ReturnNode): Int? {
        return returnNode.value.accept(DebugExpressionExecVisitor())
    }

    suspend override fun visit(literalNode: LiteralNode): Int? {
        return literalNode.accept(DebugExpressionExecVisitor())
    }

    suspend override fun visit(binaryExpressionNode: BinaryExpressionNode): Int? {
        return binaryExpressionNode.accept(DebugExpressionExecVisitor())
    }

    suspend override fun visit(functionCallNode: FunctionCallNode): Int? {
        return functionCallNode.accept(DebugExpressionExecVisitor())
    }

    suspend override fun visit(varNode: VarNode): Int? {
        return varNode.accept(DebugExpressionExecVisitor())
    }

    inner class DebugExpressionExecVisitor : ExpressionExecVisitor() {
        suspend override fun visit(literalNode: LiteralNode): Int {
            checkBreakPoint(literalNode)
            return super.visit(literalNode)
        }

        suspend override fun visit(binaryExpressionNode: BinaryExpressionNode): Int {
            checkBreakPoint(binaryExpressionNode)
            return super.visit(binaryExpressionNode)
        }

        suspend override fun visit(functionCallNode: FunctionCallNode): Int {
            checkBreakPoint(functionCallNode)
            return super.visit(functionCallNode)
        }

        suspend override fun visit(varNode: VarNode): Int {
            checkBreakPoint(varNode)
            return super.visit(varNode)
        }
    }

    companion object {
        fun build() = DebugVisitor(Scope.empty())
    }

}