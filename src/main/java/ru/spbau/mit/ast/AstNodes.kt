package ru.spbau.mit.ast

import ru.spbau.mit.execution.ExpressionVisitor

interface AstNode {
    fun <T> accept(visitor: AstVisitor<T>): T
}

interface StatementNode : AstNode

interface ExpressionNode : StatementNode {

    fun <T> accept(visitor: ExpressionVisitor<T>): T

}

data class FileNode(val block: BlockNode) : AstNode {

    override fun <T> accept(visitor: AstVisitor<T>): T {
        return visitor.visit(this)
    }

}

data class BlockNode(val statements: List<StatementNode>) : AstNode {

    override fun <T> accept(visitor: AstVisitor<T>): T {
        return visitor.visit(this)
    }

}

data class PrintlnNode(val args: List<ExpressionNode>) : StatementNode {

    override fun <T> accept(visitor: AstVisitor<T>): T {
        return visitor.visit(this)
    }

}

data class FunctionNode(val name: String,
                        val argNames: List<String>,
                        val body: BlockNode) : StatementNode {

    override fun <T> accept(visitor: AstVisitor<T>): T {
        return visitor.visit(this)
    }

}

data class VariableNode(val varName: String,
                        val varValue: ExpressionNode) : StatementNode {

    override fun <T> accept(visitor: AstVisitor<T>): T {
        return visitor.visit(this)
    }

}

data class WhileNode(val condition: ExpressionNode,
                     val body: BlockNode) : StatementNode {

    override fun <T> accept(visitor: AstVisitor<T>): T {
        return visitor.visit(this)
    }

}

data class IfNode(val condition: ExpressionNode,
                  val ifBody: BlockNode,
                  val elseBody: BlockNode?) : StatementNode {

    override fun <T> accept(visitor: AstVisitor<T>): T {
        return visitor.visit(this)
    }

}

data class AssignmentNode(val varName: String,
                          val newValue: ExpressionNode) : StatementNode {

    override fun <T> accept(visitor: AstVisitor<T>): T {
        return visitor.visit(this)
    }

}

data class ReturnNode(val value: ExpressionNode) : StatementNode {

    override fun <T> accept(visitor: AstVisitor<T>): T {
        return visitor.visit(this)
    }

}

data class VarNode(val name: String) : ExpressionNode {

    override fun <T> accept(visitor: AstVisitor<T>): T {
        return visitor.visit(this)
    }

    override fun <T> accept(visitor: ExpressionVisitor<T>): T {
        return visitor.visit(this)
    }

}

data class LiteralNode(val value: Int) : ExpressionNode {

    override fun <T> accept(visitor: AstVisitor<T>): T {
        return visitor.visit(this)
    }

    override fun <T> accept(visitor: ExpressionVisitor<T>): T {
        return visitor.visit(this)
    }

}

data class BinaryExpressionNode(val left: ExpressionNode,
                                val op: String,
                                val right: ExpressionNode) : ExpressionNode {

    override fun <T> accept(visitor: AstVisitor<T>): T {
        return visitor.visit(this)
    }

    override fun <T> accept(visitor: ExpressionVisitor<T>): T {
        return visitor.visit(this)
    }

}

data class FunctionCallNode(val name: String,
                            val args: List<ExpressionNode>) : ExpressionNode {

    override fun <T> accept(visitor: AstVisitor<T>): T {
        return visitor.visit(this)
    }

    override fun <T> accept(visitor: ExpressionVisitor<T>): T {
        return visitor.visit(this)
    }

}