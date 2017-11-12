package ru.spbau.mit
import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.junit.Test
import ru.spbau.mit.parser.FunLexer
import ru.spbau.mit.parser.FunParser
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.assertEquals

class TestSource {

    private val RESOURCES = "src/test/resources/funSamples"

    @Test
    fun test1() {
        execute(1, 0, "1 \n")
    }

    @Test
    fun test2() {
        execute(2, 0,
                "1 1 \n" +
                "2 2 \n" +
                "3 3 \n" +
                "4 5 \n" +
                "5 8 \n"
        )
    }

    @Test
    fun test3() {
        execute(3, 0,
                "42 \n")
    }

    @Test
    fun test4() {
        execute(4, 0,
                "2 \n" +
                "3 \n" +
                "5 \n" +
                "4 \n"
        )
    }

    private fun execute(num: Int, expectedResult: Int, expectedOutput: String) {
        val funParser = FunParser(BufferedTokenStream(
                FunLexer(CharStreams.fromString(readFile("$RESOURCES/$num.txt")))))
        val visitor = FunTreeVisitor()
        assertEquals(expectedResult, visitor.visit(funParser.file()))
        assertEquals(expectedOutput, visitor.getOutput())
    }

    private fun readFile(filePath: String): String {
        return String(Files.readAllBytes(Paths.get(filePath)));
    }
}
