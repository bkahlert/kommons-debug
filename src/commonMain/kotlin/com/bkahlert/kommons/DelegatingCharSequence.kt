package com.bkahlert.kommons

/**
 * A [CharSequence] that delegates to the specified [delegate]
 * optionally sub-sequenced with the specified [range].
 */
public open class DelegatingCharSequence(
    protected var delegate: CharSequence,
    protected val range: IntRange? = null,
) : CharSequence {

    init {
        range?.also { checkBoundsIndexes(delegate.length, it.first, it.last + 1) }
    }

    final override val length: Int
        get() = range?.run { if (first > last) first - last + 1 else last - first + 1 } ?: delegate.length

    private inline val start: Int get() = range?.start ?: 0

    override fun get(index: Int): Char {
        checkBoundsIndex(indices, index)
        return delegate[start + index]
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        checkBoundsIndexes(length, startIndex, endIndex)
        if (startIndex == endIndex) return String.EMPTY
        return DelegatingCharSequence(delegate, start.let { (it + startIndex).until(it + endIndex) })
    }

    /** Returns the [delegate] sub-sequenced with the [range]. */
    final override fun toString(): String =
        range?.let { delegate.substring(it) } ?: delegate.toString()

    /** Returns `true` if the specified [other] is a [DelegatingCharSequence] and the return values of [toString] are equal. */
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as DelegatingCharSequence

        return toString() == other.toString()
    }

    /** Returns the hash code of [toString]. */
    final override fun hashCode(): Int = toString().hashCode()
}
