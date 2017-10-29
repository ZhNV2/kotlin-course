package ru.spbau.mit.ast

interface AstVisitor<T> {

    fun visit(fileNode: FileNode): T

    fun visit(blockNode: BlockNode): T

    fun visit(printlnNode: PrintlnNode): T

    fun visit(functionNode: FunctionNode): T

    fun visit(variableNode: VariableNode): T

    fun visit(whileNode: WhileNode): T

    fun visit(ifNodeNode: IfNode): T

    fun visit(assignmentNode: AssignmentNode): T

    fun visit(returnNode: ReturnNode): T

    fun visit(varNode: VarNode): T

    fun visit(literalNode: LiteralNode): T

    fun visit(binaryExpressionNode: BinaryExpressionNode): T

    fun visit(functionCallNode: FunctionCallNode): T

}