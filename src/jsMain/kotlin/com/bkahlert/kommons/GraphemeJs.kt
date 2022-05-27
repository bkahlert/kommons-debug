package com.bkahlert.kommons

private val GraphemeSplitter = js("require('grapheme-splitter')")
private val graphemeSplitter = GraphemeSplitter()

/** Returns a sequence yielding the [Grapheme] instances this string consists of. */
public actual fun String.asGraphemeSequence(): Sequence<Grapheme> {
    val result = graphemeSplitter.splitGraphemes(this) as Array<String>
    return result.asSequence().map { Grapheme(it) }
}
