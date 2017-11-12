package ru.spbau.mit

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import ru.spbau.mit.parser.FunLexer
import ru.spbau.mit.parser.FunParser
import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("specify file to interpret")
        return
    }
    val funLexer = FunLexer(CharStreams.fromString(String(Files.readAllBytes(Paths.get(args[0])))))
    val funParser = FunParser(BufferedTokenStream(funLexer))
    val visitor = FunTreeVisitor()
    println("program finished with code " + visitor.visit(funParser.file()))
    println("output:")
    println(visitor.getOutput())
}
