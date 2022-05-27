package com.bkahlert.kommons

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


public actual fun String.asCodePointSequence(): Sequence<CodePoint> {
    var pos = 0
    return sequence {
        while (pos < this@asCodePointSequence.length) {
            yield(CodePoint(this@asCodePointSequence.codePointAt(pos)))
            pos = this@asCodePointSequence.offsetByCodePoints(pos, 1)
        }
    }
}
