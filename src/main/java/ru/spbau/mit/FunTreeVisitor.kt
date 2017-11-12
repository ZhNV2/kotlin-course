package ru.spbau.mit

import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor
import ru.spbau.mit.exception.DoubleDefinitionException
import ru.spbau.mit.exception.FuncIsNotInScopeException
import ru.spbau.mit.exception.VarIsNotInScopeException
import ru.spbau.mit.parser.FunParser
import ru.spbau.mit.parser.FunVisitor
import java.util.function.Supplier
import kotlin.text.toInt

class FunTreeVisitor : AbstractParseTreeVisitor<Int>(), FunVisitor<Int> {

    override fun visitFile(ctx: FunParser.FileContext?): Int {
        output = ""
        return visitFile(ctx!!, mutableListOf(RuntimeContext(mutableMapOf(), mutableMapOf()))) ?: 0
    }

    fun getOutput(): String {
        return output
    }

    override fun visitBlock(ctx: FunParser.BlockContext?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitBlockWithBraces(ctx: FunParser.BlockWithBracesContext?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitStatement(ctx: FunParser.StatementContext?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitPrintln(ctx: FunParser.PrintlnContext?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitFunction(ctx: FunParser.FunctionContext?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitParameterNames(ctx: FunParser.ParameterNamesContext?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitVariable(ctx: FunParser.VariableContext?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitWhileT(ctx: FunParser.WhileTContext?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitIfT(ctx: FunParser.IfTContext?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitAssignment(ctx: FunParser.AssignmentContext?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitReturnT(ctx: FunParser.ReturnTContext?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitExpression(ctx: FunParser.ExpressionContext?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitExpressionInBrackets(ctx: FunParser.ExpressionInBracketsContext?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitFunctionCall(ctx: FunParser.FunctionCallContext?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitArguments(ctx: FunParser.ArgumentsContext?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitBinaryExpression(ctx: FunParser.BinaryExpressionContext?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitEval(ctx: FunParser.EvalContext?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitAdditionExp(ctx: FunParser.AdditionExpContext?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitMultiplyExpWithSign(ctx: FunParser.MultiplyExpWithSignContext?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitPlusMultiplyExp(ctx: FunParser.PlusMultiplyExpContext?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitMinusMultiplyExp(ctx: FunParser.MinusMultiplyExpContext?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitMultiplyExp(ctx: FunParser.MultiplyExpContext?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitAtomExpWithSign(ctx: FunParser.AtomExpWithSignContext?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitMulAtomExp(ctx: FunParser.MulAtomExpContext?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitDivAtomExp(ctx: FunParser.DivAtomExpContext?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitModAtomExp(ctx: FunParser.ModAtomExpContext?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitAtomExp(ctx: FunParser.AtomExpContext?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    ///////////////////////////////////////////////

    private var output: String = ""

    private fun visitFile(ctx: FunParser.FileContext, rCtx: MutableList<RuntimeContext>): Int? {
        return addRuntimeContext(rCtx, Supplier { visitBlock(ctx.block(), rCtx) })
    }

    private fun visitBlock(ctx: FunParser.BlockContext, rCtx: MutableList<RuntimeContext>): Int? {
        for (statement in ctx.statement()) {
            val res = visitStatement(statement, rCtx)
            if (res != null) {
                return res
            }
        }
        return null
    }


    private fun visitPrintln(ctx: FunParser.PrintlnContext, rCtx: MutableList<RuntimeContext>): Int? {
        visitArguments(ctx.arguments(), rCtx).forEach { output += it.toString() + " " }
        output += "\n"
        return null
    }

    private fun visitExpressionInBrackets(ctx: FunParser.ExpressionInBracketsContext, rCtx: MutableList<RuntimeContext>): Int {
        if (ctx.expression() != null) {
            return visitExpression(ctx.expression(), rCtx)
        } else if (ctx.binaryExpression() != null) {
            return visitBinaryExpression(ctx.binaryExpression(), rCtx)
        }
        throw IllegalStateException()
    }


    private fun visitBlockWithBraces(ctx: FunParser.BlockWithBracesContext, rCtx: MutableList<RuntimeContext>): Int? {
        return addRuntimeContext(rCtx, Supplier { visitBlock(ctx.block(), rCtx) })
    }

    private fun visitStatement(ctx: FunParser.StatementContext, rCtx: MutableList<RuntimeContext>): Int? {
        return when {
            ctx.assignment() != null -> visitAssignment(ctx.assignment(), rCtx)
            ctx.expression() != null -> visitExpression(ctx.expression(), rCtx)
            ctx.function() != null -> visitFunction(ctx.function(), rCtx)
            ctx.variable() != null -> visitVariable(ctx.variable(), rCtx)
            ctx.returnT() != null -> visitReturnT(ctx.returnT(), rCtx)
            ctx.ifT() != null -> visitIfT(ctx.ifT(), rCtx)
            ctx.whileT() != null -> visitWhileT(ctx.whileT(), rCtx)
            ctx.println() != null -> visitPrintln(ctx.println(), rCtx)
            else -> throw IllegalStateException()
        }

    }

    private fun visitFunction(ctx: FunParser.FunctionContext, rCtx: MutableList<RuntimeContext>): Int? {
        val funcName = ctx.Identifier().symbol.text
        val argNames = ctx.parameterNames().Identifier().map { it -> it.symbol.text }
        putFunc(ctx, rCtx, funcName, Function(copyMutableList(rCtx), ctx.blockWithBraces(), argNames))
        return null
    }

    private fun visitVariable(ctx: FunParser.VariableContext, rCtx: MutableList<RuntimeContext>): Int? {
        val value = if (ctx.expression() == null) 0 else visitExpression(ctx.expression(), rCtx)
        putVar(ctx, rCtx, ctx.Identifier().symbol.text, value, false)
        return null
    }

    private fun visitWhileT(ctx: FunParser.WhileTContext, rCtx: MutableList<RuntimeContext>): Int? {
        while (checkCondition(ctx.expressionInBrackets(), rCtx)) {
            val res = visitBlockWithBraces(ctx.blockWithBraces(), rCtx)
            if (res != null) {
                return res
            }
        }
        return null
    }

    private fun visitIfT(ctx: FunParser.IfTContext, rCtx: MutableList<RuntimeContext>): Int? {
        if (checkCondition(ctx.expressionInBrackets(), rCtx)) {
            return visitBlockWithBraces(ctx.blockWithBraces()[0], rCtx)
        } else if (ctx.blockWithBraces().size > 1) {
            return visitBlockWithBraces(ctx.blockWithBraces()[1], rCtx)
        }
        return null
    }

    private fun checkCondition(ctx: FunParser.ExpressionInBracketsContext, rCtx: MutableList<RuntimeContext>): Boolean {
        return visitExpressionInBrackets(ctx, rCtx) != 0
    }

    private fun visitAssignment(ctx: FunParser.AssignmentContext, rCtx: MutableList<RuntimeContext>): Int? {
        val value = visitExpression(ctx.expression(), rCtx)
        putVar(ctx, rCtx, ctx.Identifier().symbol.text, value, true)
        return null
    }

    private fun visitReturnT(ctx: FunParser.ReturnTContext, rCtx: MutableList<RuntimeContext>): Int {
        return visitExpression(ctx.expression(), rCtx)
    }

    private fun visitExpression(ctx: FunParser.ExpressionContext, rCtx: MutableList<RuntimeContext>): Int {
        return when {
            ctx.Identifier() != null -> getVar(ctx, rCtx, ctx.Identifier().symbol.text)
            ctx.eval() != null -> visitEval(ctx.eval(), rCtx)
            ctx.expressionInBrackets() != null -> visitExpressionInBrackets(ctx.expressionInBrackets(), rCtx)
            ctx.functionCall() != null -> visitFunctionCall(ctx.functionCall(), rCtx)
            else -> throw IllegalStateException()
        }
    }

    private fun visitFunctionCall(ctx: FunParser.FunctionCallContext, rCtx: MutableList<RuntimeContext>): Int {
        val funcName = ctx.Identifier().symbol.text
        val func = getFunc(ctx, rCtx, funcName)
        val args = visitArguments(ctx.arguments(), rCtx)
        if (func.argNames.size != args.size) {
            throw IllegalArgumentException(funcName + " was called with invalid number of params")
        }
        val funcCtx = copyMutableList(func.rCtx)
        val newRuntimeContext = RuntimeContext(mutableMapOf(), mutableMapOf())
        funcCtx.add(newRuntimeContext)
        for (i in 0 until func.argNames.size) {
            putVar(ctx, funcCtx, func.argNames[i], args[i], false)
        }
        return visitBlockWithBraces(func.blockContext, funcCtx) ?: 0
    }

    private fun visitArguments(ctx: FunParser.ArgumentsContext, rCtx: MutableList<RuntimeContext>): List<Int> {
        val args = mutableListOf<Int>()
        ctx.expression().forEach({
            args.add(visitExpression(it, rCtx))
        })
        return args
    }

    private fun visitBinaryExpression(ctx: FunParser.BinaryExpressionContext, rCtx: MutableList<RuntimeContext>): Int {
        val left = visitExpression(ctx.expression()[0], rCtx)
        val right = visitExpression(ctx.expression()[1], rCtx)
        when (ctx.sign.text) {
            "<" -> return toInt(left < right)
            ">" -> return toInt(left > right)
            "<=" -> return toInt(left <= right)
            ">=" -> return toInt(left >= right)
            "==" -> return toInt(left == right)
            "!=" -> return toInt(left != right)
            "||" -> return toInt(toBoolean(left) || toBoolean(right))
            "&&" -> return toInt(toBoolean(left) && toBoolean(right))
        }
        throw IllegalStateException()
    }

    private fun toInt(boolean: Boolean): Int {
        return if (boolean) 1 else 0
    }

    private fun toBoolean(int: Int): Boolean {
        return int != 0
    }

    private fun visitEval(ctx: FunParser.EvalContext, rCtx: MutableList<RuntimeContext>): Int {
        return visitAdditionExp(ctx.additionExp(), rCtx)
    }

    private fun visitAdditionExp(ctx: FunParser.AdditionExpContext, rCtx: MutableList<RuntimeContext>): Int {
        return visitMultiplyExp(ctx.multiplyExp(), rCtx) +
                ctx.multiplyExpWithSign().sumBy { visitMultiplyExpWithSign(it, rCtx) }
    }

    private fun visitMultiplyExp(ctx: FunParser.MultiplyExpContext, rCtx: MutableList<RuntimeContext>): Int {
        var result = visitAtomExp(ctx.atomExp(), rCtx)
        for (atomExpWithSign in ctx.atomExpWithSign()) {
            result = visitAtomExpWithSign(atomExpWithSign, rCtx, result)
        }
        return result
    }

    private fun visitAtomExp(ctx: FunParser.AtomExpContext, rCtx: MutableList<RuntimeContext>): Int {
        return when {
            ctx.expressionInBrackets() != null -> visitExpressionInBrackets(ctx.expressionInBrackets(), rCtx)
            ctx.functionCall() != null -> visitFunctionCall(ctx.functionCall(), rCtx)
            ctx.Identifier() != null -> getVar(ctx, rCtx, ctx.Identifier().symbol.text)
            ctx.Literal() != null -> ctx.Literal().symbol.text.toInt()
            else -> throw IllegalStateException()
        }
    }

    private fun visitMultiplyExpWithSign(ctx: FunParser.MultiplyExpWithSignContext, rCtx: MutableList<RuntimeContext>): Int {
        return when {
            ctx.minusMultiplyExp() != null -> visitMinusMultiplyExp(ctx.minusMultiplyExp(), rCtx)
            ctx.plusMultiplyExp() != null -> visitPlusMultiplyExp(ctx.plusMultiplyExp(), rCtx)
            else -> throw IllegalStateException()
        }
    }

    private fun visitAtomExpWithSign(
            ctx: FunParser.AtomExpWithSignContext,
            rCtx: MutableList<RuntimeContext>,
            cur: Int
    ): Int {
        return when {
            ctx.divAtomExp() != null -> visitDivAtomExp(ctx.divAtomExp(), rCtx, cur)
            ctx.modAtomExp() != null -> visitModAtomExp(ctx.modAtomExp(), rCtx, cur)
            ctx.mulAtomExp() != null -> visitMulAtomExp(ctx.mulAtomExp(), rCtx, cur)
            else -> throw IllegalStateException()
        }
    }

    private fun <T> copyMutableList(list: MutableList<T>): MutableList<T> {
        val res = mutableListOf<T>()
        list.forEach { res.add(it) }
        return res
    }


    private fun visitPlusMultiplyExp(ctx: FunParser.PlusMultiplyExpContext, rCtx: MutableList<RuntimeContext>): Int {
        return visitMultiplyExp(ctx.multiplyExp(), rCtx)
    }

    private fun visitMinusMultiplyExp(ctx: FunParser.MinusMultiplyExpContext, rCtx: MutableList<RuntimeContext>): Int {
        return -visitMultiplyExp(ctx.multiplyExp(), rCtx)
    }

    private fun visitMulAtomExp(
            ctx: FunParser.MulAtomExpContext,
            rCtx: MutableList<RuntimeContext>,
            cur: Int
    ): Int {
        return cur * visitAtomExp(ctx.atomExp(), rCtx)

    }

    private fun visitDivAtomExp(
            ctx: FunParser.DivAtomExpContext,
            rCtx: MutableList<RuntimeContext>,
            cur: Int
    ): Int {
        return cur / visitAtomExp(ctx.atomExp(), rCtx)
    }

    private fun visitModAtomExp(
            ctx: FunParser.ModAtomExpContext,
            rCtx: MutableList<RuntimeContext>,
            cur: Int
    ): Int {
        return cur % visitAtomExp(ctx.atomExp(), rCtx)
    }

    private fun addRuntimeContext(rCtx: MutableList<RuntimeContext>, action: Supplier<Int?>): Int? {
        rCtx.add(RuntimeContext(mutableMapOf(), mutableMapOf()))
        val res = action.get()
        rCtx.removeAt(rCtx.size - 1)
        return res
    }

    private fun putVar(ctx: ParserRuleContext, rCtx: MutableList<RuntimeContext>,
                       name: String, value: Int, isAssignment: Boolean) {
        if (!isAssignment) {
            rCtx[rCtx.size - 1].putVar(name, value)
        }
        for (runtimeContext in rCtx.reversed()) {
            if (runtimeContext.containsVar(name)) {
                runtimeContext.putVar(name, value)
                return
            }
        }
        throw VarIsNotInScopeException(name + " start = " + ctx.start + " stop = " + ctx.stop)
    }

    private fun putFunc(ctx: ParserRuleContext, rCtx: MutableList<RuntimeContext>, name: String, value: Function) {
        for (runtimeContext in rCtx.reversed()) {
            if (runtimeContext.containsFunc(name)) {
                throw DoubleDefinitionException("function $name was defined twice")
            }
        }
        rCtx[rCtx.size - 1].putFunc(name, value)
    }

    private fun getVar(ctx: ParserRuleContext, rCtx: MutableList<RuntimeContext>, name: String): Int {
        for (runtimeContext in rCtx.reversed()) {
            val value = runtimeContext.getVar(name)
            if (value != null) {
                return value;
            }
        }
        throw VarIsNotInScopeException(name + " start = " + ctx.start + " stop = " + ctx.stop)
    }

    private fun getFunc(ctx: ParserRuleContext, rCtx: MutableList<RuntimeContext>, name: String): Function {
        for (runtimeContext in rCtx.reversed()) {
            val func = runtimeContext.getFunc(name)
            if (func != null) {
                return func;
            }
        }
        throw FuncIsNotInScopeException(name + " start = " + ctx.start + " stop = " + ctx.stop)
    }

}