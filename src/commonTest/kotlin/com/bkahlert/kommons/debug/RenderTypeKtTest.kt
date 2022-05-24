package com.bkahlert.kommons.debug

import io.kotest.matchers.shouldBe
import kotlin.collections.Map.Entry
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.fail

class RenderTypeTest {

    @Test
    fun render_primitive_types() {
        PrimitiveTypes.forEach { (type, value) ->
            when (type) {
                Byte::class -> value.renderType() shouldBe when (Platform.Current) {
                    Platform.JS -> "Double"
                    Platform.JVM -> "Byte"
                }
                Short::class -> value.renderType() shouldBe "Short"
                Int::class -> value.renderType() shouldBe "Int"
                Long::class -> value.renderType() shouldBe "Long"
                Float::class -> value.renderType() shouldBe "Float"
                Double::class -> value.renderType() shouldBe "Double"
                Boolean::class -> value.renderType() shouldBe "Boolean"
                Char::class -> value.renderType() shouldBe "Char"
                String::class -> value.renderType() shouldBe "String"
                Array::class -> value.renderType() shouldBe "Array"
                IntArray::class -> value.renderType() shouldBe "IntArray"
                else -> {
                    when (type.simpleName) {
                        "Char" -> value.renderType() shouldBe "Char"
                        "Array" -> value.renderType() shouldBe "Array"
                        else -> fail("uncovered type $type")
                    }
                }
            }
        }
    }

    @Test
    fun render_unsigned_numbers() {
        UnsignedNumbers.forEach { (_, value) ->
            when (value) {
                is UByte -> value.renderType() shouldBe "UByte"
                is UShort -> value.renderType() shouldBe "UShort"
                is UInt -> value.renderType() shouldBe "UInt"
                is ULong -> value.renderType() shouldBe "ULong"
                is UByteArray -> value.renderType() shouldBe "UByteArray"
                else -> fail("uncovered case: $value")
            }
        }
    }

    @Test
    fun render_collections() {
        CollectionTypes.forEach { (_, value) ->
            when (value) {
                is Set<*> -> value.renderType() shouldBe "Set"
                is MutableList<*> -> value.renderType() shouldBe "MutableList"
                is List<*> -> value.renderType() shouldBe "List"
                is Iterable<*> -> value.renderType() shouldBe "Iterable"
                else -> fail("uncovered case: $value")
            }
        }
    }

    @Test
    fun render_classes() {
        ClassTypes.forEach { (_, value) ->
            when (value) {
                is OrdinaryClass -> value.renderType() shouldBe "OrdinaryClass"
                is Pair<*, *> -> value.renderType() shouldBe "Pair"
                is Triple<*, *, *> -> value.renderType() shouldBe "Triple"
                is KClass<*> -> value.renderType() shouldBe "KClass"
                else -> value.renderType() shouldBe "object"
            }
        }
    }

    @Test
    fun render_functions() {

        ({}).renderType() shouldBe "lambda"
        (fun() {}).renderType() shouldBe "X"
    }
}

internal abstract class TypeMap : AbstractMap<KClass<*>, Any>() {
    protected abstract val _values: Collection<Any>
    override val entries: Set<Entry<KClass<out Any>, Any>> by lazy { _values.associateBy { it::class }.entries }
}

internal object PrimitiveTypes : TypeMap() {
    val byte: Byte = 39
    val short: Short = 40
    val int: Int = 41
    val long: Long = 42
    val float: Float = 42.1f
    val double: Double = 42.12
    val boolean: Boolean = true
    val char: Char = '*'
    val string: String = "string"
    val array: Array<Char> = arrayOf('a', 'r', 'r', 'a', 'y')
    val primitiveArray: IntArray = intArrayOf('a'.code, 'r'.code, 'r'.code, 'a'.code, 'y'.code)
    override val _values: Collection<Any> = listOf(byte, short, int, long, float, double, boolean, char, string, array, primitiveArray)
}

internal object UnsignedNumbers : TypeMap() {
    val unsignedByte: UByte = 39u
    val unsignedShort: UShort = 40u
    val unsignedInt: UInt = 41u
    val unsignedLong: ULong = 42u
    val unsignedByteArray: UByteArray = ubyteArrayOf('a'.code.toUByte(), 'r'.code.toUByte(), 'r'.code.toUByte(), 'a'.code.toUByte(), 'y'.code.toUByte())
    override val _values: Collection<Any> = listOf(unsignedByte, unsignedShort, unsignedInt, unsignedLong, unsignedByteArray)
}

internal object CollectionTypes : TypeMap() {
    val iterable: Iterable<Any?> = object : Iterable<Any?> {
        override fun iterator(): Iterator<Any?> = PrimitiveTypes.values.iterator()
    }
    val set: Set<Any?> = emptySet()
    val list: List<Any?> = emptyList()
    val mutableList: MutableList<Any?> = mutableListOf(PrimitiveTypes.values)
    override val _values: Collection<Any> = listOf(iterable, set, list, mutableList)
}

internal object ClassTypes : TypeMap() {
    val singleton: Any = object {}
    val `class`: OrdinaryClass = OrdinaryClass()
    val pair: Pair<Any?, Any?> = PrimitiveTypes.byte to PrimitiveTypes.short
    val triple: Triple<Any?, Any?, Any?> = Triple(PrimitiveTypes.byte, PrimitiveTypes.short, PrimitiveTypes.int)
    val reflect: KClass<*> = OrdinaryClass::class
    override val _values: Collection<Any> = listOf(singleton, `class`, pair, triple, reflect)
}
