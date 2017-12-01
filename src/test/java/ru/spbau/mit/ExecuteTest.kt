package ru.spbau.mit

import org.junit.Test
import ru.spbau.mit.ast.*
import ru.spbau.mit.execution.AstExecVisitor
import kotlin.test.assertEquals

class ExecuteTest {

    @Test
    fun executeFileNode() {
        val ast = FileNode(BlockNode(mutableListOf(PrintlnNode(mutableListOf(LiteralNode(1))))))
        assert(ast, "1\n")
    }

    @Test
    fun executeBlockNode() {
        val ast = BlockNode(mutableListOf(PrintlnNode(mutableListOf(LiteralNode(1)))))
        assert(ast, "1\n")
    }

    @Test
    fun executePrintlnNode() {
        val ast = PrintlnNode(mutableListOf(LiteralNode(1), BinaryExpressionNode(
                LiteralNode(1), "-", LiteralNode(3)
        )))
        assert(ast, "1 -2\n")
    }

    @Test
    fun executeWhileNode() {
        val ast = BlockNode(mutableListOf(
                VariableNode("a", LiteralNode(0)),
                WhileNode(BinaryExpressionNode(VarNode("a"), "<", LiteralNode(3)),
                        BlockNode(mutableListOf(
                                PrintlnNode(mutableListOf(VarNode("a"))),
                                AssignmentNode("a", BinaryExpressionNode(VarNode("a"), "+", LiteralNode(1)))
                        )
                        ))
        )
        )
        assert(ast, "0\n1\n2\n")
    }

    @Test
    fun executeIfNode() {
        val ast = BlockNode(mutableListOf(
                VariableNode("a", LiteralNode(0)),
                IfNode(BinaryExpressionNode(VarNode("a"), "<", LiteralNode(-1)),
                        BlockNode(emptyList()),
                        BlockNode(mutableListOf(PrintlnNode(mutableListOf(VarNode("a")))))
                )
        ))
        assert(ast, "0\n")
    }

    @Test
    fun executeFunctionCallNode() {
        val ast = BlockNode(mutableListOf(
                FunctionNode("foo", mutableListOf("n"), BlockNode(mutableListOf(
                        PrintlnNode(mutableListOf(BinaryExpressionNode(VarNode("n"), "*", VarNode("n"))))
                ))),
                FunctionCallNode("foo", mutableListOf(LiteralNode(5)))
        ))
        assert(ast, "25\n")
    }

    private fun assert(ast: AstNode, expected: String) {
        val visitor = AstExecVisitor.build()
        ast.accept(visitor)
        assertEquals(expected, visitor.getOutput())
    }
}