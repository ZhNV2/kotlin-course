package ru.spbau.mit.ast

interface AstNode {
    fun <T> accept(visitor: AstVisitor<T>)
}

interface StatementNode : AstNode

interface ExpressionNode : StatementNode

data class FileNode(val block: AstNode) : AstNode {
    override fun <T> accept(visitor: AstVisitor<T>) {
        return visitor.visit(this)
    }
}

data class BlockNode(val statements: List<AstNode>) : AstNode {
    override fun <T> accept(visitor: AstVisitor<T>) {
        return visitor.visit(this)
    }
}

data class PrintlnNode(val args: List<AstNode>): StatementNode {
    override fun <T> accept(visitor: AstVisitor<T>) {
        return visitor.visit(this)
    }
}

data class FunctionNode(val name: AstNode,
                        val argNames: List<AstNode>,
                        val body: AstNode) : StatementNode {
    override fun <T> accept(visitor: AstVisitor<T>) {
        return visitor.visit(this)
    }
}

data class VariableNode(val varName: AstNode,
                        val varValue: AstNode) : StatementNode {
    override fun <T> accept(visitor: AstVisitor<T>) {
        return visitor.visit(this)
    }
}

data class WhileNode(val condition: AstNode,
                     val body: AstNode) : StatementNode {
    override fun <T> accept(visitor: AstVisitor<T>) {
        return visitor.visit(this)
    }
}

data class IfNode(val condition: AstNode,
                  val ifBody: AstNode,
                  val elseBody: AstNode?) : StatementNode {
    override fun <T> accept(visitor: AstVisitor<T>) {
        return visitor.visit(this)
    }
}

data class AssignmentNode(val varName: AstNode,
                          val newValue: AstNode) : StatementNode {
    override fun <T> accept(visitor: AstVisitor<T>) {
        return visitor.visit(this)
    }
}

data class ReturnNode(val value: AstNode) : StatementNode {
    override fun <T> accept(visitor: AstVisitor<T>) {
        return visitor.visit(this)
    }
}

data class LiteralNode(val value: Int) : ExpressionNode {
    override fun <T> accept(visitor: AstVisitor<T>) {
        return visitor.visit(this)
    }
}

data class IdentifierNode(val name: String) : ExpressionNode {
    override fun <T> accept(visitor: AstVisitor<T>) {
        return visitor.visit(this)
    }
}

data class BinaryExpressionNode(val left: AstNode,
                                val op: String,
                                val right: AstNode) : ExpressionNode{
    override fun <T> accept(visitor: AstVisitor<T>) {
        return visitor.visit(this)
    }
}

data class FunctionCallNode(val name: AstNode,
                            val args: List<AstNode>) : ExpressionNode{
    override fun <T> accept(visitor: AstVisitor<T>) {
        return visitor.visit(this)
    }

}