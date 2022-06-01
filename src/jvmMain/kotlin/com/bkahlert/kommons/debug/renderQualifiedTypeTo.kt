package com.bkahlert.kommons.debug

import kotlin.reflect.KClass

/**
 * Renders the qualified type of this class to the specified [out].
 * @see renderQualifiedType
 */
internal actual fun KClass<*>.renderQualifiedTypeTo(out: StringBuilder) {
    out.append(qualifiedName?.takeUnless { objectRegex.containsMatchIn(it) } ?: "<object>")
}

private val objectRegex = "\\$\\d+$".toRegex()
