package ru.spbau.mit.execution

import ru.spbau.mit.exception.DoubleDefinitionException
import ru.spbau.mit.exception.FuncIsNotInScopeException
import ru.spbau.mit.exception.VarIsNotInScopeException


class ExecutionContext(private val ctx: MutableList<RuntimeContext>) {

    fun putVar(name: String, value: Int, isAssignment: Boolean) {
        if (!isAssignment) {
            ctx.last().putVar(name, value)
        }
        for (runtimeContext in ctx.reversed()) {
            if (runtimeContext.containsVar(name)) {
                runtimeContext.putVar(name, value)
                return
            }
        }
        throw VarIsNotInScopeException(name)
    }

    fun putFunc(name: String, value: Function) {
        for (runtimeContext in ctx.reversed()) {
            if (runtimeContext.containsFunc(name)) {
                throw DoubleDefinitionException("function $name was defined twice")
            }
        }
        ctx.last().putFunc(name, value)
    }

    fun getVar(name: String): Int {
        for (runtimeContext in ctx.reversed()) {
            val value = runtimeContext.getVar(name)
            if (value != null) {
                return value
            }
        }
        throw VarIsNotInScopeException(name)
    }

    fun getFunc(name: String): Function {
        for (runtimeContext in ctx.reversed()) {
            val func = runtimeContext.getFunc(name)
            if (func != null) {
                return func
            }
        }
        throw FuncIsNotInScopeException(name)
    }

    fun addRuntimeContext() {
        ctx.add(RuntimeContext.empty())
    }

    fun removeRuntimeContext() {
        ctx.removeAt(ctx.lastIndex)
    }

    fun copy(): ExecutionContext {
        val list = mutableListOf<RuntimeContext>()
        ctx.forEach { list.add(it) }
        return ExecutionContext(list)
    }

    companion object {
        fun empty() = ExecutionContext(mutableListOf())
    }
}

