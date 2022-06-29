package com.bkahlert.kommons

import java.text.BreakIterator

/** Returns a sequence yielding the [Grapheme] instances this string consists of. */
public actual fun String.asGraphemeSequence(): Sequence<Grapheme> {
    val iterator = BreakIterator.getCharacterInstance()
    iterator.setText(this)
    var start = iterator.first()
    return sequence {
        while (true) {
            val end = iterator.next()
            if (end == BreakIterator.DONE) break
            yield(Grapheme(this@asGraphemeSequence.substring(start, end)))
            start = end
        }
    }
}
