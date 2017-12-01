package ru.spbau.mit.ast

interface ExpressionVisitor<T> {

    fun visit(literalNode: LiteralNode): T

    fun visit(binaryExpressionNode: BinaryExpressionNode): T

    fun visit(functionCallNode: FunctionCallNode): T

    fun visit(varNode: VarNode): T
}