package com.bkahlert.kommons

public actual val CodePoint.string: String
    get() = if (index < 0x10000) {
        index.toChar().toString()
    } else {
        val off = 0xD800 - (0x10000 shr 10)
        val high = off + (index shr 10)
        val low = 0xDC00 + (index and 0x3FF)
        "${high.toChar()}${low.toChar()}"
    }

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
    var i = 0
    return sequence {
        while (i < this@asCodePointSequence.length) {
            val ch = this@asCodePointSequence[i++]
            val v = when {
                ch.isHighSurrogate() -> {
                    val low = this@asCodePointSequence[i++]
                    if (low.isLowSurrogate()) {
                        makeCharFromSurrogatePair(ch, low)
                    } else {
                        throw IllegalStateException("Expected low surrogate, got: ${low.code}")
                    }
                }
                ch.isLowSurrogate() -> throw IllegalStateException("Standalone low surrogate found: ${ch.code}")
                else -> ch.code
            }
            yield(CodePoint(v))
        }
    }
}
