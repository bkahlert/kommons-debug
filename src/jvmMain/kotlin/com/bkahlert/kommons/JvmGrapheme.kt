package com.bkahlert.kommons

import com.ibm.icu.text.BreakIterator

/** An [Iterator] that iterates [Grapheme] positions. */
public actual class GraphemePositionIterator actual constructor(
    private val text: CharSequence,
) : PositionIterator by text.asGraphemeIndicesSequence().iterator()

/** Returns a sequence yielding the [Grapheme] instances this string consists of. */
private fun CharSequence.asGraphemeIndicesSequence(): Sequence<IntRange> {
    val iterator = BreakIterator.getCharacterInstance().also { it.setText(this) }
    var index = iterator.first()
    return sequence {
        while (true) {
            val breakIndex = iterator.next()
            if (breakIndex == BreakIterator.DONE) break
            if (breakIndex > this@asGraphemeIndicesSequence.length) {
                yield(index until this@asGraphemeIndicesSequence.length)
                break
            }
            yield(index until breakIndex)
            index = breakIndex
        }
    }
}
