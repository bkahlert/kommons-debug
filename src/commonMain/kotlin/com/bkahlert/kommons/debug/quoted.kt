package com.bkahlert.kommons.debug

/** Contains this string escaped and wrapped with double quotes. */
public val CharSequence.quoted: String get() = buildString { this@quoted.quoteTo(this) }

/** Appends this string escaped and wrapped with double quotes to the specified [out]. */
private fun CharSequence.quoteTo(out: StringBuilder) {
    out.append("\"")
    for (element in this) {
        when (element) {
            '\\' -> out.append("\\\\")
            '\n' -> out.append("\\n")
            '\r' -> out.append("\\r")
            '\t' -> out.append("\\t")
            '\"' -> out.append("\\\"")
            else -> out.append(element)
        }
    }
    out.append("\"")
}
