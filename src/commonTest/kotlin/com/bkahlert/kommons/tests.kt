package com.bkahlert.kommons

import io.kotest.assertions.assertSoftly

/** Asserts the specified [assertions] softly. */
inline fun <T> tests(assertions: () -> T) {
    assertSoftly(assertions)
}
