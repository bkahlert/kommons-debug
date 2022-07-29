package com.bkahlert.kommons

private val nextGraphemeClusterBreak = js("require('@stdlib/string-next-grapheme-cluster-break')")

/** Returns a sequence yielding the [Grapheme] instances this string consists of. */
public actual fun CharSequence.asGraphemeIndicesSequence(
    startIndex: Int,
    endIndex: Int,
): Sequence<IntRange> {
    checkBoundsIndexes(length, startIndex, endIndex)
    val subSequence = subSequence(startIndex, endIndex)
    if (subSequence.isEmpty()) return emptySequence()
    var index = 0
    return sequence {
        while (true) {
            val breakIndex = nextGraphemeClusterBreak(subSequence, index) as Int
            if (breakIndex == -1) {
                yield(index + startIndex until subSequence.length + startIndex)
                break
            }
            yield(index + startIndex until breakIndex + startIndex)
            index = breakIndex
        }
    }
}
