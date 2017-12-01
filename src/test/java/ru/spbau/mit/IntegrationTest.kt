package ru.spbau.mit

import org.junit.Test
import ru.spbau.mit.exception.DoubleDefinitionException
import ru.spbau.mit.exception.FuncIsNotInScopeException
import ru.spbau.mit.exception.IllegalNumberOfArguments
import ru.spbau.mit.exception.VarIsNotInScopeException
import kotlin.test.assertEquals

class IntegrationTest {

    private val RESOURCES_PATH = "src/test/resources/funSamples"

    @Test
    fun testSimpleIf() {
        assert("simple_if", "8\n1\n")
    }

    @Test
    fun testFibonacci() {
        assert("fibonacci", "1 1\n" +
                "2 2\n" +
                "3 3\n" +
                "4 5\n" +
                "5 8\n"
        )
    }

    @Test
    fun testFunInFun() {
        assert("fun_in_fun", "42\n")
    }

    @Test
    fun testReverse() {
        assert("reverse", "2\n" +
                "3\n" +
                "5\n" +
                "4\n"
        )
    }

    @Test
    fun testInnerPrintln() {
        assert("inner_println", "0\n1\n")
    }

    @Test(expected = DoubleDefinitionException::class)
    fun testDoubleDef() {
        assert("double_def", "")
    }

    @Test(expected = VarIsNotInScopeException::class)
    fun testVarIsNotScope() {
        assert("var_is_not_in_scope", "")
    }

    @Test(expected = FuncIsNotInScopeException::class)
    fun testFuncIsNotInScope() {
        assert("func_is_not_in_scope", "")
    }

    @Test(expected = IllegalNumberOfArguments::class)
    fun testIllegalNumberOfArgs() {
        assert("illegal_number_of_args", "")
    }


    private fun assert(test: String, expectedOutput: String) {
        val actualOutput = ru.spbau.mit.execute("$RESOURCES_PATH/$test.txt")
        assertEquals(expectedOutput, actualOutput)
    }

}
