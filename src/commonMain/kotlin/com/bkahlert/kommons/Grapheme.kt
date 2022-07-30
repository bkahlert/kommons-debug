package com.bkahlert.kommons

import kotlin.jvm.JvmInline

/**
 * Representation of a [Unicode grapheme cluster](https://unicode.org/glossary/#grapheme_cluster)
 *
 * @see <a href="https://unicode.org/reports/tr29/">Unicode® Technical Standard #18—UNICODE TEXT SEGMENTATION</a>
 * @see <a href="https://unicode.org/reports/tr29/#Grapheme_Cluster_Boundary_Rules">Grapheme Cluster Boundary Rules</a>
 */
@JvmInline
public value class Grapheme private constructor(
    /** The string this grapheme consists of. */
    public val value: CharSequence,
) : CharSequence by value {
    public constructor(delegate: CharSequence, range: IntRange? = null) : this(DelegatingCharSequence(delegate, range))

    /** The [CodePoint] instances this grapheme consists of. */
    public val codePoints: List<CodePoint> get() = value.toCodePointList()
    override fun toString(): String = value.toString()
}

/** An [Iterator] that iterates [Grapheme] positions. */
public expect class GraphemePositionIterator(
    text: CharSequence,
) : PositionIterator

/** An [Iterator] that iterates [Grapheme] instances. */
public class GraphemeIterator(private val text: CharSequence) :
    ChunkIterator<Grapheme> by ChunkingIterator(GraphemePositionIterator(text), { Grapheme(text, it) })

/** Returns the [Grapheme] with the same value, or throws an [IllegalArgumentException] otherwise. */
public fun CharSequence.asGrapheme(): Grapheme = asGraphemeOrNull() ?: throw IllegalArgumentException("invalid grapheme: $this")

/** Returns the [Grapheme] with the same value, or `null` otherwise. */
public fun CharSequence.asGraphemeOrNull(): Grapheme? = asGraphemeSequence().singleOrNull()

/** Returns a sequence yielding the [Grapheme] instances contained in the specified text range of this string. */
public fun CharSequence.asGraphemeSequence(
    startIndex: Int = 0,
    endIndex: Int = length,
): Sequence<Grapheme> {
    checkBoundsIndexes(length, startIndex, endIndex)
    return GraphemeIterator(subSequence(startIndex, endIndex)).asSequence()
}

/** Returns the [Grapheme] instances contained in the specified text range of this string. */
public fun CharSequence.toGraphemeList(
    startIndex: Int = 0,
    endIndex: Int = length,
): List<Grapheme> = asGraphemeSequence(startIndex, endIndex).toList()

/** Returns the number of [Grapheme] instances contained in the specified text range of this string. */
public fun CharSequence.graphemeCount(
    startIndex: Int = 0,
    endIndex: Int = length,
): Int = asGraphemeSequence(startIndex, endIndex).count()
