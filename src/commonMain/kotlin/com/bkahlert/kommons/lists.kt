package com.bkahlert.kommons

/**
 * Returns a new list wrapping this list with the following differences:
 * 1) Negative indices are supported and start from the end of this list
 *    (e.g. `this[-1]` returns the last element, `this[-2]` returns the second, and so on).
 * 2) Modulus operation is applied, that is,
 *    `listOf("a","b","c").withNegativeIndices(4)` returns `a`. `this[-4]` would return `c`.
 */
public fun <T> List<T>.withNegativeIndices(): List<T> =
    object : List<T> by this {
        override fun get(index: Int): T = this@withNegativeIndices[index.mod(size)]
    }
