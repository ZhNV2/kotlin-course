package ru.spbau.mit

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import ru.spbau.mit.ast.AstBuilder
import ru.spbau.mit.execution.AstExecVisitor
import ru.spbau.mit.parser.FunLexer
import ru.spbau.mit.parser.FunParser
import java.nio.file.Files
import java.nio.file.Paths

fun execute(file: String): String {
    val funLexer = FunLexer(CharStreams.fromString(String(Files.readAllBytes(Paths.get(file)))))
    val funParser = FunParser(BufferedTokenStream(funLexer))

    val ast = AstBuilder().buildFile(funParser.file())
    val visitor = AstExecVisitor.build()
    ast.accept(visitor)
    return visitor.getOutput()
}

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("specify file to interpret")
        return
    }
    println(execute(args[0]))
}
