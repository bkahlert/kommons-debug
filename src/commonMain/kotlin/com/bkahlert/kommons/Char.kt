package com.bkahlert.kommons

import com.bkahlert.kommons.Text.ChunkedText

/** Text unit for texts consisting of [Char] chunks. */
public object Char : TextUnit<kotlin.Char> {
    override val name: String = "character"
    override fun textOf(text: CharSequence): Text<kotlin.Char> =
        if (text.isEmpty()) Text.emptyText() else ChunkedText(text, text.indices.map { it..it }.toList(), CharSequence::get)
}
