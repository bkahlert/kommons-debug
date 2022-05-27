package com.bkahlert.kommons.debug

import kotlin.reflect.KClassifier
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty

public fun Any.renderType(simplified: Boolean = true): String = buildString { this@renderType.renderTypeTo(this, simplified) }

public fun Any.renderTypeTo(out: StringBuilder, simplified: Boolean = true) {
    if (simplified) {
        when (this) {
            is UByteArray -> out.append("UByteArray")
            is UShortArray -> out.append("UShortArray")
            is UIntArray -> out.append("UIntArray")
            is ULongArray -> out.append("ULongArray")

            is Map<*, *> -> if (isPlain) out.append("Map") else out.append(this::class.simpleName ?: "object")
            is Set<*> -> if (isPlain) out.append("Set") else out.append(this::class.simpleName ?: "object")
            is List<*> -> if (isPlain) out.append("List") else out.append(this::class.simpleName ?: "object")
            is Collection<*> -> if (isPlain) out.append("Collection") else out.append(this::class.simpleName ?: "object")
            is Iterable<*> -> out.append("Iterable")

            is KClassifier -> out.append(this::class.simpleName?.removeSuffix("Impl") ?: "⁉️")
            is KProperty<*> -> out.append(this::class.simpleName?.removeSuffix("Impl") ?: "⁉️")
            is KFunction<*> -> renderFunctionTypeTo(out)
            is Function<*> -> renderFunctionTypeTo(out)
            else -> out.append(this::class.simpleName ?: "object")
        }
    } else {
        out.append(this::class.toString().removePrefix("class ").takeUnless { it == "null" } ?: "object")
    }
}

public fun Function<*>.renderFunctionType(simplified: Boolean = true): String = buildString { this@renderFunctionType.renderFunctionTypeTo(this, simplified) }

public expect fun Function<*>.renderFunctionTypeTo(out: StringBuilder, simplified: Boolean = true)
