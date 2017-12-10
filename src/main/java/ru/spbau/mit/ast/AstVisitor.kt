package ru.spbau.mit.ast

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
interface AstVisitor<T> {

    suspend fun visit(fileNode: FileNode): T

    suspend fun visit(blockNode: BlockNode): T

    suspend fun visit(printlnNode: PrintlnNode): T

    suspend fun visit(functionNode: FunctionNode): T

    suspend fun visit(variableNode: VariableNode): T

    suspend fun visit(whileNode: WhileNode): T

    suspend fun visit(ifNodeNode: IfNode): T

    suspend fun visit(assignmentNode: AssignmentNode): T

    suspend fun visit(returnNode: ReturnNode): T

    suspend fun visit(varNode: VarNode): T

    suspend fun visit(literalNode: LiteralNode): T

    suspend fun visit(binaryExpressionNode: BinaryExpressionNode): T

    suspend fun visit(functionCallNode: FunctionCallNode): T

}