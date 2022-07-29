package com.bkahlert.kommons

import com.ibm.icu.text.BreakIterator

/** Returns a sequence yielding the [Grapheme] instances this string consists of. */
public actual fun CharSequence.asGraphemeIndicesSequence(
    startIndex: Int,
    endIndex: Int,
): Sequence<IntRange> {
    checkBoundsIndexes(length, startIndex, endIndex)
    val iterator = BreakIterator.getCharacterInstance()
    iterator.setText(subSequence(startIndex, endIndex))
    var index = iterator.first()
    return sequence {
        while (true) {
            val breakIndex = iterator.next()
            if (breakIndex == BreakIterator.DONE) break
            if (breakIndex > endIndex) {
                yield(index + startIndex until endIndex + startIndex)
                break
            }
            yield(index + startIndex until breakIndex + startIndex)
            index = breakIndex
        }
    }
}
