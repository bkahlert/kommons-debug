package com.bkahlert.kommons

import com.bkahlert.kommons.debug.renderQualifiedType
import io.kotest.assertions.assertSoftly
import kotlin.reflect.KClass

/** Asserts the specified [assertions] softly. */
inline fun <T> tests(assertions: () -> T) {
    assertSoftly(assertions)
}

/**
 * Returns the longest possible name available for this class.
 * That is, in order, the FQN, the simple name, or toString.
 */
fun KClass<*>.bestName(): String = renderQualifiedType()
