package ru.spbau.mit.execution

import ru.spbau.mit.ast.*
import ru.spbau.mit.exception.IllegalNumberOfArguments

class AstExecVisitor private constructor(private var scope: Scope) : AstVisitor<Int?> {

    private val outputBuilder = StringBuilder()

    val output
        get() = outputBuilder.toString()

    override fun visit(fileNode: FileNode): Int? {
        return fileNode.block.accept(this)
    }

    override fun visit(blockNode: BlockNode): Int? {
        scope = Scope.from(scope)
        for (statement in blockNode.statements) {
            statement.accept(this)?.let {
                return it
            }
        }
        scope = scope.prev
        return null
    }

    override fun visit(printlnNode: PrintlnNode): Int? {
        outputBuilder.append(printlnNode.args.map { it.accept(this) }.joinToString(" "))
        outputBuilder.append("\n")
        return null
    }

    override fun visit(functionNode: FunctionNode): Int? {
        val funcName = functionNode.name
        val argNames = functionNode.argNames
        scope.putFunc(funcName, Function(scope, functionNode.body, argNames))
        return null
    }


    override fun visit(variableNode: VariableNode): Int? {
        val value = variableNode.varValue.accept(this) as Int
        scope.putVar(variableNode.varName, value, false)
        return null
    }

    override fun visit(whileNode: WhileNode): Int? {
        while (checkCondition(whileNode.condition)) {
            whileNode.body.accept(this)?.let {
                return it
            }
        }
        return null
    }

    private fun checkCondition(expressionNode: ExpressionNode): Boolean {
        val value = expressionNode.accept(this) as Int
        return value != 0
    }

    override fun visit(ifNodeNode: IfNode): Int? {
        if (checkCondition(ifNodeNode.condition)) {
            return ifNodeNode.ifBody.accept(this)
        } else if (ifNodeNode.elseBody != null) {
            return ifNodeNode.elseBody.accept(this)
        }
        return null
    }

    override fun visit(assignmentNode: AssignmentNode): Int? {
        val newValue = assignmentNode.newValue.accept(this) as Int
        scope.putVar(assignmentNode.varName, newValue, true)
        return null
    }

    override fun visit(returnNode: ReturnNode): Int? {
        return returnNode.value.accept(ExpressionExecVisitor())
    }

    override fun visit(literalNode: LiteralNode): Int? {
        return literalNode.accept(ExpressionExecVisitor())
    }

    override fun visit(binaryExpressionNode: BinaryExpressionNode): Int? {
        return binaryExpressionNode.accept(ExpressionExecVisitor())
    }

    override fun visit(functionCallNode: FunctionCallNode): Int? {
        return functionCallNode.accept(ExpressionExecVisitor())
    }

    override fun visit(varNode: VarNode): Int? {
        return varNode.accept(ExpressionExecVisitor())
    }

    inner class ExpressionExecVisitor : ExpressionVisitor<Int> {
        override fun visit(literalNode: LiteralNode): Int {
            return literalNode.value
        }

        override fun visit(binaryExpressionNode: BinaryExpressionNode): Int {
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

        private fun toInt(boolean: Boolean): Int {
            return if (boolean) 1 else 0
        }

        private fun toBoolean(int: Int): Boolean {
            return int != 0
        }

        override fun visit(functionCallNode: FunctionCallNode): Int {
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

        override fun visit(varNode: VarNode): Int {
            return scope.getVar(varNode.name)
        }

    }

    companion object {
        fun build() = AstExecVisitor(Scope.empty())
    }

}