package com.bkahlert.kommons.debug

import kotlin.reflect.KClass

internal actual fun KClass<*>.renderQualifiedTypeTo(out: StringBuilder) {
    out.append(qualifiedName?.takeUnless { objectRegex.containsMatchIn(it) } ?: "<object>")
}

private val objectRegex = "\\$\\d+$".toRegex()
