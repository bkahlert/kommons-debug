package com.bkahlert.kommons.debug

import com.bkahlert.kommons.debug.Compression.Always
import com.bkahlert.kommons.debug.Compression.Auto
import com.bkahlert.kommons.debug.Compression.Never
import com.bkahlert.kommons.debug.CustomToString.Ignore
import com.bkahlert.kommons.debug.CustomToString.IgnoreForPlainCollectionsAndMaps
import com.bkahlert.kommons.debug.Typing.FullyTyped
import com.bkahlert.kommons.debug.Typing.SimplyTyped
import com.bkahlert.kommons.debug.Typing.Untyped
import com.bkahlert.kommons.isMultiline

/** Serialization option that specifies if and how type information should be included. */
public sealed class Typing {
    /** Omit type information */
    public object Untyped : Typing()

    /** Include simplified type information */
    public object SimplyTyped : Typing()

    /** Include all available type information */
    public object FullyTyped : Typing()
}

/** Serialization option that specifies if and how the output is compressed. */
public sealed class Compression {
    /** Always compress output */
    public object Always : Compression()

    /** Never compress output */
    public object Never : Compression()

    /** Compress output if the output does not exceed the specified [maxLength] (default: 60). */
    public class Auto(
        /** The maximum length compressed output is allowed to have. */
        public val maxLength: Int = 60
    ) : Compression()
}

/** Serialization option that specifies how to handle overridden [Any.toString] implementations. */
public sealed class CustomToString {
    /** Uses an overridden [Any.toString] for all typed but plain collections and maps. */
    public object IgnoreForPlainCollectionsAndMaps : CustomToString()

    /** Ignores an eventually existing [Any.toString] implementation. */
    public object Ignore : CustomToString()
}

/**
 * Renders this object depending on whether its `toString()` is overridden:
 * - If there is a custom `toString()` it is simply used.
 * - If there is *no custom* `toString()` the object is serialized structurally.
 *   The serialization can include [typing] information (default: [Typing.Untyped]),
 *   apply [compression] (default: [Compression.Auto]).
 */
public fun Any?.render(
    typing: Typing = Untyped,
    compression: Compression = Auto(),
    customToString: CustomToString = IgnoreForPlainCollectionsAndMaps,
    rendered: MutableSet<Any> = mutableSetOf(),
): String = buildString { this@render.renderTo(this, typing, compression, customToString, rendered) }

/**
 * Renders this object depending on whether its `toString()` is overridden:
 * - If there is a custom `toString()` it is simply used.
 * - If there is *no custom* `toString()` the object is serialized structurally.
 *   The serialization can be [typing] (default: [Typing.Untyped]) and [compression] (default: [Compression.Auto]).
 */
public fun Any?.renderTo(
    out: StringBuilder,
    typing: Typing = Untyped,
    compression: Compression = Auto(),
    customToString: CustomToString = IgnoreForPlainCollectionsAndMaps,
    rendered: MutableSet<Any>,
) {
    if (this == null) {
        out.append("null")
    } else {
        when (typing) {
            Untyped -> {}
            SimplyTyped -> {
                out.append("!")
                renderTypeTo(out, simplified = true)
                out.append(" ")
            }
            FullyTyped -> {
                out.append("!")
                renderTypeTo(out, simplified = false)
                out.append(" ")
            }
        }
        when (this) {
            is CharSequence -> renderStringTo(out, typing, compression, this)

            is Boolean, is Char, is Float, is Double,
            is UByte, is UShort, is UInt, is ULong,
            is Byte, is Short, is Int, is Long -> renderPrimitiveTo(out, this)

            is BooleanArray, is CharArray, is FloatArray, is DoubleArray,
            is UByteArray, is UShortArray, is UIntArray, is ULongArray,
            is ByteArray, is ShortArray, is IntArray, is LongArray -> renderPrimitiveArrayTo(out, this)

            is Array<*> -> renderArrayTo(out, this, typing, compression, customToString, rendered)

            else -> {
                if (rendered.contains(this)) {
                    out.append("<")
                    renderTypeTo(out)
                    out.append("@")
                    out.append(hashCode())
                    out.append(">")
                } else {
                    rendered.add(this)

                    when {
                        this is Collection<*> && this.isPlain -> renderCollectionTo(out, this, typing, compression, customToString, rendered)
                        this is Map<*, *> && this.isPlain -> renderObjectTo(out, this, typing, compression, customToString, rendered)
                        else -> {
                            when (customToString) {
                                IgnoreForPlainCollectionsAndMaps -> toCustomStringOrNull()
                                Ignore -> null
                            }
                                ?.also { out.append(it) }
                                ?: kotlin.runCatching { renderObjectTo(out, this, typing, compression, customToString, rendered) }
                                    .recoverCatching { out.append("<$this>") }
                                    .recoverCatching { out.append("<unsupported:$it>") }
                        }
                    }
                }
            }
        }
    }
}

internal fun renderString(
    string: CharSequence,
    typing: Typing = Untyped,
    compression: Compression = Auto(),
): String = buildString { renderStringTo(this, typing, compression, string) }

internal fun renderStringTo(
    out: StringBuilder,
    typing: Typing,
    compression: Compression,
    string: CharSequence,
) {
    when (compression) {
        Always -> {
            out.append(string.quoted)
        }
        Never -> {
            if (typing != Untyped) out.append("\n")
            out.append(string.toString())
        }
        is Auto -> {
            if (string.isMultiline) {
                if (typing != Untyped) out.append("\n")
                out.append(string.toString())
            } else {
                out.append(string.quoted)
            }
        }
    }
}

internal fun renderPrimitive(
    primitive: Any,
): String = buildString { renderPrimitiveTo(this, primitive) }

internal fun renderPrimitiveTo(
    out: StringBuilder,
    primitive: Any,
) {
    when (primitive) {
        is Boolean -> out.append(primitive)
        is Char -> out.append(primitive)
        is Float -> if (primitive.toLong() in Byte.VALUE_RANGE && primitive.mod(1.0f) == 0.0f)
            out.append("0x" + primitive.toInt().toByte().toHexadecimalString()) else out.append(primitive)
        is Double -> if (primitive.toLong() in Byte.VALUE_RANGE && primitive.mod(1.0) == 0.0)
            out.append("0x" + primitive.toInt().toByte().toHexadecimalString()) else out.append(primitive)

        is UByte -> out.append("0x" + primitive.toHexadecimalString())
        is UShort -> if (primitive in UByte.VALUE_RANGE) out.append("0x" + primitive.toUByte().toHexadecimalString()) else out.append(primitive)
        is UInt -> if (primitive in UByte.VALUE_RANGE) out.append("0x" + primitive.toUByte().toHexadecimalString()) else out.append(primitive)
        is ULong -> if (primitive in UByte.VALUE_RANGE) out.append("0x" + primitive.toUByte().toHexadecimalString()) else out.append(primitive)

        is Byte -> out.append("0x" + primitive.toHexadecimalString())
        is Short -> if (primitive in Byte.VALUE_RANGE) out.append("0x" + primitive.toByte().toHexadecimalString()) else out.append(primitive)
        is Int -> if (primitive in Byte.VALUE_RANGE) out.append("0x" + primitive.toByte().toHexadecimalString()) else out.append(primitive)
        is Long -> if (primitive in Byte.VALUE_RANGE) out.append("0x" + primitive.toByte().toHexadecimalString()) else out.append(primitive)
        else -> out.append("⁉️")
    }
}

internal fun renderPrimitiveArray(
    primitiveArray: Any,
): String = buildString { renderPrimitiveArrayTo(this, primitiveArray) }

internal fun renderPrimitiveArrayTo(
    out: StringBuilder,
    primitiveArray: Any,
) {
    when (primitiveArray) {
        is BooleanArray -> renderPrimitivesTo(out, primitiveArray.iterator())
        is CharArray -> renderPrimitivesTo(out, primitiveArray.iterator())
        is FloatArray -> renderPrimitivesTo(out, primitiveArray.iterator())
        is DoubleArray -> renderPrimitivesTo(out, primitiveArray.iterator())

        is UByteArray -> out.append("0x" + primitiveArray.toHexadecimalString())
        is UShortArray -> renderPrimitivesTo(out, primitiveArray.iterator())
        is UIntArray -> renderPrimitivesTo(out, primitiveArray.iterator())
        is ULongArray -> renderPrimitivesTo(out, primitiveArray.iterator())

        is ByteArray -> out.append("0x" + primitiveArray.toHexadecimalString())
        is ShortArray -> renderPrimitivesTo(out, primitiveArray.iterator())
        is IntArray -> renderPrimitivesTo(out, primitiveArray.iterator())
        is LongArray -> renderPrimitivesTo(out, primitiveArray.iterator())
        else -> out.append("⁉️")
    }
}

private fun renderPrimitivesTo(
    out: StringBuilder,
    primitives: Iterator<*>,
) {
    out.append('[')
    primitives.withIndex().forEach { (index, value) ->
        if (index != 0) out.append(", ")
        value.renderTo(out, Untyped, Auto(), Ignore, mutableSetOf())
    }
    out.append(']')
}

internal fun renderArray(
    array: Array<*>,
    typing: Typing = Untyped,
    compression: Compression = Auto(),
    customToString: CustomToString = IgnoreForPlainCollectionsAndMaps,
    rendered: MutableSet<Any> = mutableSetOf(),
): String = buildString { renderArrayTo(this, array, typing, compression, customToString, rendered) }

internal fun renderArrayTo(
    out: StringBuilder,
    array: Array<*>,
    typing: Typing,
    compression: Compression,
    customToString: CustomToString,
    rendered: MutableSet<Any>,
) = renderObjectsTo(out, array.asList(), typing, compression, customToString, rendered)


internal fun renderCollection(
    collection: Collection<*>,
    typing: Typing = Untyped,
    compression: Compression = Auto(),
    customToString: CustomToString = IgnoreForPlainCollectionsAndMaps,
    rendered: MutableSet<Any> = mutableSetOf(),
): String = buildString { renderCollectionTo(this, collection, typing, compression, customToString, rendered) }

internal fun renderCollectionTo(
    out: StringBuilder,
    collection: Collection<*>,
    typing: Typing,
    compression: Compression,
    customToString: CustomToString,
    rendered: MutableSet<Any>
) = renderObjectsTo(out, collection.toList(), typing, compression, customToString, rendered)


private fun renderObjectsTo(
    out: StringBuilder,
    objects: List<*>,
    typing: Typing,
    compression: Compression,
    customToString: CustomToString,
    rendered: MutableSet<Any>
) {
    when (compression) {
        Always -> renderCompressedObjectsTo(out, objects, typing, customToString, rendered)
        Never -> renderNonCompressedObjectsTo(out, objects, typing, customToString, rendered)
        is Auto -> {
            buildString { renderCompressedObjectsTo(this, objects, typing, customToString, rendered.toMutableSet()) }
                .takeUnless { it.length > compression.maxLength }
                ?.also { out.append(it); rendered.addAll(objects.filterNotNull()) }
                ?: renderNonCompressedObjectsTo(out, objects, typing, customToString, rendered)
        }
    }
}

private fun renderCompressedObjectsTo(
    out: StringBuilder,
    objects: List<*>,
    typing: Typing,
    customToString: CustomToString,
    rendered: MutableSet<Any>
) {
    out.append("[")
    if (objects.isNotEmpty()) out.append(" ")
    objects.forEachIndexed { index, value ->
        if (index > 0) out.append(", ")
        value.renderTo(out, typing, Always, customToString, rendered)
    }
    if (objects.isNotEmpty()) out.append(" ")
    out.append("]")
}

private fun renderNonCompressedObjectsTo(
    out: StringBuilder,
    objects: List<*>,
    typing: Typing,
    customToString: CustomToString,
    rendered: MutableSet<Any>
) {
    val indent = "    "
    out.append("[")
    if (objects.isNotEmpty()) out.append("\n")
    objects.forEachIndexed { index, value ->
        if (index > 0) out.append(",\n")
        out.append(indent)
        val renderedElement = value.render(typing, Never, customToString, rendered).prependIndent(indent)
        out.append(renderedElement, indent.length, renderedElement.length)
    }
    if (objects.isNotEmpty()) out.append("\n")
    out.append("]")
}

internal fun renderObject(
    obj: Any,
    typing: Typing = Untyped,
    compression: Compression = Auto(),
    customToString: CustomToString = IgnoreForPlainCollectionsAndMaps,
    rendered: MutableSet<Any> = mutableSetOf(),
): String = buildString { renderObjectTo(this, obj, typing, compression, customToString, rendered) }

internal fun renderObjectTo(
    out: StringBuilder,
    obj: Any,
    typing: Typing,
    compression: Compression,
    customToString: CustomToString,
    rendered: MutableSet<Any>,
) {
    when (compression) {
        Always -> renderCompressedObjectTo(out, obj, typing, customToString, rendered)
        Never -> renderNonCompressedObjectTo(out, obj, typing, customToString, rendered)
        is Auto -> {
            buildString { renderCompressedObjectTo(this, obj, typing, customToString, rendered.toMutableSet()) }
                .takeUnless { it.length > compression.maxLength }
                ?.also { out.append(it); rendered.add(obj) }
                ?: renderNonCompressedObjectTo(out, obj, typing, customToString, rendered)
        }
    }
}

private fun renderCompressedObjectTo(
    out: StringBuilder,
    obj: Any,
    typing: Typing,
    customToString: CustomToString,
    rendered: MutableSet<Any>,
) {
    out.append("{")
    val entries = if (obj is Map<*, *>) obj.entries else obj.properties.entries
    if (entries.isNotEmpty()) out.append(" ")
    entries.forEachIndexed { index, (key, value) ->
        if (index > 0) out.append(", ")
        if (key is CharSequence) out.append(key.quoted.removeSurrounding("\""))
        else key.renderTo(out, typing, Always, customToString, rendered)

        out.append(": ")

        value.renderTo(out, typing, Always, customToString, rendered)
    }
    if (entries.isNotEmpty()) out.append(" ")
    out.append("}")
}

private fun renderNonCompressedObjectTo(
    out: StringBuilder,
    obj: Any,
    typing: Typing,
    customToString: CustomToString,
    rendered: MutableSet<Any>,
) {
    val keyIndent = "    "
    out.append("{")
    val entries = if (obj is Map<*, *>) obj.entries else obj.properties.entries
    if (entries.isNotEmpty()) out.append("\n")
    entries.forEachIndexed { index, (key, value) ->
        if (index > 0) out.append(",\n")
        out.append(keyIndent)

        val renderedKey = if (key is CharSequence) key.quoted.removeSurrounding("\"") else key.render(typing, Always, customToString, rendered)
        out.append(renderedKey)

        out.append(": ")

        val valueIndent = " ".repeat(keyIndent.length + renderedKey.length + 2)
        val renderedValue = value.render(typing, Never, customToString, rendered).prependIndent(valueIndent)
        out.append(renderedValue, valueIndent.length, renderedValue.length)
    }
    if (entries.isNotEmpty()) out.append("\n")
    out.append("}")
}
