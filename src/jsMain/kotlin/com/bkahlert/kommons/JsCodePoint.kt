package com.bkahlert.kommons

/**
 * Contains the character pointed to and represented by a [String].
 */
public actual val CodePoint.string: String
    get() = if (index < 0x10000) {
        index.toChar().toString()
    } else {
        val off = 0xD800 - (0x10000 shr 10)
        val high = off + (index shr 10)
        val low = 0xDC00 + (index and 0x3FF)
        "${high.toChar()}${low.toChar()}"
    }

@Suppress("SpellCheckingInspection")
private val XRegExp = js("require('xregexp')")
private val letterRegexp = XRegExp("^\\p{L}$")
private val digitRegexp = XRegExp("^\\p{N}$")
private val whitespaceRegexp = XRegExp("^\\p{Zs}$")

/** Whether this code point is a letter. */
public actual val CodePoint.isLetter: Boolean
    get() = letterRegexp.test(string) as Boolean

/** Whether this code point is a digit. */
public actual val CodePoint.isDigit: Boolean
    get() = digitRegexp.test(index.toChar().toString()) as Boolean

/** Whether this code point is a [Unicode Space Character](http://www.unicode.org/versions/Unicode13.0.0/ch06.pdf). */
public actual val CodePoint.isWhitespace: Boolean
    get() = whitespaceRegexp.test(index.toChar().toString()) as Boolean

private fun makeCharFromSurrogatePair(high: Char, low: Char): Int {
    val highInt = high.code
    val lowInt = low.code
    check(highInt in 0xD800..0xDBFF) { "high character is outside valid range: 0x${highInt.toString(16)}" }
    check(lowInt in 0xDC00..0xDFFF) { "high character is outside valid range: 0x${lowInt.toString(16)}" }
    val off = 0x10000 - (0xD800 shl 10) - 0xDC00
    return (highInt shl 10) + lowInt + off
}

/** Returns a sequence yielding the [CodePoint] instances this string consists of. */
public actual fun String.asCodePointSequence(): Sequence<CodePoint> {
    if (isEmpty()) return emptySequence()
    var index = 0
    return sequence {
        while (index < this@asCodePointSequence.length) {
            val ch = this@asCodePointSequence[index++]
            val v = when {
                ch.isHighSurrogate() -> {
                    val low = if (index < this@asCodePointSequence.length) this@asCodePointSequence[index++] else null
                    if (low?.isLowSurrogate() == true) {
                        makeCharFromSurrogatePair(ch, low)
                    } else {
                        ch.code
//                        throw IllegalStateException("Expected low surrogate, got: ${low.code}")
                    }
                }
                ch.isLowSurrogate() -> {
                    ch.code
//                    throw IllegalStateException("Standalone low surrogate found: ${ch.code}")
                }
                else -> ch.code
            }
            yield(CodePoint(v))
        }
    }
}

/** Returns the number of Unicode code points in the specified text range of this string. */
public actual fun String.codePointCount(startIndex: Int, endIndex: Int): Int {
    if (endIndex < startIndex || startIndex < 0 || endIndex > length) throw IndexOutOfBoundsException("begin $startIndex, end $endIndex, length $length")
    val substring = substring(startIndex, endIndex)
    if (substring.isEmpty()) return 0
    return substring.asCodePointSequence().count()
}
