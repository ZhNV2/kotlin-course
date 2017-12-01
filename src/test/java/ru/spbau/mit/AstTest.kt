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
    fun testBuildFile() {
        val parsed = build(
                """
                    |
                    """.trimMargin()
        )
        val actual = AstBuilder().buildFile(parsed.file())
        val expected = FileNode(BlockNode(emptyList()))
        assertEquals(actual, expected)
    }

    @Test
    fun testBuildBlock() {
        val parsed = build(
                """
                    |var a = 1
                    """.trimMargin()
        )
        val actual = AstBuilder().buildBlock(parsed.block())
        val expected = BlockNode(mutableListOf(VariableNode("a", LiteralNode(1))))
        assertEquals(actual, expected)
    }

    @Test
    fun testBuildPrintln() {
        val parsed = build(
                """
                    |println(2, a)
                    """.trimMargin()
        )
        val actual = AstBuilder().buildPrintln(parsed.println())
        val expected = PrintlnNode(mutableListOf(LiteralNode(2), VarNode("a")))
        assertEquals(actual, expected)
    }

    @Test
    fun testBuildFunction() {
        val parsed = build(
                """
                    |fun foo(x) {
                    |}
                    """.trimMargin()
        )
        val actual = AstBuilder().buildFunction(parsed.function())
        val expected = FunctionNode("foo", mutableListOf("x"), BlockNode(emptyList()))
        assertEquals(actual, expected)
    }

    @Test
    fun testBuildVariable() {
        val parsed = build(
                """
                    |var a = foo(x)
                    """.trimMargin()
        )
        val actual = AstBuilder().buildVariable(parsed.variable())
        val expected = VariableNode("a", FunctionCallNode("foo", mutableListOf(VarNode("x"))))
        assertEquals(actual, expected)
    }

    @Test
    fun testBuildWhile() {
        val parsed = build(
                """
                    |while (x) {}
                    """.trimMargin()
        )
        val actual = AstBuilder().buildWhileT(parsed.whileT())
        val expected = WhileNode(VarNode("x"), BlockNode(emptyList()))
        assertEquals(actual, expected)
    }

    @Test
    fun testBuildIf() {
        val parsed = build(
                """
                    |if (x) {} else {}
                    """.trimMargin()
        )
        val actual = AstBuilder().buildIfT(parsed.ifT())
        val expected = IfNode(VarNode("x"), BlockNode(emptyList()), BlockNode(emptyList()))
        assertEquals(actual, expected)
    }

    @Test
    fun testBuildAssignment() {
        val parsed = build(
                """
                    |a = 1
                    """.trimMargin()
        )
        val actual = AstBuilder().buildAssignment(parsed.assignment())
        val expected = AssignmentNode("a", LiteralNode(1))
        assertEquals(actual, expected)
    }

    @Test
    fun testBuildReturn() {
        val parsed = build(
                """
                    |return n + 1
                    """.trimMargin()
        )
        val actual = AstBuilder().buildReturnT(parsed.returnT())
        val expected = ReturnNode(BinaryExpressionNode(VarNode("n"), "+", LiteralNode(1)))
        assertEquals(actual, expected)
    }

    @Test
    fun testBuildVar() {
        val parsed = build(
                """
                    |at
                    """.trimMargin()
        )
        val actual = AstBuilder().buildVar(parsed.`var`())
        val expected = VarNode("at")
        assertEquals(actual, expected)
    }

    @Test
    fun testBuildFunctionCall() {
        val parsed = build(
                """
                    |foo(bar(1))
                    """.trimMargin()
        )
        val actual = AstBuilder().buildFunctionCall(parsed.functionCall())
        val expected = FunctionCallNode("foo", mutableListOf(FunctionCallNode("bar", mutableListOf(LiteralNode(1)))))
        assertEquals(actual, expected)
    }

    @Test
    fun testBuildLiteral() {
        val parsed = build(
                """
                    |10
                    """.trimMargin()
        )
        val actual = AstBuilder().buildLiteral(parsed.literal())
        val expected = LiteralNode(10)
        assertEquals(actual, expected)
    }

    private fun build(input: String): FunParser {
        return FunParser(BufferedTokenStream(FunLexer(CharStreams.fromString(input))))
    }

}