package com.bkahlert.kommons

private val nextGraphemeClusterBreak = js("require('@stdlib/string-next-grapheme-cluster-break')")

/** An [Iterator] that iterates [Grapheme] positions. */
public actual class GraphemeBreakIterator actual constructor(
    text: CharSequence,
) : BreakIterator by (text.asGraphemeIndicesSequence().iterator())

private fun CharSequence.asGraphemeIndicesSequence(): Sequence<Int> {
    if (isEmpty()) return emptySequence()
    var index = 0
    return sequence {
        while (true) {
            val breakIndex = nextGraphemeClusterBreak(this@asGraphemeIndicesSequence, index) as Int
            if (breakIndex == -1) {
                yield(this@asGraphemeIndicesSequence.length)
                break
            }
            yield(breakIndex)
            index = breakIndex
        }
    }
}
