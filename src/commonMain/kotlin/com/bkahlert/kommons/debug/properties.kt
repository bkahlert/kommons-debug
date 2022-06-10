package com.bkahlert.kommons.debug

import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1

/**
 * Contains a [Map] of this object's properties with each [Map.Entry.key] representing
 * a property name and [Map.Entry.value] the corresponding value.
 */
public expect val Any.properties: Map<String, Any?>

/** Gets the value of this property or the result of [onFailure]. */
public expect fun <V> KProperty0<V>.getOrElse(onFailure: (Throwable) -> V): V

/** Gets the value of this property or the result of [onFailure]. */
public expect fun <T, V> KProperty1<T, V>.getOrElse(receiver: T, onFailure: (Throwable) -> V): V
