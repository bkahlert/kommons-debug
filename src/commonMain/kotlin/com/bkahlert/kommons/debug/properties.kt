package com.bkahlert.kommons.debug

/**
 * Contains a [Map] of this object's properties with each [Map.Entry.key] representing
 * a property name and [Map.Entry.value] the corresponding value.
 */
public expect val Any.properties: Map<String, Any?>
