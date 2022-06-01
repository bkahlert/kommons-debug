package com.bkahlert.kommons.debug

import com.bkahlert.kommons.Unicode
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
public inline val <T> T.traceJs: T get(): T = traceJs()

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
@Deprecated("Don't forget to remove after you finished debugging.", replaceWith = ReplaceWith("this")) @Suppress("GrazieInspection", "DEPRECATION")
public inline fun <T> T.traceJs(
    caption: CharSequence? = null,
    includeCallSite: Boolean = true,
    render: (Any?) -> String = { it.render() },
    noinline out: ((String) -> Unit)? = null,
    noinline transform: ((T) -> Any?)? = null,
): T {

    val appendWrapped: (StringBuilder, String, Pair<String, String>) -> Unit = { sb, value, brackets ->
        val separator = if (value.isMultiline) Unicode.LINE_FEED else ' '
        sb.append(brackets.first)
        sb.append(separator)
        sb.append(value)
        sb.append(separator)
        sb.append(brackets.second)
    }

    buildString {
        if (caption != null) {
            append(caption)
            append(" ")
        }
        appendWrapped(
            this,
            render(this@traceJs),
            "⟨" to "⟩",
        )
        transform?.also {
            append(" ")
            appendWrapped(
                this,
                render(transform(this@traceJs)),
                "{" to "}",
            )
        }
    }.also {
        out?.invoke(it) ?: run {
            if (includeCallSite) console.trace(it)
            else console.log(it)
        }
    }
    return this
}
