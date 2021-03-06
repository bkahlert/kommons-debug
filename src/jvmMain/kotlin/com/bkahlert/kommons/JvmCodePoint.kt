package com.bkahlert.kommons

import com.ibm.icu.lang.UCharacter
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

/** The name of this code point. */
public val CodePoint.name: String
    get() = UCharacter.getName(index) ?: "0x${Integer.toHexString(index).uppercase()}"

/** Returns a sequence yielding the [CodePoint] instances this string consists of. */
public actual fun String.asCodePointSequence(): Sequence<CodePoint> {
    if (isEmpty()) return emptySequence()
    var pos = 0
    return sequence {
        while (pos < this@asCodePointSequence.length) {
            yield(CodePoint(this@asCodePointSequence.codePointAt(pos)))
            pos = this@asCodePointSequence.offsetByCodePoints(pos, 1)
        }
    }
}

/** Returns the number of Unicode code points in the specified text range of this string. */
public actual fun String.codePointCount(startIndex: Int, endIndex: Int): Int {
    if (endIndex < startIndex || startIndex < 0 || endIndex > length) throw IndexOutOfBoundsException("begin $startIndex, end $endIndex, length $length")
    return kotlinCodePointCount(startIndex, endIndex)
}
