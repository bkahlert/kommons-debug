package com.bkahlert.kommons

import com.ibm.icu.text.BreakIterator

/** Returns a sequence yielding the [Grapheme] instances this string consists of. */
public actual fun String.asGraphemeSequence(): Sequence<Grapheme> {
    if (isEmpty()) return emptySequence()
    val iterator = BreakIterator.getCharacterInstance()
    iterator.setText(this)
    var index = iterator.first()
    return sequence {
        while (true) {
            val breakIndex = iterator.next()
            if (breakIndex == BreakIterator.DONE) break
            yield(Grapheme(this@asGraphemeSequence.substring(index, breakIndex)))
            index = breakIndex
        }
    }
}
