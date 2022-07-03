package com.bkahlert.kommons

private val nextGraphemeClusterBreak = js("require('@stdlib/string-next-grapheme-cluster-break')")

/** Returns a sequence yielding the [Grapheme] instances this string consists of. */
public actual fun String.asGraphemeSequence(): Sequence<Grapheme> {
    if (isEmpty()) return emptySequence()
    var index = 0
    return sequence {
        while (true) {
            val breakIndex = nextGraphemeClusterBreak(this@asGraphemeSequence, index) as Int
            if (breakIndex == -1) {
                yield(Grapheme(this@asGraphemeSequence.substring(index, this@asGraphemeSequence.length)))
                break
            }
            yield(Grapheme(this@asGraphemeSequence.substring(index, breakIndex)))
            index = breakIndex
        }
    }
}
