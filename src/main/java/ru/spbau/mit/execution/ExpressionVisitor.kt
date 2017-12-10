package ru.spbau.mit.execution

import ru.spbau.mit.ast.BinaryExpressionNode
import ru.spbau.mit.ast.FunctionCallNode
import ru.spbau.mit.ast.LiteralNode
import ru.spbau.mit.ast.VarNode

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
interface ExpressionVisitor<T> {

    suspend fun visit(literalNode: LiteralNode): T

    suspend fun visit(binaryExpressionNode: BinaryExpressionNode): T

    suspend fun visit(functionCallNode: FunctionCallNode): T

    suspend fun visit(varNode: VarNode): T
}