package com.bkahlert.kommons

private val nextGraphemeClusterBreak = js("require('@stdlib/string-next-grapheme-cluster-break')")

/** An [Iterator] that iterates [Grapheme] positions. */
public actual class GraphemePositionIterator actual constructor(
    private val text: CharSequence,
) : PositionIterator by text.asGraphemeIndicesSequence().iterator()

private fun CharSequence.asGraphemeIndicesSequence(): Sequence<IntRange> {
    if (isEmpty()) return emptySequence()
    var index = 0
    return sequence {
        while (true) {
            val breakIndex = nextGraphemeClusterBreak(this@asGraphemeIndicesSequence, index) as Int
            if (breakIndex == -1) {
                yield(index until this@asGraphemeIndicesSequence.length)
                break
            }
            yield(index until breakIndex)
            index = breakIndex
        }
    }
}
