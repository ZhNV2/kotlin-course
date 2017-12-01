package ru.spbau.mit.ast

import org.antlr.v4.runtime.tree.TerminalNode
import ru.spbau.mit.parser.FunParser

class AstBuilder {

    fun buildFile(ctx: FunParser.FileContext?): FileNode {
        val block = buildBlock(ctx!!.block())
        return FileNode(block)
    }

    fun buildBlock(ctx: FunParser.BlockContext?): BlockNode {
        val nodes: MutableList<StatementNode> = mutableListOf()
        ctx!!.statement().forEach { nodes.add(buildStatement(it)) }
        return BlockNode(nodes)
    }

    fun buildBlockWithBraces(ctx: FunParser.BlockWithBracesContext?): BlockNode {
        return buildBlock(ctx!!.block())
    }

    fun buildStatement(ctx: FunParser.StatementContext?): StatementNode {
        return when {
            ctx!!.println() != null -> buildPrintln(ctx.println())
            ctx.whileT() != null -> buildWhileT(ctx.whileT())
            ctx.ifT() != null -> buildIfT(ctx.ifT())
            ctx.returnT() != null -> buildReturnT(ctx.returnT())
            ctx.variable() != null -> buildVariable(ctx.variable())
            ctx.function() != null -> buildFunction(ctx.function())
            ctx.assignment() != null -> buildAssignment(ctx.assignment())
            ctx.expression() != null -> buildExpression(ctx.expression())
            else -> throw IllegalStateException("statement has unknown type")
        }
    }

    fun buildPrintln(ctx: FunParser.PrintlnContext?): PrintlnNode {
        return PrintlnNode(buildArguments(ctx!!.arguments()))
    }

    fun buildFunction(ctx: FunParser.FunctionContext?): FunctionNode {
        val identifiers = ctx!!.Identifier()
        val name = buildTerminalNode(identifiers[0])
        val argNames = mutableListOf<String>()
        identifiers.subList(1, identifiers.size).forEach {
            argNames.add(buildTerminalNode(it))
        }
        val body = buildBlockWithBraces(ctx.blockWithBraces())
        return FunctionNode(name, argNames, body)
    }

    fun buildVariable(ctx: FunParser.VariableContext?): VariableNode {
        val varName = buildTerminalNode(ctx!!.Identifier())
        val varValue = buildExpression(ctx.expression())
        return VariableNode(varName, varValue)
    }

    fun buildWhileT(ctx: FunParser.WhileTContext?): WhileNode {
        val cond = buildExpression(ctx!!.expression())
        val body = buildBlockWithBraces(ctx.blockWithBraces())
        return WhileNode(cond, body)
    }

    fun buildIfT(ctx: FunParser.IfTContext?): IfNode {
        val cond = buildExpression(ctx!!.expression())
        val bodies = ctx.blockWithBraces()
        val ifBody = buildBlockWithBraces(bodies[0])
        val elseBody = if (bodies.size == 1) null else buildBlockWithBraces(bodies[1])
        return IfNode(cond, ifBody, elseBody)
    }

    fun buildAssignment(ctx: FunParser.AssignmentContext?): AssignmentNode {
        val varName = buildTerminalNode(ctx!!.Identifier())
        val newValue = buildExpression(ctx.expression())
        return AssignmentNode(varName, newValue)
    }

    fun buildReturnT(ctx: FunParser.ReturnTContext?): ReturnNode {
        return ReturnNode(buildExpression(ctx!!.expression()))
    }

    fun buildExpression(ctx: FunParser.ExpressionContext?): ExpressionNode {
        val children = ctx!!.children
        return if (children.size == 1) {
            when {
                ctx.expressionInBrackets() != null -> buildExpressionInBrackets(ctx.expressionInBrackets())
                ctx.`var`() != null -> buildVar(ctx.`var`())
                ctx.literal() != null -> buildLiteral(ctx.literal())
                ctx.functionCall() != null -> buildFunctionCall(ctx.functionCall())
                else -> throw IllegalStateException("unknown expression type")
            }
        } else {
            val expressions = ctx.expression()
            val left = buildExpression(expressions[0])
            val op = ctx.op.text
            val right = buildExpression(expressions[1])
            BinaryExpressionNode(left, op, right)
        }
    }

    fun buildVar(ctx: FunParser.VarContext?): VarNode {
        return VarNode(ctx!!.text)
    }

    fun buildExpressionInBrackets(ctx: FunParser.ExpressionInBracketsContext?): ExpressionNode {
        return buildExpression(ctx!!.expression())
    }

    fun buildFunctionCall(ctx: FunParser.FunctionCallContext?): FunctionCallNode {
        val name = buildTerminalNode(ctx!!.Identifier())
        val args = buildArguments(ctx.arguments())
        return FunctionCallNode(name, args)
    }

    fun buildArguments(ctx: FunParser.ArgumentsContext?): List<ExpressionNode> {
        val nodes = mutableListOf<ExpressionNode>()
        ctx!!.expression().forEach { nodes.add(buildExpression(it)) }
        return nodes
    }

    fun buildTerminalNode(terminalNode: TerminalNode): String {
        return terminalNode.text
    }

    fun buildLiteral(ctx: FunParser.LiteralContext?): LiteralNode {
        return LiteralNode(ctx!!.Literal().text.toInt())
    }

}