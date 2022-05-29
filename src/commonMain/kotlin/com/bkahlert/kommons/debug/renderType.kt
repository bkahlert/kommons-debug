package com.bkahlert.kommons.debug

import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty

public fun Any.renderType(simplified: Boolean = true): String = buildString { this@renderType.renderTypeTo(this, simplified) }

public fun Any.renderTypeTo(out: StringBuilder, simplified: Boolean = true) {
    if (simplified) when (this) {
        is UByteArray -> out.append("UByteArray")
        is UShortArray -> out.append("UShortArray")
        is UIntArray -> out.append("UIntArray")
        is ULongArray -> out.append("ULongArray")

        is Map<*, *> -> if (isPlain) out.append("Map") else out.append(this::class.simpleName.sanitize())
        is Set<*> -> if (isPlain) out.append("Set") else out.append(this::class.simpleName.sanitize())
        is List<*> -> if (isPlain) out.append("List") else out.append(this::class.simpleName.sanitize())
        is Collection<*> -> if (isPlain) out.append("Collection") else out.append(this::class.simpleName.sanitize())
        is Iterable<*> -> out.append("Iterable")

        is KClassifier -> out.append(this::class.simpleName.sanitize())
        is KProperty<*> -> out.append(this::class.simpleName.sanitize())
        is KFunction<*> -> renderFunctionTypeTo(out)
        is Function<*> -> renderFunctionTypeTo(out)
        else -> out.append(this::class.simpleName.sanitize())
    } else this::class.renderQualifiedTypeTo(out)
}

private val objectRegex = "\\$\\d+$".toRegex()
private fun String?.sanitize() =
    this?.takeUnless { objectRegex.containsMatchIn(it) }?.removeSuffix("Impl") ?: "<object>"

internal fun KClass<*>.renderQualifiedType(): String = buildString { this@renderQualifiedType.renderQualifiedTypeTo(this) }

internal fun Function<*>.renderFunctionType(simplified: Boolean = true): String = buildString { this@renderFunctionType.renderFunctionTypeTo(this, simplified) }

internal expect fun Function<*>.renderFunctionTypeTo(out: StringBuilder, simplified: Boolean = true)
