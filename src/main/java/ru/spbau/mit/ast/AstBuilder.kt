package ru.spbau.mit.ast

import org.antlr.v4.runtime.tree.ErrorNode
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.RuleNode
import org.antlr.v4.runtime.tree.TerminalNode
import ru.spbau.mit.parser.FunParser
import ru.spbau.mit.parser.FunVisitor

class FunAstBuilder() : FunVisitor<FunAstNode> {
    override fun visitFile(ctx: FunParser.FileContext?): FunAstNode {
        val block = ctx!!.block().accept(this)
        return FileNode(block)
    }

    override fun visitBlock(ctx: FunParser.BlockContext?): FunAstNode {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitBlockWithBraces(ctx: FunParser.BlockWithBracesContext?): FunAstNode {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitStatement(ctx: FunParser.StatementContext?): FunAstNode {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitPrintln(ctx: FunParser.PrintlnContext?): FunAstNode {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitFunction(ctx: FunParser.FunctionContext?): FunAstNode {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitParameterNames(ctx: FunParser.ParameterNamesContext?): FunAstNode {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitVariable(ctx: FunParser.VariableContext?): FunAstNode {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitChildren(node: RuleNode?): FunAstNode {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitWhileT(ctx: FunParser.WhileTContext?): FunAstNode {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitIfT(ctx: FunParser.IfTContext?): FunAstNode {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitAssignment(ctx: FunParser.AssignmentContext?): FunAstNode {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitReturnT(ctx: FunParser.ReturnTContext?): FunAstNode {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitExpression(ctx: FunParser.ExpressionContext?): FunAstNode {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitExpressionInBrackets(ctx: FunParser.ExpressionInBracketsContext?): FunAstNode {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitFunctionCall(ctx: FunParser.FunctionCallContext?): FunAstNode {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitArguments(ctx: FunParser.ArgumentsContext?): FunAstNode {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitIdentifier(ctx: FunParser.IdentifierContext?): FunAstNode {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitLiteral(ctx: FunParser.LiteralContext?): FunAstNode {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitTerminal(node: TerminalNode?): FunAstNode {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visit(tree: ParseTree?): FunAstNode {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun visitErrorNode(node: ErrorNode?): FunAstNode {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}