package com.bkahlert.kommons.debug

import com.bkahlert.kommons.Unicode
import com.bkahlert.kommons.debug.Platform.JVM
import com.bkahlert.kommons.isMultiline

/**
 * Helper property that supports
 * [print debugging][https://en.wikipedia.org/wiki/Debugging#Print_debugging].
 *
 * **Example**
 * ```kotlin
 * chain().of.endless().trace.breaks.print().calls()
 * ```
 * … does the same as …
 *
 * ```kotlin
 * chain().of.endless().calls()
 * ```
 * … with the only difference that the return value of
 *
 * ```kotlin
 * chain().of.endless()
 * ```
 *
 * will be printed.
 */
@Suppress("GrazieInspection", "DEPRECATION")
@Deprecated("Don't forget to remove after you finished debugging.", replaceWith = ReplaceWith("this"))
public inline val <T> T.trace: T get(): T = trace()

/**
 * Helper property that supports
 * [print debugging][https://en.wikipedia.org/wiki/Debugging#Print_debugging].
 *
 * **Example**
 * ```kotlin
 * chain().of.endless().trace.breaks.print().calls()
 * ```
 * … does the same as …
 *
 * ```kotlin
 * chain().of.endless().calls()
 * ```
 * … with the only difference that the return value of
 *
 * ```kotlin
 * chain().of.endless()
 * ```
 *
 * will be printed.
 */
@Suppress("GrazieInspection", "DEPRECATION")
@Deprecated("Don't forget to remove after you finished debugging.", replaceWith = ReplaceWith("this"))
public fun <T> T.trace(
    caption: CharSequence? = null,
    highlight: Boolean = Platform.Current == JVM,
    includeCallSite: Boolean = true,
    render: (Any?) -> String = { it.render() },
    out: ((String) -> Unit)? = null,
    inspect: ((T) -> Any?)? = null
): T {
    buildString {
        if (includeCallSite) {
            StackTrace.findByLastKnownCallOrNull(::trace)?.also {
                append(".ͭ ")
                append("(${it.file}:${it.line}) ")
            }
        }
        caption?.also {
            append(caption.let { if (highlight) it.highlighted else it })
            append(' ')
        }
        appendWrapped(
            render(this@trace).let { if (highlight) it.highlightedStrongly else it },
            if (highlight) "⟨".highlighted to "⟩".highlighted else "⟨" to "⟩",
        )
        inspect?.also {
            append(" ")
            appendWrapped(
                render(inspect(this@trace)).let { if (highlight) it.highlightedStrongly else it },
                if (highlight) "{".highlighted to "}".highlighted else "{" to "}",
            )
        }
    }.also { out?.invoke(it) ?: println(it) }
    return this
}

/**
 * Special version of [trace] that inspects the structure of
 * each object, no matter if a custom [Any.toString] exists or not.
 */
@Suppress("GrazieInspection", "DEPRECATION")
@Deprecated("Don't forget to remove after you finished debugging.", replaceWith = ReplaceWith("this"))
public inline val <T> T.inspect: T get(): T = inspect()

/**
 * Special version of [trace] that inspects the structure of
 * each object, no matter if a custom [Any.toString] exists or not.
 */
@Suppress("GrazieInspection", "DEPRECATION", "NOTHING_TO_INLINE") // = avoid impact on stack trace
@Deprecated("Don't forget to remove after you finished debugging.", replaceWith = ReplaceWith("this"))
public inline fun <T> T.inspect(
    caption: CharSequence? = null,
    highlight: Boolean = Platform.Current == JVM,
    includeCallSite: Boolean = true,
    typing: Typing = Typing.SimplyTyped,
    noinline out: ((String) -> Unit)? = null,
    noinline inspect: ((T) -> Any?)? = null
): T = trace(caption, highlight, includeCallSite, { it.render(typing = typing, customToString = CustomToString.Ignore) }, out, inspect)

private fun StringBuilder.appendWrapped(value: String, brackets: Pair<String, String>) {
    val separator = if (value.isMultiline) Unicode.LINE_FEED else ' '
    append(brackets.first)
    append(separator)
    append(value)
    append(separator)
    append(brackets.second)
}
