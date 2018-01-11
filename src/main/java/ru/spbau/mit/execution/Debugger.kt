package ru.spbau.mit.execution

import ru.spbau.mit.ast.AstNode
import ru.spbau.mit.ast.ExpressionNode

interface Debugger {

    fun load(node: AstNode)

    fun breakPoint(line: Int)

    fun condition(line: Int, expression: ExpressionNode, text: String)

    fun list()

    fun remove(line: Int)

    fun run()

    suspend fun evaluateIndependently(expression: ExpressionNode): Int

    fun stop()

    fun continueDebug()

    fun lastRunOutput(): String

}