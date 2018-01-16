package ru.spbau.mit.execution

import ru.spbau.mit.ast.*
import ru.spbau.mit.exception.IllegalNumberOfArguments

open class AstExecVisitor protected constructor(protected var scope: Scope) : AstVisitor<Int?> {

    protected var outputBuilder = StringBuilder()

    val output
        get() = outputBuilder.toString()

    override suspend fun visit(fileNode: FileNode): Int? {
        return fileNode.block.accept(this)
    }

    override suspend fun visit(blockNode: BlockNode): Int? {
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
        val out = printlnNode.args.map { it.accept(this) }.joinToString(" ")
        println(out)
        outputBuilder.append(out).append("\n")
        return null
    }

    override suspend fun visit(functionNode: FunctionNode): Int? {
        val funcName = functionNode.name
        val argNames = functionNode.argNames
        scope.putFunc(funcName, Function(scope, functionNode.body, argNames))
        return null
    }


    override suspend fun visit(variableNode: VariableNode): Int? {
        val value = variableNode.varValue.accept(this) as Int
        scope.putVar(variableNode.varName, value, false)
        return null
    }

    override suspend fun visit(whileNode: WhileNode): Int? {
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
        if (checkCondition(ifNodeNode.condition)) {
            return ifNodeNode.ifBody.accept(this)
        } else if (ifNodeNode.elseBody != null) {
            return ifNodeNode.elseBody.accept(this)
        }
        return null
    }

    override suspend fun visit(assignmentNode: AssignmentNode): Int? {
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

    open inner class ExpressionExecVisitor : ExpressionVisitor<Int> {
        override suspend fun visit(literalNode: LiteralNode): Int {
            return literalNode.value
        }

        override suspend fun visit(binaryExpressionNode: BinaryExpressionNode): Int {
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
            return scope.getVar(varNode.name)
        }

    }

    private fun toInt(boolean: Boolean): Int {
        return if (boolean) 1 else 0
    }

    protected fun toBoolean(int: Int): Boolean {
        return int != 0
    }

    companion object {
        fun build() = AstExecVisitor(Scope.empty())
    }

}