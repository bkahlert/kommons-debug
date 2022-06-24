package com.bkahlert.kommons

import kotlin.random.Random

/**
 * Returns a random element from this range,
 * or throws a [NoSuchElementException] if this range is empty.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun ClosedRange<Double>.random(): Double = random(Random)

/**
 * Returns a random element from this range using the specified source of randomness,
 * or throws a [NoSuchElementException] if this range is empty.
 */
public fun ClosedRange<Double>.random(random: Random): Double =
    if (isEmpty()) throw NoSuchElementException("Cannot get random in empty range: $this")
    else random.nextDouble(start, endInclusive)

/**
 * Returns a random element from this range,
 * or `null` if this range is empty.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun ClosedRange<Double>.randomOrNull(): Double? = randomOrNull(Random)

/**
 * Returns a random element from this range using the specified source of randomness,
 * or `null` if this range is empty.
 */
public fun ClosedRange<Double>.randomOrNull(random: Random): Double? =
    if (isEmpty()) null else random.nextDouble(start, endInclusive)

/**
 * Creates an [Iterable] instance that wraps the original range returning
 * its elements when being iterated.
 *
 * Unless empty, the first element returned is [ClosedRange.start].
 * The remaining elements are computed by applying the specified [step] to
 * the most recently returned element.
 *
 * The iteration ends when the next element is no longer contained in this range.
 */
public fun <T : Comparable<T>> ClosedRange<T>.asIterable(step: (T) -> T): Iterable<T> =
    Iterable {
        var next: T = start
        iterator {
            while (contains(next)) {
                yield(next)
                next = step(next)
            }
        }
    }
