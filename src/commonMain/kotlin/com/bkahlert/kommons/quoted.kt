package com.bkahlert.kommons

/** Contains this string escaped and wrapped with double quotes. */
public val Char.quoted: String get() = buildString { this@quoted.quoteTo(this) }

/** Contains this string escaped and wrapped with double quotes. */
public val CharSequence.quoted: String get() = buildString { this@quoted.quoteTo(this) }

/** Appends this character escaped and wrapped with double quotes to the specified [out]. */
private fun Char.quoteTo(out: StringBuilder) {
    out.append("\"")
    escapeTo(out)
    out.append("\"")
}

/** Appends this string escaped and wrapped with double quotes to the specified [out]. */
private fun CharSequence.quoteTo(out: StringBuilder) {
    out.append("\"")
    for (element in this) element.escapeTo(out)
    out.append("\"")
}

/** Appends this character escaped to the specified [out]. */
@Suppress("NOTHING_TO_INLINE")
private inline fun Char.escapeTo(out: StringBuilder) {
    when (this) {
        '\\' -> out.append("\\\\")
        '\n' -> out.append("\\n")
        '\r' -> out.append("\\r")
        '\t' -> out.append("\\t")
        '\"' -> out.append("\\\"")
        else -> out.append(this)
    }
}
