package com.bkahlert.kommons

import kotlin.jvm.JvmInline

/**
 * Representation of a [Unicode code point](https://unicode.org/glossary/#code_point)
 *
 * @see <a href="https://unicode.org/reports/tr18/">Unicode® Technical Standard #18—UNICODE REGULAR EXPRESSIONS</a>
 */
@JvmInline
public value class CodePoint(
    /**
     * Index of this code point in the [Unicode](http://www.unicode.org/) table.
     */
    public val value: Int,
) : Comparable<CodePoint>, CharSequence {
    public constructor(char: Char) : this(char.code)
    public constructor(high: Char, low: Char) : this(makeCharFromSurrogatePair(high, low))

    init {
        if (value !in INDEX_RANGE) throw IndexOutOfBoundsException("index out of range $INDEX_RANGE: $value")
    }

    /** Returns the code point with the [value] increased by the specified [offset]. */
    public operator fun plus(offset: Int): CodePoint = CodePoint(value + offset)

    /** Returns the code point with the [value] decreased by the specified [offset]. */
    public operator fun minus(offset: Int): CodePoint = CodePoint(value - offset)

    /** Returns the code point with the [value] increased by `1`. */
    public operator fun inc(): CodePoint = CodePoint(value + 1)

    /** Returns the code point with the [value] decreased by `1`. */
    public operator fun dec(): CodePoint = CodePoint(value - 1)

    /** Returns a new [CodePointRange] starting with this code point and ending with the specified [endInclusive] code point. */
    public operator fun rangeTo(endInclusive: CodePoint): CodePointRange = CodePointRange(this, endInclusive)

    override fun compareTo(other: CodePoint): Int = value.compareTo(other.value)

    override val length: Int get() = string.length
    override fun get(index: Int): Char = string[index]
    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = string.subSequence(startIndex, endIndex)
    override fun toString(): String = string

    /** The [Char] representing this code point **if** it can be represented by a single [Char]. */
    public val char: Char? get() = value.takeIf { it in Char.MIN_VALUE.code..Char.MAX_VALUE.code }?.toChar()

    /** The 16-bit Unicode characters needed to encode. */
    public val chars: CharArray get() = string.toCharArray()

    /** The number of 16-bit Unicode characters needed to encode. */
    public val charCount: Int get() = chars.size

    /** Whether this code point is one of the 10 digits `0`-`9`. */
    public val is0to9: Boolean get() = value in 0x30..0x39

    /** Whether this code point is one of the 26 uppercase characters `A`-`Z`. */
    public val isAtoZ: Boolean get() = value in 0x41..0x5a

    /** Whether this code point is one of the 26 lowercase characters `a`-`z`. */
    @Suppress("SpellCheckingInspection")
    public val isatoz: Boolean get() = value in 0x61..0x7a

    /**
     * Whether this code point is one of
     * the 26 uppercase characters `A`-`Z` or
     * the 26 lowercase characters `a`-`z`.
     */
    @Suppress("SpellCheckingInspection")
    public val isAtoz: Boolean get() = isAtoZ || isatoz

    /**
     * Whether this code point is an alphanumeric ASCII character, that is,
     * is `A`-`Z`, `a`-`z` or `0`-`9`.
     */
    public val isAsciiAlphanumeric: Boolean get() = is0to9 || isAtoZ || isatoz

    /**
     * Whether this code point is alphanumeric, that is,
     * if it's a [Unicode Letter](https://www.unicode.org/glossary/#letter) or
     * a [Unicode Digit](http://www.unicode.org/glossary/#digits).
     */
    public val isAlphanumeric: Boolean get() = Regex("[\\p{L} \\p{Nd}]").matches(string)

    public companion object {
        /** The minimum index a code point can have. */
        public const val MIN_INDEX: Int = 0x0

        /** The maximum index a code point can have. */
        public const val MAX_INDEX: Int = 0x10FFFF

        /** The range of indices a code point can have. */
        public val INDEX_RANGE: IntRange = MIN_INDEX..MAX_INDEX
    }
}

/** An [Iterator] that iterates [CodePoint] positions. */
public class CodePointPositionIterator(
    private val text: CharSequence,
    private val throwOnInvalidSequence: Boolean = false,
) : PositionIterator {
    private var index = 0
    override fun hasNext(): Boolean = index < text.length

    override fun next(): IntRange {
        val ch = text[index]
        return when {
            ch.isHighSurrogate() -> {
                val low = if (index + 1 < text.length) text[index + 1] else null
                if (low?.isLowSurrogate() == true) {
                    index += 2
                    (index - 2) until index
                } else {
                    if (throwOnInvalidSequence) throw CharacterCodingException(index)
                    else {
                        index++
                        index - 1 until index
                    }
                }
            }

            ch.isLowSurrogate() -> {
                if (throwOnInvalidSequence) throw CharacterCodingException(index)
                else {
                    index++
                    index - 1 until index
                }
            }

            else -> {
                index++
                index - 1 until index
            }
        }
    }
}

/** An [Iterator] that iterates [CodePoint] instances. */
public class CodePointIterator(
    private val text: CharSequence,
    private val throwOnInvalidSequence: Boolean = false,
) : ChunkIterator<CodePoint> by ChunkingIterator(CodePointPositionIterator(text, throwOnInvalidSequence), {
    val sub = text.subSequence(it)
    when (sub.length) {
        1 -> CodePoint(sub[0])
        2 -> CodePoint(sub[0], sub[1])
        else -> error("cannot convert $sub to code point")
    }
})

/** The character pointed to and represented by a [String]. */
internal expect val CodePoint.string: String

/** Returns the Unicode code point with the same value. */
public inline val Char.codePoint: CodePoint get() = CodePoint(code)

/** Returns the Unicode code point with the same value. */
public fun Byte.asCodePoint(): CodePoint = CodePoint(toInt() and 0xFF)

/** Returns the Unicode code point with the same value or throws an [IllegalArgumentException] otherwise. */
public fun CharSequence.asCodePoint(): CodePoint = asCodePointOrNull() ?: throw IllegalArgumentException("invalid code point: $this")

/** Returns the Unicode code point with the same value, or `null` otherwise. */
public fun CharSequence.asCodePointOrNull(): CodePoint? = asCodePointSequence().singleOrNull()

/** Whether this code point is a letter. */
public expect val CodePoint.isLetter: Boolean

/** Whether this code point is a digit. */
public expect val CodePoint.isDigit: Boolean

/** Whether this code point is a [Unicode Space Character](http://www.unicode.org/versions/Unicode13.0.0/ch06.pdf). */
public expect val CodePoint.isWhitespace: Boolean

internal expect fun CharacterCodingException(inputLength: Int): CharacterCodingException

private fun makeCharFromSurrogatePair(high: Char, low: Char): Int {
    check(high in Char.MIN_HIGH_SURROGATE..Char.MAX_HIGH_SURROGATE) { "high character is outside valid range: 0x${high.code.toString(16)}" }
    check(low in Char.MIN_LOW_SURROGATE..Char.MAX_LOW_SURROGATE) { "high character is outside valid range: 0x${low.code.toString(16)}" }
    val off = 0x10000 - (Char.MIN_HIGH_SURROGATE.code shl 10) - Char.MIN_LOW_SURROGATE.code
    return (high.code shl 10) + low.code + off
}

/** Returns a sequence yielding the indices describing the [CodePoint] instances contained in the specified text range of this string. */
public fun CharSequence.asCodePointIndicesSequence(
    startIndex: Int = 0,
    endIndex: Int = length,
    throwOnInvalidSequence: Boolean = false,
): Sequence<IntRange> {
    checkBoundsIndexes(length, startIndex, endIndex)
    if (isEmpty()) return emptySequence()
    var index = startIndex
    return sequence {
        while (index < endIndex) {
            val ch = this@asCodePointIndicesSequence[index]
            when {
                ch.isHighSurrogate() -> {
                    val low = if (index + 1 < endIndex) this@asCodePointIndicesSequence[index + 1] else null
                    if (low?.isLowSurrogate() == true) {
                        yield(index..index + 1)
                        index += 2
                    } else {
                        if (throwOnInvalidSequence) throw CharacterCodingException(index)
                        else yield(index..index)
                        index++
                    }
                }

                ch.isLowSurrogate() -> {
                    if (throwOnInvalidSequence) throw CharacterCodingException(index)
                    else yield(index..index)
                    index++
                }

                else -> {
                    yield(index..index)
                    index++
                }
            }
        }
    }
}

/** Returns a sequence yielding the [CodePoint] instances contained in the specified text range of this string. */
public fun CharSequence.asCodePointSequence(
    startIndex: Int = 0,
    endIndex: Int = length,
    throwOnInvalidSequence: Boolean = false,
): Sequence<CodePoint> =
    asCodePointIndicesSequence(startIndex, endIndex, throwOnInvalidSequence).map { indices ->
        if (indices.last > indices.first) {
            CodePoint(makeCharFromSurrogatePair(get(indices.first), get(indices.last)))
        } else {
            get(indices.first).codePoint
        }
    }

/** Returns the [CodePoint] instances contained in the specified text range of this string. */
public fun CharSequence.toCodePointList(
    startIndex: Int = 0,
    endIndex: Int = length,
    throwOnInvalidSequence: Boolean = false,
): List<CodePoint> = asCodePointSequence(startIndex, endIndex, throwOnInvalidSequence).toList()

/** Returns the number of Unicode code points contained in the specified text range of this string. */
public fun CharSequence.codePointCount(
    startIndex: Int = 0,
    endIndex: Int = length,
    throwOnInvalidSequence: Boolean = false,
): Int = asCodePointIndicesSequence(startIndex, endIndex, throwOnInvalidSequence).count()

/** A closed range of code points. */
public class CodePointRange(
    override val start: CodePoint,
    override val endInclusive: CodePoint,
) : ClosedRange<CodePoint>, Iterable<CodePoint> {
    private val iterable = asIterable { it + 1 }
    override fun iterator(): Iterator<CodePoint> = iterable.iterator()
}
