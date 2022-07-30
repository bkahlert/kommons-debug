package com.bkahlert.kommons

/**
 * A [CharSequence] that acts as if the specified [delegates]
 * were concatenated.
 */
public open class ComposingCharSequence(
    private val delegates: List<CharSequence>,
) : CharSequence {
    public constructor(vararg delegates: CharSequence) : this(delegates.asList())

    override val length: Int
        get() = delegates.sumOf { it.length }

    protected fun locate(index: Int): Pair<Int, Int> {
        var delegateIndex = 0
        var localIndex = index
        for (delegate in delegates) {
            if (localIndex >= delegate.length) {
                delegateIndex++
                localIndex -= delegate.length
            } else {
                break
            }
        }
        return delegateIndex to localIndex
    }

    override fun get(index: Int): Char {
        checkBoundsIndex(indices, index)
        val (delegateIndex, localIndex) = locate(index)
        return delegates[delegateIndex][localIndex]
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        checkBoundsIndexes(length, startIndex, endIndex)
        if (startIndex == endIndex) return String.EMPTY
        val (delegateStartIndex, localStartIndex) = locate(startIndex)
        val (delegateEndIndex, localEndIndex) = locate(endIndex - 1)
        return when (delegateEndIndex - delegateStartIndex) {
            0 -> DelegatingCharSequence(delegates[delegateStartIndex], localStartIndex..localEndIndex)
            1 -> ComposingCharSequence(
                delegates[delegateStartIndex].let { DelegatingCharSequence(it, localStartIndex until it.length) },
                delegates[delegateEndIndex].let { DelegatingCharSequence(it, 0..localEndIndex) },
            )

            else -> ComposingCharSequence(
                buildList {
                    add(delegates[delegateStartIndex].let { DelegatingCharSequence(it, localStartIndex until it.length) })
                    for (i in delegateStartIndex + 1 until delegateEndIndex) add(delegates[i])
                    add(delegates[delegateEndIndex].let { DelegatingCharSequence(it, 0..localEndIndex) })
                }
            )
        }
    }

    /** Returns the [delegate] sub-sequenced with the [range]. */
    final override fun toString(): String =
        delegates.joinToString(String.EMPTY)

    /** Returns `true` if the specified [other] is a [ComposingCharSequence] and the return values of [toString] are equal. */
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ComposingCharSequence

        return toString() == other.toString()
    }

    /** Returns the hash code of [toString]. */
    final override fun hashCode(): Int = toString().hashCode()
}
