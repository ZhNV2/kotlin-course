package ru.spbau.mit

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.spbau.mit.ast.*
import ru.spbau.mit.parser.FunLexer
import ru.spbau.mit.parser.FunParser

class AstTest {

    @Test
    fun testVisitFile() {
        val parsed = visit(
                """
                    |
                    """.trimMargin()
        )
        val actual = AstBuilder().visitFile(parsed.file())
        val expected = FileNode(BlockNode(emptyList()))
        assertEquals(actual, expected)
    }

    @Test
    fun testVisitBlock() {
        val parsed = visit(
                """
                    |var a = 1
                    """.trimMargin()
        )
        val actual = AstBuilder().visitBlock(parsed.block())
        val expected = BlockNode(mutableListOf(VariableNode("a", LiteralNode(1))))
        assertEquals(actual, expected)
    }

    @Test
    fun testVisitPrintln() {
        val parsed = visit(
                """
                    |println(2, a)
                    """.trimMargin()
        )
        val actual = AstBuilder().visitPrintln(parsed.println())
        val expected = PrintlnNode(mutableListOf(LiteralNode(2), VarNode("a")))
        assertEquals(actual, expected)
    }

    @Test
    fun testVisitFunction() {
        val parsed = visit(
                """
                    |fun foo(x) {
                    |}
                    """.trimMargin()
        )
        val actual = AstBuilder().visitFunction(parsed.function())
        val expected = FunctionNode("foo", mutableListOf("x"), BlockNode(emptyList()))
        assertEquals(actual, expected)
    }

    @Test
    fun testVisitVariable() {
        val parsed = visit(
                """
                    |var a = foo(x)
                    """.trimMargin()
        )
        val actual = AstBuilder().visitVariable(parsed.variable())
        val expected = VariableNode("a", FunctionCallNode("foo", mutableListOf(VarNode("x"))))
        assertEquals(actual, expected)
    }

    @Test
    fun testVisitWhile() {
        val parsed = visit(
                """
                    |while (x) {}
                    """.trimMargin()
        )
        val actual = AstBuilder().visitWhileT(parsed.whileT())
        val expected = WhileNode(VarNode("x"), BlockNode(emptyList()))
        assertEquals(actual, expected)
    }

    @Test
    fun testVisitIf() {
        val parsed = visit(
                """
                    |if (x) {} else {}
                    """.trimMargin()
        )
        val actual = AstBuilder().visitIfT(parsed.ifT())
        val expected = IfNode(VarNode("x"), BlockNode(emptyList()), BlockNode(emptyList()))
        assertEquals(actual, expected)
    }

    @Test
    fun testVisitAssignment() {
        val parsed = visit(
                """
                    |a = 1
                    """.trimMargin()
        )
        val actual = AstBuilder().visitAssignment(parsed.assignment())
        val expected = AssignmentNode("a", LiteralNode(1))
        assertEquals(actual, expected)
    }

    @Test
    fun testVisitReturn() {
        val parsed = visit(
                """
                    |return n + 1
                    """.trimMargin()
        )
        val actual = AstBuilder().visitReturnT(parsed.returnT())
        val expected = ReturnNode(BinaryExpressionNode(VarNode("n"), "+", LiteralNode(1)))
        assertEquals(actual, expected)
    }

    @Test
    fun testVisitVar() {
        val parsed = visit(
                """
                    |at
                    """.trimMargin()
        )
        val actual = AstBuilder().visitVar(parsed.`var`())
        val expected = VarNode("at")
        assertEquals(actual, expected)
    }

    @Test
    fun testVisitFunctionCall() {
        val parsed = visit(
                """
                    |foo(bar(1))
                    """.trimMargin()
        )
        val actual = AstBuilder().visitFunctionCall(parsed.functionCall())
        val expected = FunctionCallNode("foo", mutableListOf(FunctionCallNode("bar", mutableListOf(LiteralNode(1)))))
        assertEquals(actual, expected)
    }

    @Test
    fun testVisitLiteral() {
        val parsed = visit(
                """
                    |10
                    """.trimMargin()
        )
        val actual = AstBuilder().visitLiteral(parsed.literal())
        val expected = LiteralNode(10)
        assertEquals(actual, expected)
    }

    private fun visit(input: String): FunParser {
        return FunParser(BufferedTokenStream(FunLexer(CharStreams.fromString(input))))
    }

}