package com.bkahlert.kommons.debug

public fun Any?.render(
    separator: CharSequence = ", ",
    prefix: CharSequence = "(",
    postfix: CharSequence = ")",
): String = buildString { this@render.renderTo(this, separator, prefix, postfix) }

public fun Any?.renderTo(
    out: StringBuilder,
    separator: CharSequence = ", ",
    prefix: CharSequence = "(",
    postfix: CharSequence = ")",
) {
    when (this) {
        null -> out.append("␀")
        is CharSequence -> {
            out.append("\"")
            out.append(this)
            out.append("\"")
        }
        else -> toCustomStringOrNull()?.let { out.append(it) } ?: run {
            out.append(renderType())
            out.append(prefix)
            properties.entries.forEachIndexed { index, (key, value) ->
                if (index != 0) out.append(separator)
                out.append(key)
                out.append("=")
                value.renderTo(out)
            }
            listOf<String>().joinToString()
            out.append(postfix)
        }
    }
}

public fun Any.renderType(): String {
    return this::class.simpleName!!
}
