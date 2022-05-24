package com.bkahlert.kommons.debug

import kotlin.reflect.KCallable
import kotlin.reflect.KClassifier

public fun Any.renderType(): String = buildString { this@renderType.renderTypeTo(this) }

public fun Any.renderTypeTo(out: StringBuilder) {
    when (this) {
        is UByteArray -> out.append("UByteArray")
        is UShortArray -> out.append("UShortArray")
        is UIntArray -> out.append("UIntArray")
        is ULongArray -> out.append("ULongArray")

        is MutableMap<*, *> -> out.append("MutableMap")
        is Map<*, *> -> out.append("Map")
        is MutableSet<*> -> out.append("MutableSet")
        is Set<*> -> out.append("Set")
        is MutableList<*> -> out.append("MutableList")
        is List<*> -> out.append("List")
        is MutableCollection<*> -> out.append("MutableCollection")
        is Collection<*> -> out.append("Collection")
        is MutableIterable<*> -> out.append("MutableIterable")
        is Iterable<*> -> out.append("Iterable")

        is KCallable<*>, is KClassifier -> out.append(this::class.simpleName?.removeSuffix("Impl") ?: "⁉️")
        is Function<*> -> out.append("lambda")
        else -> out.append(this::class.simpleName ?: "object")
    }
}
