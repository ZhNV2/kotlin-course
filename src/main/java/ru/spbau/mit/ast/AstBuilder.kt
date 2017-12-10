package ru.spbau.mit.ast

import org.antlr.v4.runtime.tree.TerminalNode
import ru.spbau.mit.parser.FunBaseVisitor
import ru.spbau.mit.parser.FunParser

class AstBuilder : FunBaseVisitor<AstNode>() {
    
    override fun visitFile(ctx: FunParser.FileContext): FileNode {
        val block = visitBlock(ctx.block())
        return FileNode(block, ctx.start.line)
    }

    override fun visitBlock(ctx: FunParser.BlockContext): BlockNode {
        val nodes: MutableList<StatementNode> = mutableListOf()
        ctx.statement().forEach { nodes.add(visitStatement(it) as StatementNode) }
        return BlockNode(nodes, ctx.start.line)
    }

    override fun visitBlockWithBraces(ctx: FunParser.BlockWithBracesContext): BlockNode {
        return visitBlock(ctx.block())
    }

    override fun visitPrintln(ctx: FunParser.PrintlnContext): PrintlnNode {
        return PrintlnNode(visitArgumentsNode(ctx.arguments()), ctx.start.line)
    }

    override fun visitFunction(ctx: FunParser.FunctionContext): FunctionNode {
        val identifiers = ctx.Identifier()
        val name = visitTerminalNode(identifiers[0])
        val argNames = mutableListOf<String>()
        identifiers.subList(1, identifiers.size).forEach {
            argNames.add(visitTerminalNode(it))
        }
        val body = visitBlockWithBraces(ctx.blockWithBraces())
        return FunctionNode(name, argNames, body, ctx.start.line)
    }

    override fun visitVariable(ctx: FunParser.VariableContext): VariableNode {
        val varName = visitTerminalNode(ctx.Identifier())
        val varValue = visitExpression(ctx.expression())
        return VariableNode(varName, varValue, ctx.start.line)
    }

    override fun visitWhileT(ctx: FunParser.WhileTContext): WhileNode {
        val cond = visitExpression(ctx.expression())
        val body = visitBlockWithBraces(ctx.blockWithBraces())
        return WhileNode(cond, body, ctx.start.line)
    }

    override fun visitIfT(ctx: FunParser.IfTContext): IfNode {
        val cond = visitExpression(ctx.expression())
        val bodies = ctx.blockWithBraces()
        val ifBody = visitBlockWithBraces(bodies[0])
        val elseBody = if (bodies.size == 1) null else visitBlockWithBraces(bodies[1])
        return IfNode(cond, ifBody, elseBody, ctx.start.line)
    }

    override fun visitAssignment(ctx: FunParser.AssignmentContext): AssignmentNode {
        val varName = visitTerminalNode(ctx.Identifier())
        val newValue = visitExpression(ctx.expression())
        return AssignmentNode(varName, newValue, ctx.start.line)
    }

    override fun visitReturnT(ctx: FunParser.ReturnTContext): ReturnNode {
        return ReturnNode(visitExpression(ctx.expression()), ctx.start.line)
    }

    override fun visitExpression(ctx: FunParser.ExpressionContext): ExpressionNode {
        val children = ctx.children
        if (children.size == 1) {
            return super.visitExpression(ctx) as ExpressionNode
        }
        val expressions = ctx.expression()
        val left = visitExpression(expressions[0])
        val op = ctx.op.text
        val right = visitExpression(expressions[1])
        return BinaryExpressionNode(left, op, right, ctx.start.line)
    }

    override fun visitVar(ctx: FunParser.VarContext): VarNode {
        return VarNode(ctx.text, ctx.start.line)
    }

    override fun visitExpressionInBrackets(ctx: FunParser.ExpressionInBracketsContext): ExpressionNode {
        return visitExpression(ctx.expression())
    }

    override fun visitFunctionCall(ctx: FunParser.FunctionCallContext): FunctionCallNode {
        val name = visitTerminalNode(ctx.Identifier())
        val args = visitArgumentsNode(ctx.arguments())
        return FunctionCallNode(name, args, ctx.start.line)
    }

    override fun visitLiteral(ctx: FunParser.LiteralContext): LiteralNode {
        return LiteralNode(ctx.Literal().text.toInt(), ctx.start.line)
    }

    private fun visitArgumentsNode(ctx: FunParser.ArgumentsContext): List<ExpressionNode> {
        val nodes = mutableListOf<ExpressionNode>()
        ctx.expression().forEach { nodes.add(visitExpression(it)) }
        return nodes
    }

    private fun visitTerminalNode(terminalNode: TerminalNode): String {
        return terminalNode.text
    }

}