package ru.spbau.mit.execution

import ru.spbau.mit.ast.BlockNode
import ru.spbau.mit.exception.DoubleDefinitionException
import ru.spbau.mit.exception.FuncIsNotInScopeException
import ru.spbau.mit.exception.VarIsNotInScopeException

data class Function(
        val scope: Scope,
        val block: BlockNode,
        val argNames: List<String>
)

open class Scope {

    private val vars: MutableMap<String, Int>
    private val functions: MutableMap<String, Function>
    val prev: Scope

    constructor(vars: MutableMap<String, Int>,
                functions: MutableMap<String, Function>) {
        this.vars = vars
        this.functions = functions
        this.prev = NullScope
    }

    constructor(vars: MutableMap<String, Int>,
                functions: MutableMap<String, Function>,
                prev: Scope) {
        this.vars = vars
        this.functions = functions
        this.prev = prev
    }

    private fun putVarInScope(name: String, value: Int) {
        vars.put(name, value)
    }

    private fun putFuncInScope(name: String, func: Function) {
        functions.put(name, func)
    }

    private fun getVarFromScope(name: String): Int? {
        return vars[name]
    }

    private fun getFuncFromScope(name: String): Function? {
        return functions[name]
    }

    private fun containsVarInScope(name: String): Boolean {
        return vars.containsKey(name)
    }

    private fun containsFuncInScope(name: String): Boolean {
        return functions.containsKey(name)
    }

    open fun putVar(name: String, value: Int, isAssignment: Boolean) {
        if (!isAssignment) {
            putVarInScope(name, value)
        } else if (containsVarInScope(name)) {
            putVarInScope(name, value)
            return
        } else {
            prev.putVar(name, value, isAssignment)
        }
    }

    open fun putFunc(name: String, value: Function) {
        if (containsFunc(name)) {
            throw DoubleDefinitionException("function $name was defined twice")
        }
        putFuncInScope(name, value)
    }

    open fun containsFunc(name: String): Boolean {
        return if (containsFuncInScope(name)) true else prev.containsFunc(name)
    }

    open fun getVar(name: String): Int {
        getVarFromScope(name)?.let {
            return it
        }
        return prev.getVar(name)
    }

    open fun getFunc(name: String): Function {
        getFuncFromScope(name)?.let {
            return it
        }
        return prev.getFunc(name)
    }

    companion object {
        fun empty() = Scope(mutableMapOf(), mutableMapOf(), NullScope)
        fun from(scope: Scope) = Scope(mutableMapOf(), mutableMapOf(), scope)
    }

}

private object NullScope : Scope(mutableMapOf(), mutableMapOf()) {

    override fun putVar(name: String, value: Int, isAssignment: Boolean) {
        throw VarIsNotInScopeException(name)
    }

    override fun putFunc(name: String, value: Function) {
    }

    override fun getVar(name: String): Int {
        throw VarIsNotInScopeException(name)
    }

    override fun getFunc(name: String): Function {
        throw FuncIsNotInScopeException(name)
    }

    override fun containsFunc(name: String): Boolean {
        return false
    }

}
