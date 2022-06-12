package com.bkahlert.kommons.debug

import com.bkahlert.kommons.VALUE_RANGE
import com.bkahlert.kommons.debug.Compression.Always
import com.bkahlert.kommons.debug.Compression.Auto
import com.bkahlert.kommons.debug.Compression.Never
import com.bkahlert.kommons.debug.CustomToString.Ignore
import com.bkahlert.kommons.debug.CustomToString.IgnoreForPlainCollectionsAndMaps
import com.bkahlert.kommons.debug.RenderingSettings.Companion
import com.bkahlert.kommons.debug.Typing.FullyTyped
import com.bkahlert.kommons.debug.Typing.SimplyTyped
import com.bkahlert.kommons.debug.Typing.Untyped
import com.bkahlert.kommons.isMultiline
import com.bkahlert.kommons.quoted
import com.bkahlert.kommons.toHexadecimalString

/** Serialization option that specifies if and how type information should be included. */
public sealed class Typing {
    /** Omit type information */
    public object Untyped : Typing()

    /** Include simplified type information */
    public object SimplyTyped : Typing()

    /** Include all available type information */
    public object FullyTyped : Typing()
}

/** Serialization option that specifies if and how to compress the output. */
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

/** Renders this object using the optionally specified [settings]. */
public fun Any?.render(settings: RenderingSettings = RenderingSettings.Default): String =
    buildString { RenderingContext(settings).renderTo2(this, this@render) }

/** Renders this object using the [RenderingSettings] built with the specified [template] and [init]. */
public fun Any?.render(template: RenderingSettings = RenderingSettings.Default, init: RenderingSettingsBuilder.() -> Unit): String =
    render(Companion.build(template, init))

/**
 * Renders this object to the specified [out].
 * @see render
 */
public fun Any?.renderTo(
    out: StringBuilder,
    typing: Typing = Untyped,
    compression: Compression = Auto(),
    customToString: CustomToString = IgnoreForPlainCollectionsAndMaps,
    rendered: MutableSet<Any> = mutableSetOf(),
    filterProperties: PropertyFilter = { _, _ -> true },
): Unit {
    RenderingContext(typing, compression, customToString, rendered, filterProperties).apply { renderTo2(out, this@renderTo) }
}

/** Settings used to specify how rendering an object takes place. */
public interface RenderingSettings {
    /** If and how type information should be included. */
    public val typing: Typing

    /** If and how to compress the output. */
    public val compression: Compression

    /** How to handle overridden [Any.toString] implementations. */
    public val customToString: CustomToString

    /** Which object-property pairs to render. */
    public val filterProperties: PropertyFilter

    public companion object {
        /** Default [RenderingSettings] */
        public val Default: RenderingSettings = object : RenderingSettings {
            override var typing: Typing = Untyped
            override var compression: Compression = Auto()
            override var customToString: CustomToString = IgnoreForPlainCollectionsAndMaps
            override var filterProperties: PropertyFilter = { _: Any?, _: String? -> true }
        }

        /** Builds a [RenderingSettings] using the specified [init] and optional [template]. */
        public fun build(template: RenderingSettings = Default, init: RenderingSettingsBuilder.() -> Unit = {}): RenderingSettings =
            (object : RenderingSettingsBuilder {
                val settings: MutableMap<String, Any?> = mutableMapOf()
                override var typing: Typing by settings.withDefault { template.typing }
                override var compression: Compression by settings.withDefault { template.compression }
                override var customToString: CustomToString by settings.withDefault { template.customToString }
                override var filterProperties: PropertyFilter by settings.withDefault { template.filterProperties }
                override fun filterProperties(predicate: PropertyFilter) {
                    filterProperties = predicate
                }
            }).apply(init)
    }
}

/** Filter that specifies which object-property pairs to render. */
public typealias PropertyFilter = (receiver: Any?, property: String?) -> Boolean

/** Builder for [RenderingSettings] */
public interface RenderingSettingsBuilder : RenderingSettings {
    public override var typing: Typing
    public override var compression: Compression
    public override var customToString: CustomToString
    public override var filterProperties: PropertyFilter

    /** @see [RenderingSettings.filterProperties] */
    public fun filterProperties(predicate: PropertyFilter)
}

internal data class RenderingContext(
    val settings: RenderingSettings,
    val rendered: MutableSet<Any> = mutableSetOf(),
) : RenderingSettings by settings {
    internal constructor(
        typing: Typing = Untyped,
        compression: Compression = Auto(),
        customToString: CustomToString = IgnoreForPlainCollectionsAndMaps,
        rendered: MutableSet<Any> = mutableSetOf(),
        filterProperties: PropertyFilter = { _, _ -> true },
    ) : this(RenderingSettings.build {
        this.typing = typing
        this.compression = compression
        this.customToString = customToString
        this.filterProperties = filterProperties
    }, rendered)

    val CharSequence.needsCompression: Boolean
        get() = when (val compression = settings.compression) {
            is Auto -> length > compression.maxLength
            else -> false
        }

    fun render(
        init: RenderingSettingsBuilder.() -> Unit = {},
        isolated: Boolean = false,
        block: RenderingContext.(StringBuilder) -> Unit
    ): String = buildString {
        copy(
            settings = RenderingSettings.build(settings, init),
            rendered = if (isolated) rendered.toMutableSet() else rendered,
        ).block(this)
    }
}


internal fun RenderingContext.renderTo2(out: StringBuilder, obj: Any?) {
    if (!filterProperties(obj, null)) return
    if (obj == null) {
        out.append("null")
    } else {
        when (typing) {
            Untyped -> {}
            SimplyTyped -> {
                out.append("!")
                obj.renderTypeTo(out, simplified = true)
                out.append(" ")
            }
            FullyTyped -> {
                out.append("!")
                obj.renderTypeTo(out, simplified = false)
                out.append(" ")
            }
        }
        when (obj) {
            is CharSequence -> renderStringTo(out, obj)

            is Boolean, is Char, is Float, is Double,
            is UByte, is UShort, is UInt, is ULong,
            is Byte, is Short, is Int, is Long -> renderPrimitiveTo(out, obj)

            is BooleanArray, is CharArray, is FloatArray, is DoubleArray,
            is UByteArray, is UShortArray, is UIntArray, is ULongArray,
            is ByteArray, is ShortArray, is IntArray, is LongArray -> renderPrimitiveArrayTo(out, obj)

            is Array<*> -> renderArrayTo(out, obj)

            else -> {
                if (rendered.contains(obj)) {
                    out.append("<")
                    obj.renderTypeTo(out)
                    out.append("@")
                    out.append(obj.hashCode())
                    out.append(">")
                } else {
                    rendered.add(obj)

                    when {
                        obj is Collection<*> && obj.isPlain -> renderCollectionTo(out, obj)
                        obj is Map<*, *> && obj.isPlain -> renderObjectTo(out, obj)
                        else -> {
                            val likelyRenderInvokingToString =
                                StackTrace.get().findByLastKnownCallsOrNull("render", "renderTo")?.function == "toString"

                            when (customToString) {
                                IgnoreForPlainCollectionsAndMaps -> if (likelyRenderInvokingToString) null else obj.toCustomStringOrNull()
                                Ignore -> null
                            }
                                ?.also { out.append(it) }
                                ?: kotlin.runCatching { renderObjectTo(out, obj) }
                                    .recoverCatching { out.append("<$obj>") }
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
): String = buildString { RenderingContext(typing = typing, compression = compression).renderStringTo(this, string) }

internal fun RenderingContext.renderStringTo(out: StringBuilder, string: CharSequence) {
    when (compression) {
        Always -> {
            out.append(string.quoted)
        }
        Never, is Auto -> {
            if (string.isMultiline) {
                out.append("\"\"\"\n")
                out.append(string.toString())
                out.append("\n\"\"\"")
            } else {
                out.append(string.quoted)
            }
        }
    }
}

internal fun renderPrimitive(primitive: Any): String =
    buildString { RenderingContext().renderPrimitiveTo(this, primitive) }

internal fun RenderingContext.renderPrimitiveTo(out: StringBuilder, primitive: Any) {
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

internal fun renderPrimitiveArray(primitiveArray: Any): String =
    buildString { RenderingContext().renderPrimitiveArrayTo(this, primitiveArray) }

internal fun RenderingContext.renderPrimitiveArrayTo(out: StringBuilder, primitiveArray: Any) {
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

private fun RenderingContext.renderPrimitivesTo(out: StringBuilder, primitives: Iterator<*>) {
    out.append('[')
    primitives.withIndex().forEach { (index, value) ->
        if (index != 0) out.append(", ")
        value.renderTo(out, Untyped, Auto(), Ignore, mutableSetOf()) { _, _ -> true }
    }
    out.append(']')
}

internal fun renderArray(
    array: Array<*>,
    typing: Typing = Untyped,
    compression: Compression = Auto(),
    customToString: CustomToString = IgnoreForPlainCollectionsAndMaps,
    rendered: MutableSet<Any> = mutableSetOf(),
    filterProperties: PropertyFilter = { _, _ -> true },
): String = buildString {
    RenderingContext(
        typing = typing,
        compression = compression,
        customToString = customToString,
        rendered = rendered,
        filterProperties = filterProperties
    ).renderArrayTo(this, array)
}

internal fun RenderingContext.renderArrayTo(out: StringBuilder, array: Array<*>) =
    renderObjectsTo(out, array.asList())


internal fun renderCollection(
    collection: Collection<*>,
    typing: Typing = Untyped,
    compression: Compression = Auto(),
    customToString: CustomToString = IgnoreForPlainCollectionsAndMaps,
    rendered: MutableSet<Any> = mutableSetOf(),
    filterProperties: PropertyFilter = { _, _ -> true },
): String = buildString {
    RenderingContext(
        typing = typing,
        compression = compression,
        customToString = customToString,
        rendered = rendered,
        filterProperties = filterProperties
    ).renderCollectionTo(this, collection)
}

internal fun RenderingContext.renderCollectionTo(out: StringBuilder, collection: Collection<*>) =
    renderObjectsTo(out, collection.toList())


private fun RenderingContext.renderObjectsTo(out: StringBuilder, objects: List<*>) {
    when (compression) {
        Always -> renderCompressedObjectsTo(out, objects)
        Never -> renderNonCompressedObjectsTo(out, objects)
        is Auto -> {
            render(isolated = true) { renderCompressedObjectsTo(it, objects) }
                .takeUnless { it.needsCompression }
                ?.also { out.append(it); rendered.addAll(objects.filterNotNull()) }
                ?: renderNonCompressedObjectsTo(out, objects)
        }
    }
}

private fun RenderingContext.renderCompressedObjectsTo(out: StringBuilder, objects: List<*>) {
    out.append("[")
    val filteredObjects = objects
        .filterIndexed { index, _ -> filterProperties(objects, "$index") }
        .filter { filterProperties(it, null) }
    if (filteredObjects.isNotEmpty()) out.append(" ")
    filteredObjects.forEachIndexed { index, value ->
        if (index > 0) out.append(", ")
        value.renderTo(out, typing, Always, customToString, rendered, filterProperties)
    }
    if (filteredObjects.isNotEmpty()) out.append(" ")
    out.append("]")
}

private fun RenderingContext.renderNonCompressedObjectsTo(out: StringBuilder, objects: List<*>) {
    val indent = "    "
    out.append("[")
    val filteredObjects = objects
        .filterIndexed { index, _ -> filterProperties(objects, "$index") }
        .filter { filterProperties(it, null) }
    if (filteredObjects.isNotEmpty()) out.append("\n")
    filteredObjects.forEachIndexed { index, value ->
        if (index > 0) out.append(",\n")
        out.append(indent)
        val renderedElement = render({ compression = Never }, isolated = false) { renderTo2(it, value) }.prependIndent(indent)
        out.append(renderedElement, indent.length, renderedElement.length)
    }
    if (filteredObjects.isNotEmpty()) out.append("\n")
    out.append("]")
}

internal fun renderObject(
    obj: Any,
    typing: Typing = Untyped,
    compression: Compression = Auto(),
    customToString: CustomToString = IgnoreForPlainCollectionsAndMaps,
    rendered: MutableSet<Any> = mutableSetOf(),
    filterProperties: PropertyFilter = { _, _ -> true },
): String = buildString {
    RenderingContext(
        typing = typing,
        compression = compression,
        customToString = customToString,
        rendered = rendered,
        filterProperties = filterProperties
    ).renderObjectTo(this, obj)
}

internal fun RenderingContext.renderObjectTo(out: StringBuilder, obj: Any) {
    when (compression) {
        Always -> renderCompressedObjectTo(out, obj)
        Never -> renderNonCompressedObjectTo(out, obj)
        is Auto -> {
            render(isolated = true) { renderCompressedObjectTo(it, obj) }
                .takeUnless { it.needsCompression }
                ?.also { out.append(it); rendered.add(obj) }
                ?: renderNonCompressedObjectTo(out, obj)
        }
    }
}

private fun RenderingContext.renderCompressedObjectTo(out: StringBuilder, obj: Any) {
    out.append("{")
    val entries = (if (obj is Map<*, *>) obj.entries else obj.properties.entries)
        .map { (key, value) ->
            val renderedKey =
                if (key is CharSequence) key.quoted.removeSurrounding("\"")
                else render({ compression = Always }) { renderTo2(it, key) }
            renderedKey to value
        }
        .filter { (key, _) -> filterProperties(obj, key) }
        .filter { (_, value) -> filterProperties(value, null) }
    if (entries.isNotEmpty()) out.append(" ")
    entries.forEachIndexed { index, (key, value) ->
        if (index > 0) out.append(", ")

        out.append(key)
        out.append(": ")

        val renderedValue = render({ compression = Always }) { renderTo2(it, value) }
        out.append(renderedValue)
    }
    if (entries.isNotEmpty()) out.append(" ")
    out.append("}")
}

private fun RenderingContext.renderNonCompressedObjectTo(out: StringBuilder, obj: Any) {
    val keyIndent = "    "
    out.append("{")
    val entries = (if (obj is Map<*, *>) obj.entries else obj.properties.entries)
        .map { (key, value) ->
            val renderedKey =
                if (key is CharSequence) key.quoted.removeSurrounding("\"")
                else render({ compression = Always }) { renderTo2(it, key) }
            renderedKey to value
        }
        .filter { (key, _) -> filterProperties(obj, key) }
        .filter { (_, value) -> filterProperties(value, null) }
    if (entries.isNotEmpty()) out.append("\n")
    entries.forEachIndexed { index, (key, value) ->
        if (index > 0) out.append(",\n")
        out.append(keyIndent)

        out.append(key)
        out.append(": ")

        val valueIndent = " ".repeat(keyIndent.length + key.length + 2)
        val renderedValue = render({ compression = Never }) { renderTo2(it, value) }.prependIndent(valueIndent)
        out.append(renderedValue, valueIndent.length, renderedValue.length)
    }
    if (entries.isNotEmpty()) out.append("\n")
    out.append("}")
}
