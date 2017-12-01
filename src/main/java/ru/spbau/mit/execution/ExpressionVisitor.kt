package ru.spbau.mit.execution

import ru.spbau.mit.ast.BinaryExpressionNode
import ru.spbau.mit.ast.FunctionCallNode
import ru.spbau.mit.ast.LiteralNode
import ru.spbau.mit.ast.VarNode

interface ExpressionVisitor<T> {

    fun visit(literalNode: LiteralNode): T

    fun visit(binaryExpressionNode: BinaryExpressionNode): T

    fun visit(functionCallNode: FunctionCallNode): T

    fun visit(varNode: VarNode): T
}