package ru.spbau.mit.exception

class DoubleDefinitionException(message: String) : RuntimeException(message)

class VarIsNotInScopeException(message: String) : RuntimeException(message)

class FuncIsNotInScopeException(message: String) : RuntimeException(message)

class IllegalNumberOfArguments(message: String) : RuntimeException(message)