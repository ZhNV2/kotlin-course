package ru.spbau.mit.ast

import ru.spbau.mit.ExecutionContext
import ru.spbau.mit.Function
import ru.spbau.mit.exception.IllegalNumberOfArguments

class AstExecVisitor private constructor(private val execCtx: ExecutionContext) : AstVisitor<Int?> {

    private val output = StringBuilder()

    fun getOutput() = output.toString()

    override fun visit(fileNode: FileNode): Int? {
        return fileNode.block.accept(this)
    }

    override fun visit(blockNode: BlockNode): Int? {
        execCtx.addRuntimeContext()
        for (statement in blockNode.statements) {
            val res = statement.accept(this)
            if (res != null) {
                return res
            }
        }
        execCtx.removeRuntimeContext()
        return null
    }

    override fun visit(printlnNode: PrintlnNode): Int? {
        output.append(printlnNode.args.map { it.accept(this) }.joinToString(" "))
        output.append("\n")
        return null
    }

    override fun visit(functionNode: FunctionNode): Int? {
        val funcName = functionNode.name
        val argNames = functionNode.argNames
        execCtx.putFunc(funcName, Function(execCtx.copy(), functionNode.body, argNames))
        return null
    }


    override fun visit(variableNode: VariableNode): Int? {
        val value = variableNode.varValue.accept(this) as Int
        execCtx.putVar(variableNode.varName, value, false)
        return null
    }

    override fun visit(whileNode: WhileNode): Int? {
        while (checkCondition(whileNode.condition)) {
            val res = whileNode.body.accept(this)
            if (res != null) {
                return res
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
            return ifNodeNode.ifBody.accept(this);
        } else if (ifNodeNode.elseBody != null) {
            return ifNodeNode.elseBody.accept(this);
        }
        return null;
    }

    override fun visit(assignmentNode: AssignmentNode): Int? {
        val newValue = assignmentNode.newValue.accept(this) as Int
        execCtx.putVar(assignmentNode.varName, newValue, true)
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

    inner class ExpressionExecVisitor: ExpressionVisitor<Int> {
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
            val func = execCtx.getFunc(functionCallNode.name)
            val args = functionCallNode.args.map { it.accept(this) }
            if (func.argNames.size != args.size) {
                throw IllegalNumberOfArguments("${functionCallNode.name} was called with invalid number of params")
            }
            val funcCtx = func.execCtx.copy()
            funcCtx.addRuntimeContext()
            for (i in 0 until func.argNames.size) {
                funcCtx.putVar(func.argNames[i], args[i], false)
            }
            val execVisitor = AstExecVisitor(funcCtx)
            val res = func.block.accept(execVisitor) ?: 0
            output.append(execVisitor.output)
            return res
        }

        override fun visit(varNode: VarNode): Int {
            return execCtx.getVar(varNode.name)
        }


    }

    companion object {
        fun build() = AstExecVisitor(ExecutionContext.empty())
    }

}