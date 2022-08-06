package com.bkahlert.kommons.text

import com.bkahlert.kommons.text.Text.ChunkedText
import kotlin.Char

/** Text unit for texts consisting of [Char] chunks. */
public object Char : TextUnit<Char> {
    override val name: String = "character"
    override fun textOf(text: CharSequence): Text<Char> =
        if (text.isEmpty()) Text.emptyText() else ChunkedText(text, text.indices.map { it..it }.toList(), CharSequence::get)
}
