@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

package ru.spbau.mit.ast

import ru.spbau.mit.execution.ExpressionVisitor

interface AstNode {

    suspend fun <T> accept(visitor: AstVisitor<T>): T

    val line: Int

}

interface StatementNode : AstNode

interface ExpressionNode : StatementNode {

    suspend fun <T> accept(visitor: ExpressionVisitor<T>): T

}


data class FileNode(val block: BlockNode, override val line: Int) : AstNode {

    override suspend fun <T> accept(visitor: AstVisitor<T>): T {
        return visitor.visit(this)
    }

}

data class BlockNode(
        val statements: List<StatementNode>,
        override val line: Int
) : AstNode {

    override suspend fun <T> accept(visitor: AstVisitor<T>): T {
        return visitor.visit(this)
    }

}

data class PrintlnNode(
        val args: List<ExpressionNode>,
        override val line: Int
) : StatementNode {

    override suspend fun <T> accept(visitor: AstVisitor<T>): T {
        return visitor.visit(this)
    }

}

data class FunctionNode(
        val name: String,
        val argNames: List<String>,
        val body: BlockNode,
        override val line: Int
) : StatementNode {

    override suspend fun <T> accept(visitor: AstVisitor<T>): T {
        return visitor.visit(this)
    }

}

data class VariableNode(
        val varName: String,
        val varValue: ExpressionNode,
        override val line: Int
) : StatementNode {

    override suspend fun <T> accept(visitor: AstVisitor<T>): T {
        return visitor.visit(this)
    }

}

data class WhileNode(
        val condition: ExpressionNode,
        val body: BlockNode,
        override val line: Int
) : StatementNode {

    override suspend fun <T> accept(visitor: AstVisitor<T>): T {
        return visitor.visit(this)
    }

}

data class IfNode(
        val condition: ExpressionNode,
        val ifBody: BlockNode,
        val elseBody: BlockNode?,
        override val line: Int
) : StatementNode {

    override suspend fun <T> accept(visitor: AstVisitor<T>): T {
        return visitor.visit(this)
    }

}

data class AssignmentNode(
        val varName: String,
        val newValue: ExpressionNode,
        override val line: Int
) : StatementNode {

    override suspend fun <T> accept(visitor: AstVisitor<T>): T {
        return visitor.visit(this)
    }

}

data class ReturnNode(val value: ExpressionNode, override val line: Int) : StatementNode {

    override suspend fun <T> accept(visitor: AstVisitor<T>): T {
        return visitor.visit(this)
    }

}

data class VarNode(val name: String, override val line: Int) : ExpressionNode {

    override suspend fun <T> accept(visitor: AstVisitor<T>): T {
        return visitor.visit(this)
    }

    override suspend fun <T> accept(visitor: ExpressionVisitor<T>): T {
        return visitor.visit(this)
    }

}

data class LiteralNode(val value: Int, override val line: Int) : ExpressionNode {

    override suspend fun <T> accept(visitor: AstVisitor<T>): T {
        return visitor.visit(this)
    }

    override suspend fun <T> accept(visitor: ExpressionVisitor<T>): T {
        return visitor.visit(this)
    }

}

data class BinaryExpressionNode(
        val left: ExpressionNode,
        val op: String,
        val right: ExpressionNode,
        override val line: Int
) : ExpressionNode {

    override suspend fun <T> accept(visitor: AstVisitor<T>): T {
        return visitor.visit(this)
    }

    override suspend fun <T> accept(visitor: ExpressionVisitor<T>): T {
        return visitor.visit(this)
    }

}

data class FunctionCallNode(
        val name: String,
        val args: List<ExpressionNode>,
        override val line: Int
) : ExpressionNode {

    override suspend fun <T> accept(visitor: AstVisitor<T>): T {
        return visitor.visit(this)
    }

    override suspend fun <T> accept(visitor: ExpressionVisitor<T>): T {
        return visitor.visit(this)
    }

}