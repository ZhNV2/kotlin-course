package ru.spbau.mit

import org.junit.Test
import ru.spbau.mit.ast.*
import ru.spbau.mit.exception.AlreadyFinishedException
import ru.spbau.mit.exception.AlreadyRunException
import ru.spbau.mit.execution.DebugVisitor
import ru.spbau.mit.execution.Debugger
import ru.spbau.mit.execution.SimpleContinuation
import kotlin.coroutines.experimental.startCoroutine
import kotlin.test.assertEquals

class DebuggerTest {

    private val RESOURCES_PATH = "src/test/resources/funSamples"
    private val LINE = 1

    @Test
    fun breakPointTest() {
        val debugger: Debugger = DebugVisitor.build()
        debugger.load(buildAst("fibonacci"))
        debugger.breakPoint(11)
        debugger.run()
        assertEquals("1 1\n", debugger.lastRunOutput())
    }

    @Test
    fun conditionTrueTest() {
        val debugger: Debugger = DebugVisitor.build()
        debugger.load(buildAst("fibonacci"))
        debugger.condition(
                11,
                BinaryExpressionNode(VarNode("i", LINE), "<", LiteralNode(2, LINE), LINE),
                "i < 2"
        )
        debugger.run()
        assertEquals("1 1\n", debugger.lastRunOutput())
    }

    @Test
    fun conditionFalseTest() {
        val debugger: Debugger = DebugVisitor.build()
        debugger.load(buildAst("fibonacci"))
        debugger.condition(
                11,
                BinaryExpressionNode(VarNode("i", LINE), "<", LiteralNode(1, LINE), LINE),
                "i < 1"
        )
        debugger.run()
        assertEquals("1 1\n2 2\n3 3\n4 5\n5 8\n", debugger.lastRunOutput())
    }

    @Test(expected = AlreadyRunException::class)
    fun reRunTest() {
        val debugger: Debugger = DebugVisitor.build()
        debugger.load(buildAst("fibonacci"))
        debugger.breakPoint(11)
        debugger.run()
        debugger.run()
    }

    @Test(expected = AlreadyFinishedException::class)
    fun continueAfterEndTest() {
        val debugger: Debugger = DebugVisitor.build()
        debugger.load(buildAst("fibonacci"))
        debugger.run()
        debugger.continueDebug()
    }

    @Test
    fun removeBreakPointTest() {
        val debugger: Debugger = DebugVisitor.build()
        debugger.load(buildAst("fibonacci"))
        debugger.breakPoint(11)
        debugger.remove(11)
        debugger.run()
        assertEquals("1 1\n2 2\n3 3\n4 5\n5 8\n", debugger.lastRunOutput())
    }

    @Test(expected = AlreadyFinishedException::class)
    fun stopTest() {
        val debugger: Debugger = DebugVisitor.build()
        debugger.load(buildAst("fibonacci"))
        debugger.breakPoint(11)
        debugger.run()
        debugger.stop()
        debugger.continueDebug()
    }

    @Test
    fun evaluateTest() {
        val debugger: Debugger = DebugVisitor.build()
        debugger.load(buildAst("fibonacci"))
        debugger.breakPoint(11)
        debugger.run()
        debugger.continueDebug()
        assertExpressionValue(4) {
            debugger.evaluateIndependently(BinaryExpressionNode(
                    VarNode("i", LINE),
                    "*",
                    LiteralNode(2, LINE),
                    LINE
            ))
        }
    }

    private fun assertExpressionValue(expectedValue: Int, suspendLambda: suspend () -> Int) {
        suspendLambda.startCoroutine(object : SimpleContinuation<Int>() {
            override fun resume(value: Int) {
                assertEquals(expectedValue, value)
            }
        })
    }

    @Test
    fun evaluateDoesNotAffectBasicFlow() {
        val debugger: Debugger = DebugVisitor.build()
        debugger.load(buildAst("global_variable"))
        debugger.breakPoint(7)
        debugger.run()
        assertExpressionValue(4) {
            debugger.evaluateIndependently(FunctionCallNode(
                    "foo",
                    mutableListOf(LiteralNode(3, LINE)),
                    LINE
            ))
        }
        debugger.continueDebug()
        assertEquals("3\n5\n7\n", debugger.lastRunOutput())
    }

    private fun buildAst(file: String): AstNode {
        return buildAstFromFile("$RESOURCES_PATH/$file.txt")
    }

    @Test
    fun evaluateDoesNotStopOnBreakPoint() {
        val debugger: Debugger = DebugVisitor.build()
        debugger.load(buildAst("fibonacci"))
        debugger.breakPoint(2)
        debugger.breakPoint(11)
        debugger.run()
        debugger.continueDebug()
        assertExpressionValue(1) {
            debugger.evaluateIndependently(FunctionCallNode(
                    "fib",
                    mutableListOf(LiteralNode(1, LINE)),
                    LINE
            ))
        }
    }
}