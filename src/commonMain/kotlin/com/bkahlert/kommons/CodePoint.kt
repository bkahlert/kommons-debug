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
    override fun compareTo(other: CodePoint): Int = index.compareTo(other.index)
    override fun toString(): String = string

    /**
     * Determines if this code point is one of the 10 digits `0`-`9`.
     */
    public val is0to9: Boolean get() = index in 0x30..0x39

    /**
     * Determines if this code point is one of the 26 upper case characters `A`-`Z`.
     */
    public val isAtoZ: Boolean get() = index in 0x41..0x5a

    /**
     * Determines if this code point is one of the 26 lower case characters `a`-`z`.
     */
    @Suppress("SpellCheckingInspection")
    public val isatoz: Boolean
        get() = index in 0x61..0x7a

    /**
     * Determines if this code point is one of
     * the 26 upper case characters `A`-`Z` or
     * the 26 lower case characters `a`-`z`.
     */
    @Suppress("SpellCheckingInspection")
    public val isAtoz: Boolean
        get() = isAtoZ || isatoz

    /**
     * Determines if this code point is an alphanumeric ASCII character, that is,
     * is `A`-`Z`, `a`-`z` or `0`-`9`.
     *
     * @return `true` if this code point is between a high surrogate
     * @see isHighSurrogate
     */
    public val isAsciiAlphanumeric: Boolean get() = is0to9 || isAtoZ || isatoz

    /**
     * Determines if this code point is alphanumeric, that is,
     * if it is a [Unicode Letter](https://www.unicode.org/glossary/#letter) or
     * a [Unicode Digit](http://www.unicode.org/glossary/#digits).
     *
     * @return `true` if this code point is a letter or digit
     * @see <a href="https://www.unicode.org/reports/tr18/#property_syntax">Unicode® Technical Standard #18—UNICODE REGULAR EXPRESSIONS</a>
     */
    public val isAlphanumeric: Boolean get() = Regex("[\\p{L} \\p{Nd}]").matches(string)
}

/** Contains the character pointed to and represented by a [String]. */
public expect val CodePoint.string: String

/** Contains the [Char] representing this code point **if** it can be represented by a single [Char]. */
public val CodePoint.char: Char? get() = index.takeIf { it in Char.MIN_VALUE.code..Char.MAX_VALUE.code }?.toChar()

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
