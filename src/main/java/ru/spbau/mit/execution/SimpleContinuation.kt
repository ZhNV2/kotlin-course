package ru.spbau.mit.execution

import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext


abstract class SimpleContinuation<T> : Continuation<T> {

    override val context: CoroutineContext
        get() = EmptyCoroutineContext

    override fun resumeWithException(exception: Throwable) {
        throw exception
    }

}