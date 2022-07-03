package com.bkahlert.kommons

import kotlin.jvm.JvmInline

/**
 * Representation of a [Unicode grapheme cluster](https://unicode.org/glossary/#grapheme_cluster)
 *
 * @see <a href="https://unicode.org/reports/tr29/">Unicode® Technical Standard #18—UNICODE TEXT SEGMENTATION</a>
 * @see <a href="https://unicode.org/reports/tr29/#Grapheme_Cluster_Boundary_Rules">Grapheme Cluster Boundary Rules</a>
 */
@JvmInline
public value class Grapheme(public val string: String) {
    /** The [CodePoint] instances this grapheme consists of. */
    public val codePoints: List<CodePoint> get() = string.toCodePointList()
    override fun toString(): String = string
}

/** Returns a sequence yielding the [Grapheme] instances this string consists of. */
public expect fun String.asGraphemeSequence(): Sequence<Grapheme>

/** Returns a list containing the [Grapheme] instances this string consists of. */
public fun String.toGraphemeList(): List<Grapheme> = asGraphemeSequence().toList()

/** Returns the number of [Grapheme] instances in the specified text range of this string. */
public fun String.graphemeCount(startIndex: Int = 0, endIndex: Int = length): Int {
    val substring = substring(startIndex, endIndex)
    if (substring.isEmpty()) return 0
    return substring.asGraphemeSequence().count()
}
