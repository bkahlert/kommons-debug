package com.bkahlert.kommons

import com.bkahlert.kommons.LineSeparators.Common
import com.bkahlert.kommons.Unicode as UnicodeConstants

/**
 * Collection of [Common] line separators.
 *
 * @see <a href="https://www.unicode.org/reports/tr18/#RL1.6">Unicode® Technical Standard #18—Line Boundaries</a>
 * @see <a href="https://www.unicode.org/reports/tr18/">Unicode® Technical Standard #18—UNICODE REGULAR EXPRESSIONS</a>
 */
public object LineSeparators : AbstractList<String>() {

    /**
     * Line separator as used on Windows systems.
     *
     * Representations: `\r\n`,  `␍␊`, `⏎`
     */
    public const val CRLF: String = UnicodeConstants.CARRIAGE_RETURN.toString() + UnicodeConstants.LINE_FEED.toString()

    /**
     * Line separator as used on Unix systems and modern Mac systems.
     *
     * Representations: `\n`, `␊`, `⏎`
     *
     */
    public const val LF: String = UnicodeConstants.LINE_FEED.toString()

    /**
     * Line separator as used on old Mac systems.
     *
     * Representations: `\r`, `␍`, `⏎`
     */
    public const val CR: String = UnicodeConstants.CARRIAGE_RETURN.toString()

    /**
     * Next line separator
     *
     * Representations: `␤`, `⏎`
     */
    public const val NEL: String = UnicodeConstants.NEXT_LINE.toString()

    /**
     * Paragraph separator
     *
     * Representations: `ₛᷮ`, `⏎`
     */
    public const val PS: String = UnicodeConstants.PARAGRAPH_SEPARATOR.toString()

    /**
     * Line separator
     *
     * Representations: `ₛᷞ`, `⏎`
     */
    public const val LS: String = UnicodeConstants.LINE_SEPARATOR.toString()

    /**
     * Same line separator as used by Kotlin.
     */
    public val Default: String = StringBuilder().appendLine().toString()

    /**
     * The common line separators [CRLF], [LF] and [CR].
     */
    public val Common: Array<String> = arrayOf(CRLF, LF, CR)

    override val size: Int get() = Common.size
    override fun get(index: Int): String = Common[index]

    /** All [Unicode® Technical Standard #18—Line Boundaries](https://www.unicode.org/reports/tr18/#RL1.6). */
    public val Unicode: Array<String> = arrayOf(CRLF, LF, CR, NEL, PS, LS)

    /**
     * All [Unicode® Technical Standard #18—Line Boundaries](https://www.unicode.org/reports/tr18/#RL1.6)
     * that are not [Common].
     */
    public val Uncommon: Array<String> = Unicode.subtract(Common.toSet()).toTypedArray()
}
