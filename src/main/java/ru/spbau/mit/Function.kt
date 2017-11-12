package ru.spbau.mit

import ru.spbau.mit.parser.FunParser

data class Function(
        val rCtx: MutableList<RuntimeContext>,
        val blockContext: FunParser.BlockWithBracesContext,
        val argNames: List<String>
)

