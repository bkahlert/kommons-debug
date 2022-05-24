package com.bkahlert.kommons.debug

/**
 * Renders this object depending on whether its `toString()` is overridden:
 * - if there is a custom `toString()` it is simply used
 * - if there is *no custom* `toString()` the object is serialized in the form `<TYPE>(key0=value0, key1=value1, ..., keyN=valueN)`
 */
public fun Any?.render(
    separator: CharSequence = ", ",
    prefix: CharSequence = "(",
    postfix: CharSequence = ")",
): String = buildString { this@render.renderTo(this, separator, prefix, postfix) }

/**
 * Renders this object depending on whether its `toString()` is overridden to the specified [out]:
 * - if there is a custom `toString()` it is simply used
 * - if there is *no custom* `toString()` the object is serialized in the form `<TYPE>(key0=value0, key1=value1, ..., keyN=valueN)`
 */
public fun Any?.renderTo(
    out: StringBuilder,
    separator: CharSequence = ", ",
    prefix: CharSequence = "(",
    postfix: CharSequence = ")",
    nesting: UInt = 0u,
    rendered: MutableSet<Any?> = mutableSetOf(),
) {
    when (this) {
        null -> out.append("␀")
        is CharSequence -> {
            out.append(quoted)
            // TODO port kommons .debug here
        }
        // TODO lists
        else -> {
            if (rendered.contains(this)) {
                out.append("<")
                renderTypeTo(out)
                out.append(">")
            } else {
                rendered.add(this)
                toCustomStringOrNull()?.let { out.append(it) } ?: run {
                    renderTypeTo(out)
                    out.append(prefix)
                    properties.entries.forEachIndexed { index, (key, value) ->
                        if (index != 0) out.append(separator)
                        out.append(key)
                        out.append("=")
                        value.renderTo(out, separator, prefix, postfix, nesting + 1u, rendered)
                    }
                    listOf<String>().joinToString()
                    out.append(postfix)
                }
            }
        }
    }
}
// TODO nesting -> indent
