package ru.spbau.mit

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import ru.spbau.mit.ast.AstBuilder
import ru.spbau.mit.ast.AstNode
import ru.spbau.mit.ast.ExpressionNode
import ru.spbau.mit.exception.*
import ru.spbau.mit.execution.AstExecVisitor
import ru.spbau.mit.execution.Debugger
import ru.spbau.mit.execution.SimpleContinuation
import ru.spbau.mit.parser.FunLexer
import ru.spbau.mit.parser.FunParser
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.coroutines.experimental.startCoroutine

fun buildAstFromFile(file: String): AstNode {
    val funLexer = FunLexer(CharStreams.fromString(String(Files.readAllBytes(Paths.get(file)))))
    val funParser = FunParser(BufferedTokenStream(funLexer))
    return AstBuilder().visit(funParser.file())
}

fun buildAstFromExpression(text: String): AstNode {
    val funLexer = FunLexer(CharStreams.fromString(text))
    val funParser = FunParser(BufferedTokenStream(funLexer))

    return AstBuilder().visit(funParser.expression())
}

fun printValue(suspendLambda: suspend () -> Int) {
    suspendLambda.startCoroutine(object : SimpleContinuation<Int>() {
        override fun resume(value: Int) {
            println(value)
        }
    })
}

fun main(args: Array<String>) {

    val debugger: Debugger = AstExecVisitor.build()

    while (true) {
        print(">>")
        val command = readLine()!!.split(" ").toList()
        var exit = false
        try {
            when (command[0].toLowerCase()) {
                "load" -> {
                    debugger.load(buildAstFromFile(command[1]))
                }
                "breakpoint" -> {
                    debugger.breakPoint(command[1].toInt())
                }
                "condition" -> {
                    val text = command.subList(2, command.size).joinToString(" ")
                    debugger.condition(command[1].toInt(), buildAstFromExpression(text) as ExpressionNode, text)
                }
                "list" -> {
                    debugger.list()
                }
                "remove" -> {
                    debugger.remove(command[1].toInt())
                }
                "run" -> {
                    debugger.run()
                }
                "evaluateIndependently" -> {
                    val text = command.subList(1, command.size).joinToString(" ")
                    printValue {
                        debugger.evaluateIndependently(buildAstFromExpression(text) as ExpressionNode)
                    }
                }
                "stop" -> {
                    debugger.stop()
                }
                "continue" -> {
                    debugger.continueDebug()
                }
                "exit" -> {
                    exit = true
                }
            }
            if (exit) {
                break
            }
        } catch (e: AlreadyRunException) {
            println("you have already run program")
        } catch (e: AlreadyFinishedException) {
            println("program has already finished")
        } catch (e: DoubleDefinitionException) {
            println("program contains an error: " + e.message)
        } catch (e: DoubleDefinitionException) {
            println("program contains an error: " + e.message)
        } catch (e: VarIsNotInScopeException) {
            println("program contains an error: variable ${e.message} is not in scope")
        } catch (e: FuncIsNotInScopeException) {
            println("program contains an error: function ${e.message} is not in scope")
        } catch (e: IllegalNumberOfArguments) {
            println("program contains an error: " + e.message)
        } catch (e: Exception) {
            println("program finished with exception " + e.toString())
        }

    }

}