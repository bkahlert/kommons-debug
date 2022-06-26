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
    public val index: Int,
) : Comparable<CodePoint> {
    init {
        if (index !in INDEX_RANGE) throw IndexOutOfBoundsException("index out of range $INDEX_RANGE: $index")
    }

    /** Returns the code point with the [index] increased by the specified [offset]. */
    public operator fun plus(offset: Int): CodePoint = CodePoint(index + offset)

    /** Returns the code point with the [index] decreased by the specified [offset]. */
    public operator fun minus(offset: Int): CodePoint = CodePoint(index - offset)

    /** Returns the code point with the [index] increased by `1`. */
    public operator fun inc(): CodePoint = CodePoint(index + 1)

    /** Returns the code point with the [index] decreased by `1`. */
    public operator fun dec(): CodePoint = CodePoint(index - 1)

    /** Returns a new [CodePointRange] starting with this code point and ending with the specified [endInclusive] code point. */
    public operator fun rangeTo(endInclusive: CodePoint): CodePointRange = CodePointRange(this, endInclusive)

    override fun compareTo(other: CodePoint): Int = index.compareTo(other.index)
    override fun toString(): String = string

    /** The [Char] representing this code point **if** it can be represented by a single [Char]. */
    public val char: Char? get() = index.takeIf { it in Char.MIN_VALUE.code..Char.MAX_VALUE.code }?.toChar()

    /** The 16-bit Unicode characters needed to encode. */
    public val chars: CharArray get() = string.toCharArray()

    /** The number of 16-bit Unicode characters needed to encode. */
    public val charCount: Int get() = chars.size

    /** Whether this code point is one of the 10 digits `0`-`9`. */
    public val is0to9: Boolean get() = index in 0x30..0x39

    /** Whether this code point is one of the 26 upper case characters `A`-`Z`. */
    public val isAtoZ: Boolean get() = index in 0x41..0x5a

    /** Whether this code point is one of the 26 lower case characters `a`-`z`. */
    @Suppress("SpellCheckingInspection")
    public val isatoz: Boolean get() = index in 0x61..0x7a

    /**
     * Whether this code point is one of
     * the 26 upper case characters `A`-`Z` or
     * the 26 lower case characters `a`-`z`.
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
     * if it is a [Unicode Letter](https://www.unicode.org/glossary/#letter) or
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

/** Contains the character pointed to and represented by a [String]. */
public expect val CodePoint.string: String

/** Returns the Unicode code point with the same value. */
public inline val Char.codePoint: CodePoint get() = CodePoint(code)

/** Returns the Unicode code point with the same value. */
public fun Byte.asCodePoint(): CodePoint = CodePoint(toInt() and 0xFF)

/** Whether this code point is a letter. */
public expect val CodePoint.isLetter: Boolean

/** Whether this code point is a digit. */
public expect val CodePoint.isDigit: Boolean

/** Whether this code point is a [Unicode Space Character](http://www.unicode.org/versions/Unicode13.0.0/ch06.pdf). */
public expect val CodePoint.isWhitespace: Boolean

/** Returns a sequence yielding the [CodePoint] instances this string consists of. */
public expect fun String.asCodePointSequence(): Sequence<CodePoint>

/** Returns a list containing the [CodePoint] instances this string consists of. */
public fun String.toCodePointList(): List<CodePoint> = asCodePointSequence().toList()

/** Returns the number of Unicode code points in the specified text range of this string. */
public expect fun String.codePointCount(beginIndex: Int = 0, endIndex: Int = length): Int

/** A closed range of code points. */
public class CodePointRange(
    override val start: CodePoint,
    override val endInclusive: CodePoint,
) : ClosedRange<CodePoint>, Iterable<CodePoint> {
    private val iterable = asIterable { it + 1 }
    override fun iterator(): Iterator<CodePoint> = iterable.iterator()
}
