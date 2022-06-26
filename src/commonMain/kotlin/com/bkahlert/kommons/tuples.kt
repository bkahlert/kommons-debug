package com.bkahlert.kommons

/**
 * Returns a pair containing the results of applying the given [transform] function
 * to each element in the original pair.
 */
public fun <T, A : T, B : T, R> Pair<A, B>.map(transform: (T) -> R): Pair<R, R> =
    Pair(transform(first), transform(second))

/**
 * Returns a triple containing the results of applying the given [transform] function
 * to each element in the original triple.
 */
public fun <T, A : T, B : T, C : T, R> Triple<A, B, C>.map(transform: (T) -> R): Triple<R, R, R> =
    Triple(transform(first), transform(second), transform(third))

/** Creates a tuple of type [Triple] from this [Pair] and [that]. */
public infix fun <A, B, C> Pair<A, B>.too(that: C): Triple<A, B, C> =
    Triple(first, second, that)
