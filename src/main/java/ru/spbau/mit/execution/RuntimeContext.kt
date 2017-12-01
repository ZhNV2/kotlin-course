package ru.spbau.mit.execution

import ru.spbau.mit.ast.BlockNode

data class Function(
        val execCtx: ExecutionContext,
        val block: BlockNode,
        val argNames: List<String>
)

class RuntimeContext(private val vars: MutableMap<String, Int>,
                     private val functions: MutableMap<String, Function>) {
    fun putVar(name: String, value: Int) {
        vars.put(name, value)
    }

    fun putFunc(name: String, func: Function) {
        functions.put(name, func)
    }

    fun getVar(name: String): Int? {
        return vars[name]
    }

    fun getFunc(name: String): Function? {
        return functions[name]
    }

    fun containsVar(name: String): Boolean {
        return vars.containsKey(name)
    }

    fun containsFunc(name: String): Boolean {
        return functions.containsKey(name)
    }

    companion object {
        fun empty() = RuntimeContext(mutableMapOf(), mutableMapOf())
    }
}