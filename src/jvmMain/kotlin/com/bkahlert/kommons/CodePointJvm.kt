package com.bkahlert.kommons

import kotlin.text.codePointCount as kotlinCodePointCount

/**
 * Contains the character pointed to and represented by a [String].
 */
public actual val CodePoint.string: String
    get() = buildString { appendCodePoint(index) }

/** Whether this code point is a letter. */
public actual val CodePoint.isLetter: Boolean
    get() = Character.isLetter(index)

/** Whether this code point is a digit. */
public actual val CodePoint.isDigit: Boolean
    get() = Character.isDigit(index)

/** Whether this code point is a [Unicode Space Character](http://www.unicode.org/versions/Unicode13.0.0/ch06.pdf). */
public actual val CodePoint.isWhitespace: Boolean
    get() = Character.isWhitespace(index)

/** Returns a sequence yielding the [CodePoint] instances this string consists of. */
public actual fun String.asCodePointSequence(): Sequence<CodePoint> {
    var pos = 0
    return sequence<CodePoint> {
        while (pos < this@asCodePointSequence.length) {
            yield(CodePoint(this@asCodePointSequence.codePointAt(pos)))
            pos = this@asCodePointSequence.offsetByCodePoints(pos, 1)
        }
    }
}

/** Returns the number of Unicode code points in the specified text range of this string. */
public actual fun String.codePointCount(beginIndex: Int, endIndex: Int): Int = kotlinCodePointCount(beginIndex, endIndex)
