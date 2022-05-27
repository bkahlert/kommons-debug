package com.bkahlert.kommons.debug

import com.bkahlert.kommons.debug.JsStackTraceElement.Companion.FunctionMangleRegex
import com.bkahlert.kommons.takeUnlessEmpty
import kotlin.reflect.KFunction

/** Representation of a single element of a [StackTrace] on [Platform.JS]. */
public data class JsStackTraceElement(
    override val receiver: String?,
    override val function: String?,
    override val file: String,
    override val line: Int,
    override val column: Int,
) : StackTraceElement {

    override fun toString(): String = buildString {
        var closeBracket = false
        if (receiver != null || function != null) {
            closeBracket = true
            if (receiver != null) {
                append(receiver)
                append('.')
            }
            if (function != null) {
                append(function)
            }
            append(" (")
        }
        append(file)
        append(':')
        append(line)
        append(':')
        append(column)
        if (closeBracket) {
            append(')')
        }
    }

    public companion object {
        private val RenderedStackTraceElementRegex =
            "^(?:(?:(?<receiver>[^. ]*)\\.)?(?<function>[^. ]*)?\\s+)?\\(?(?<file>[^()]+):(?<line>\\d+):(?<column>\\d+)\\)?\$".toRegex()

        public val FunctionMangleRegex: Regex = "_[a-z\\d]+_k\\$$".toRegex()

        public fun parseOrNull(renderedStackTraceElement: String): JsStackTraceElement? = RenderedStackTraceElementRegex
            .matchEntire(renderedStackTraceElement)
            ?.destructured
            ?.let { (receiver, function, file, line, column) ->
                JsStackTraceElement(
                    receiver.takeUnlessEmpty(),
                    function.takeUnlessEmpty(),
                    file,
                    line.toInt(),
                    column.toInt()
                )
            }
    }
}

public val StackTraceElement.demangledFunction: String? get() = function?.replace(FunctionMangleRegex, "")

/** Gets the current [StackTrace]. */
@Suppress("NOTHING_TO_INLINE") // = avoid impact on stack trace
public actual inline fun StackTrace.Companion.get(): StackTrace {
    val stackTraceElementSeparatorRegex = "\\n\\s+at ".toRegex()
    return try {
        throw RuntimeException()
    } catch (ex: Throwable) {
        ex.stackTraceToString().removeSuffix("\n")
    }.splitToSequence(regex = stackTraceElementSeparatorRegex)
        .dropWhile { it == "RuntimeException" }
        .mapNotNull { JsStackTraceElement.parseOrNull(it) }
        .let { StackTrace(it) }
}

/**
 * Finds the [StackTraceElement] that represents the caller
 * invoking the [StackTraceElement] matching a call to the specified [function].
 */
@Suppress("NOTHING_TO_INLINE") // = avoid impact on stack trace
public actual inline fun StackTrace.Companion.findByLastKnownCallOrNull(function: String): StackTraceElement? {
    var skipNull = false
    val demangledFunction = function.replace(FunctionMangleRegex, "")
    return findOrNull {
        if (it.demangledFunction == demangledFunction) {
            skipNull = true
            true
        } else {
            if (skipNull) {
                it.receiver == null && it.function == null
            } else false
        }
    }
}

/**
 * Finds the [StackTraceElement] that represents the caller
 * invoking the [StackTraceElement] matching a call to the specified [function].
 */
@Suppress("NOTHING_TO_INLINE") // = avoid impact on stack trace
public actual inline fun StackTrace.Companion.findByLastKnownCallOrNull(function: KFunction<*>): StackTraceElement? =
    findByLastKnownCallOrNull(function.name)
