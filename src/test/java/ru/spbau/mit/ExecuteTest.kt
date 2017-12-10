package ru.spbau.mit

import org.junit.Test
import ru.spbau.mit.ast.*
import ru.spbau.mit.execution.AstExecVisitor
import ru.spbau.mit.execution.SimpleContinuation
import kotlin.coroutines.experimental.startCoroutine
import kotlin.test.assertEquals

class ExecuteTest {

    private val LINE = 0

    @Test
    fun executeFileNode() {
        val ast = FileNode(BlockNode(mutableListOf(PrintlnNode(mutableListOf(LiteralNode(1, LINE)), LINE)), LINE), LINE)
        assert(ast, "1\n")
    }

    @Test
    fun executeBlockNode() {
        val ast = BlockNode(mutableListOf(PrintlnNode(mutableListOf(LiteralNode(1, LINE)), LINE)), LINE)
        assert(ast, "1\n")
    }

    @Test
    fun executePrintlnNode() {
        val ast = PrintlnNode(mutableListOf(LiteralNode(1, LINE), BinaryExpressionNode(
                LiteralNode(1, LINE), "-", LiteralNode(3, LINE), LINE
        )), LINE)
        assert(ast, "1 -2\n")
    }

    @Test
    fun executeWhileNode() {
        val ast = BlockNode(mutableListOf(
                VariableNode("a", LiteralNode(LINE, LINE), LINE),
                WhileNode(BinaryExpressionNode(VarNode("a", LINE), "<", LiteralNode(3, LINE), LINE),
                        BlockNode(mutableListOf(
                                PrintlnNode(mutableListOf(VarNode("a", LINE)), LINE),
                                AssignmentNode("a", BinaryExpressionNode(VarNode("a", LINE), "+", LiteralNode(1, LINE), LINE), LINE)
                        )
                        , LINE), LINE)
        ), LINE)
        assert(ast, "0\n1\n2\n")
    }

    @Test
    fun executeIfNode() {
        val ast = BlockNode(mutableListOf(
                VariableNode("a", LiteralNode(LINE, LINE), LINE),
                IfNode(BinaryExpressionNode(VarNode("a", LINE), "<", LiteralNode(-1, LINE), LINE),
                        BlockNode(emptyList(), LINE),
                        BlockNode(mutableListOf(PrintlnNode(mutableListOf(VarNode("a", LINE)), LINE)), LINE),
                        LINE
                )
        ), LINE)
        assert(ast, "0\n")
    }

    @Test
    fun executeFunctionCallNode() {
        val ast = BlockNode(mutableListOf(
                FunctionNode("foo", mutableListOf("n"), BlockNode(mutableListOf(
                        PrintlnNode(mutableListOf(BinaryExpressionNode(VarNode("n", LINE), "*", VarNode("n", LINE), LINE)), LINE)
                ), LINE), LINE),
                FunctionCallNode("foo", mutableListOf(LiteralNode(5, LINE)), LINE)
        ), LINE)
        assert(ast, "25\n")
    }

    private fun assert(ast: AstNode, expectedOutput: String) {
        startEval(expectedOutput) {
            executeAst(ast)
        }
    }

    private fun startEval(expectedOutput: String, suspendLambda: suspend () -> String) {
        suspendLambda.startCoroutine(object : SimpleContinuation<String>() {
            override fun resume(value: String) {
                assertEquals(expectedOutput, value)
            }
        })
    }

    suspend fun executeAst(astNode: AstNode): String {
        val visitor = AstExecVisitor.build()
        astNode.accept(visitor)
        return visitor.output
    }



}