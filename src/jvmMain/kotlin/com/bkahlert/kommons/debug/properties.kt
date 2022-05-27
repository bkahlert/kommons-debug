package com.bkahlert.kommons.debug

import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter

/**
 * Contains a [Map] of this object's properties with each [Map.Entry.key] representing
 * a property name and [Map.Entry.value] the corresponding value.
 */
public actual val Any.properties: Map<String, Any?>
    get() = this::class.members
        .filterIsInstance<KProperty1<Any, *>>()
        .reversed()
        .mapNotNull { prop ->
            prop.getSafely(this@properties).fold({ value -> prop.name to value }, { null })
        }.toMap()

internal fun <T, V> KProperty1<T, V>.getSafely(receiver: T): Result<V> {
    return kotlin.runCatching { get(receiver) }.recover {
        val getter = this.javaGetter
        if (getter != null) {
            if (!getter.accessible) getter.accessible = true
            @Suppress("UNCHECKED_CAST")
            return kotlin.runCatching { getter.invoke(receiver) as V }
        }
        val field = this.javaField
        if (field != null) {
            if (!field.accessible) field.accessible = true
            @Suppress("UNCHECKED_CAST")
            return kotlin.runCatching { field.get(receiver) as V }
        }
        return Result.failure(it)
    }
}
