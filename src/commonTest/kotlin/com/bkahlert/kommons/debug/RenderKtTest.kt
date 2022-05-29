package com.bkahlert.kommons.debug

import com.bkahlert.kommons.debug.Compression.Always
import com.bkahlert.kommons.debug.Compression.Never
import com.bkahlert.kommons.debug.CustomToString.Ignore
import com.bkahlert.kommons.debug.CustomToString.IgnoreForPlainCollectionsAndMaps
import com.bkahlert.kommons.debug.Typing.SimplyTyped
import com.bkahlert.kommons.debug.Typing.Untyped
import com.bkahlert.kommons.tests
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlin.test.Test

class RenderTest {

    @Test fun render_null() = tests {
        null.render() shouldBe "null"
    }

    @Suppress("SpellCheckingInspection")
    @Test fun render_string() = tests {
        renderString("", compression = Always) shouldBe "\"\""
        renderString(" ", compression = Always) shouldBe "\" \""
        renderString("string", compression = Always) shouldBe "\"string\""
        renderString("line 1\nline 2", compression = Always) shouldBe "\"line 1\\nline 2\""

        renderString("", compression = Never) shouldBe ""
        renderString(" ", compression = Never) shouldBe " "
        renderString("string", compression = Never) shouldBe "string"
        renderString("line 1\nline 2", compression = Never) shouldBe """
            line 1
            line 2
        """.trimIndent()

        renderString("") shouldBe "\"\""
        renderString(" ") shouldBe "\" \""
        renderString("string") shouldBe "\"string\""
        renderString("line 1\nline 2") shouldBe """
            line 1
            line 2
        """.trimIndent()
    }

    @Test fun render_primitive() = tests {
        renderPrimitive(PrimitiveTypes.boolean) shouldBe "true"
        renderPrimitive(PrimitiveTypes.char) shouldBe "*"
        renderPrimitive(PrimitiveTypes.float) shouldBe "42.1"
        renderPrimitive(PrimitiveTypes.double) shouldBe "42.12"

        renderPrimitive(PrimitiveTypes.uByte) shouldBe "0x27"
        renderPrimitive(PrimitiveTypes.uShort) shouldBe "0x28"
        renderPrimitive(PrimitiveTypes.uInt) shouldBe "0x29"
        renderPrimitive(PrimitiveTypes.uLong) shouldBe "0x2a"

        renderPrimitive(PrimitiveTypes.byte) shouldBe "0x27"
        renderPrimitive(PrimitiveTypes.short) shouldBe "0x28"
        renderPrimitive(PrimitiveTypes.int) shouldBe "0x29"
        renderPrimitive(PrimitiveTypes.long) shouldBe "0x2a"

        renderPrimitive("string") shouldBe "⁉️"
    }

    @Test fun render_primitive_array() = tests {
        renderPrimitiveArray(PrimitiveTypes.booleanArray) shouldBe "[true, false, false]"
        renderPrimitiveArray(PrimitiveTypes.charArray) shouldBe "[a, r, r, a, y]"
        renderPrimitiveArray(PrimitiveTypes.floatArray) shouldBe "[0x61, 0x72, 0x72, 0x61, 0x79]"
        renderPrimitiveArray(PrimitiveTypes.doubleArray) shouldBe "[0x61, 0x72, 0x72, 0x61, 0x79]"

        renderPrimitiveArray(PrimitiveTypes.uByteArray) shouldBe "0x6172726179"
        renderPrimitiveArray(PrimitiveTypes.uShortArray) shouldBe "[0x61, 0x72, 0x72, 0x61, 0x79]"
        renderPrimitiveArray(PrimitiveTypes.uIntArray) shouldBe "[0x61, 0x72, 0x72, 0x61, 0x79]"
        renderPrimitiveArray(PrimitiveTypes.uLongArray) shouldBe "[0x61, 0x72, 0x72, 0x61, 0x79]"

        renderPrimitiveArray(PrimitiveTypes.byteArray) shouldBe "0x6172726179"
        renderPrimitiveArray(PrimitiveTypes.shortArray) shouldBe "[0x61, 0x72, 0x72, 0x61, 0x79]"
        renderPrimitiveArray(PrimitiveTypes.intArray) shouldBe "[0x61, 0x72, 0x72, 0x61, 0x79]"
        renderPrimitiveArray(PrimitiveTypes.longArray) shouldBe "[0x61, 0x72, 0x72, 0x61, 0x79]"
    }

    @Suppress("SpellCheckingInspection")
    @Test fun render_array() = tests {
        renderArray(arrayOf<String>(), compression = Always) shouldBe "[]"
        renderArray(arrayOf("string"), compression = Always) shouldBe "[ \"string\" ]"
        renderArray(arrayOf("string", null), compression = Always) shouldBe "[ \"string\", null ]"
        renderArray(arrayOf("string", "line 1\nline 2"), compression = Always) shouldBe "[ \"string\", \"line 1\\nline 2\" ]"

        renderArray(arrayOf<String>(), compression = Never) shouldBe "[]"
        renderArray(arrayOf("string"), compression = Never) shouldBe """
            [
                string
            ]
        """.trimIndent()
        renderArray(arrayOf("string", null), compression = Never) shouldBe """
            [
                string,
                null
            ]
        """.trimIndent()
        renderArray(arrayOf("string", "line 1\nline 2"), compression = Never) shouldBe """
            [
                string,
                line 1
                line 2
            ]
        """.trimIndent()

        renderArray(arrayOf<String>()) shouldBe "[]"
        renderArray(arrayOf("string")) shouldBe "[ \"string\" ]"
        renderArray(arrayOf("string", null)) shouldBe "[ \"string\", null ]"
        renderArray(arrayOf("string", "line 1 -------------------------\nline 2 -------------------------")) shouldBe """
            [
                string,
                line 1 -------------------------
                line 2 -------------------------
            ]
        """.trimIndent()

        renderArray(arrayOf(ClassWithDefaultToString()), typing = SimplyTyped) shouldBe """
            [
                !ClassWithDefaultToString {
                    foo: null,
                    bar: !String 
                         baz
                }
            ]
        """.trimIndent()
    }

    @Suppress("SpellCheckingInspection")
    @Test fun render_collection() = tests {
        renderCollection(listOf<String>(), compression = Always) shouldBe "[]"
        renderCollection(listOf("string"), compression = Always) shouldBe "[ \"string\" ]"
        renderCollection(listOf("string", null), compression = Always) shouldBe "[ \"string\", null ]"
        renderCollection(listOf("string", "line 1\nline 2"), compression = Always) shouldBe "[ \"string\", \"line 1\\nline 2\" ]"

        renderCollection(listOf<String>(), compression = Never) shouldBe "[]"
        renderCollection(listOf("string"), compression = Never) shouldBe """
            [
                string
            ]
        """.trimIndent()
        renderCollection(listOf("string", null), compression = Never) shouldBe """
            [
                string,
                null
            ]
        """.trimIndent()
        renderCollection(listOf("string", "line 1\nline 2"), compression = Never) shouldBe """
            [
                string,
                line 1
                line 2
            ]
        """.trimIndent()

        renderCollection(listOf<String>()) shouldBe "[]"
        renderCollection(listOf("string")) shouldBe "[ \"string\" ]"
        renderCollection(listOf("string", null)) shouldBe "[ \"string\", null ]"
        renderCollection(listOf("string", "line 1 -------------------------\nline 2 -------------------------")) shouldBe """
            [
                string,
                line 1 -------------------------
                line 2 -------------------------
            ]
        """.trimIndent()

        renderCollection(listOf(ClassWithDefaultToString()), typing = SimplyTyped) shouldBe """
            [
                !ClassWithDefaultToString {
                    foo: null,
                    bar: !String 
                         baz
                }
            ]
        """.trimIndent()
    }

    @Suppress("SpellCheckingInspection")
    @Test fun render_map() = tests {
        renderObject(emptyMap<String, Any?>(), compression = Always) shouldBe "{}"
        renderObject(mapOf("foo" to "string"), compression = Always) shouldBe "{ foo: \"string\" }"
        renderObject(mapOf("foo" to "string", "bar" to null), compression = Always) shouldBe "{ foo: \"string\", bar: null }"
        renderObject(mapOf("foo" to "string", "bar" to "line 1\nline 2"), compression = Always) shouldBe "{ foo: \"string\", bar: \"line 1\\nline 2\" }"

        renderObject(emptyMap<String, Any?>(), compression = Never) shouldBe "{}"
        renderObject(mapOf("foo" to "string"), compression = Never) shouldBe """
            {
                foo: string
            }
        """.trimIndent()
        renderObject(mapOf("foo" to "string", "bar" to null), compression = Never) shouldBe """
            {
                foo: string,
                bar: null
            }
        """.trimIndent()
        renderObject(mapOf("foo" to "string", "bar" to "line 1\nline 2"), compression = Never) shouldBe """
            {
                foo: string,
                bar: line 1
                     line 2
            }
        """.trimIndent()

        renderObject(emptyMap<String, Any?>()) shouldBe "{}"
        renderObject(mapOf("foo" to "string")) shouldBe "{ foo: \"string\" }"
        renderObject(mapOf("foo" to "string", "bar" to null)) shouldBe "{ foo: \"string\", bar: null }"
        renderObject(mapOf("foo" to "string", "bar" to "line 1 -------------------------\nline 2 -------------------------")) shouldBe """
            {
                foo: string,
                bar: line 1 -------------------------
                     line 2 -------------------------
            }
        """.trimIndent()

        renderObject(mapOf("foo" to ClassWithDefaultToString()), typing = SimplyTyped) shouldBe """
            {
                foo: !ClassWithDefaultToString {
                         foo: null,
                         bar: !String 
                              baz
                     }
            }
        """.trimIndent()
    }

    @Test fun render_map_with_any_key() = tests {
        renderObject(mapOf(DataClass() to "foo", null to "bar"), compression = Always) shouldBe """
            { DataClass(dataProperty=data-property, openBaseProperty=37): "foo", null: "bar" }
        """.trimIndent()
        renderObject(mapOf(DataClass() to "foo", null to "bar"), compression = Never) shouldBe """
            {
                DataClass(dataProperty=data-property, openBaseProperty=37): foo,
                null: bar
            }
        """.trimIndent()
        renderObject(mapOf(DataClass() to "foo", null to "bar")) shouldBe """
            {
                DataClass(dataProperty=data-property, openBaseProperty=37): foo,
                null: bar
            }
        """.trimIndent()

        renderObject(mapOf(ClassWithDefaultToString() to "foo"), typing = SimplyTyped) shouldBe """
            {
                !ClassWithDefaultToString { foo: null, bar: !String "baz" }: !String 
                                                                             foo
            }
        """.trimIndent()
    }

    @Suppress("SpellCheckingInspection")
    @Test fun render_object() = tests {
        renderObject(ClassWithDefaultToString(), compression = Always) shouldBe "{ foo: null, bar: \"baz\" }"
        renderObject(ClassWithDefaultToString("string"), compression = Always) shouldBe "{ foo: \"string\", bar: \"baz\" }"
        renderObject(ClassWithDefaultToString("line 1\nline 2"), compression = Always) shouldBe "{ foo: \"line 1\\nline 2\", bar: \"baz\" }"
        renderObject(ClassWithDefaultToString(listOf("string", null)), compression = Always) shouldBe "{ foo: [ \"string\", null ], bar: \"baz\" }"
        renderObject(ClassWithDefaultToString(ClassWithDefaultToString()), compression = Always) shouldBe "{ foo: { foo: null, bar: \"baz\" }, bar: \"baz\" }"

        renderObject(ClassWithDefaultToString(), compression = Never) shouldBe """
            {
                foo: null,
                bar: baz
            }
        """.trimIndent()
        renderObject(ClassWithDefaultToString("string"), compression = Never) shouldBe """
            {
                foo: string,
                bar: baz
            }
        """.trimIndent()
        renderObject(ClassWithDefaultToString("line 1\nline 2"), compression = Never) shouldBe """
            {
                foo: line 1
                     line 2,
                bar: baz
            }
        """.trimIndent()
        renderObject(ClassWithDefaultToString(listOf("string", null)), compression = Never) shouldBe """
            {
                foo: [
                         string,
                         null
                     ],
                bar: baz
            }
        """.trimIndent()
        renderObject(ClassWithDefaultToString(ClassWithDefaultToString()), compression = Never) shouldBe """
            {
                foo: {
                         foo: null,
                         bar: baz
                     },
                bar: baz
            }
        """.trimIndent()

        renderObject(ClassWithDefaultToString()) shouldBe "{ foo: null, bar: \"baz\" }"
        renderObject(ClassWithDefaultToString("string")) shouldBe "{ foo: \"string\", bar: \"baz\" }"
        renderObject(ClassWithDefaultToString("line 1 -------------------------\nline 2 -------------------------")) shouldBe """
            {
                foo: line 1 -------------------------
                     line 2 -------------------------,
                bar: baz
            }
        """.trimIndent()
        renderObject(ClassWithDefaultToString(listOf("string", null))) shouldBe "{ foo: [ \"string\", null ], bar: \"baz\" }"
        renderObject(ClassWithDefaultToString(ClassWithDefaultToString())) shouldBe "{ foo: { foo: null, bar: \"baz\" }, bar: \"baz\" }"

        renderObject(ClassWithDefaultToString(ClassWithDefaultToString()), typing = SimplyTyped) shouldBe """
            {
                foo: !ClassWithDefaultToString {
                         foo: null,
                         bar: !String 
                              baz
                     },
                bar: !String 
                     baz
            }
        """.trimIndent()
    }

    // render

    @Suppress("SpellCheckingInspection")
    @Test fun render() = tests {
        null.render(typing = SimplyTyped, compression = Always) shouldBe "null"
        "line 1\nline 2".render(typing = SimplyTyped, compression = Always) shouldBe "!String \"line 1\\nline 2\""
        PrimitiveTypes.double.render(typing = SimplyTyped, compression = Always) shouldBe "!Double 42.12"
        PrimitiveTypes.doubleArray.render(typing = SimplyTyped, compression = Always) shouldBe "!DoubleArray [0x61, 0x72, 0x72, 0x61, 0x79]"
        arrayOf("string", "line 1\nline 2").render(
            typing = SimplyTyped,
            compression = Always
        ) shouldBe "!Array [ !String \"string\", !String \"line 1\\nline 2\" ]"
        listOf("string", "line 1\nline 2").render(
            typing = SimplyTyped,
            compression = Always
        ) shouldBe "!List [ !String \"string\", !String \"line 1\\nline 2\" ]"
        mapOf("foo" to "string", "bar" to "line 1\nline 2").render(
            typing = SimplyTyped,
            compression = Always
        ) shouldBe "!Map { foo: !String \"string\", bar: !String \"line 1\\nline 2\" }"
        mapOf(DataClass() to "foo", null to "bar").render(
            typing = SimplyTyped,
            compression = Always
        ) shouldBe "!Map { !DataClass DataClass(dataProperty=data-property, openBaseProperty=37): !String \"foo\", null: !String \"bar\" }"
        ClassWithDefaultToString(ClassWithDefaultToString()).render(
            typing = SimplyTyped,
            compression = Always
        ) shouldBe "!ClassWithDefaultToString { foo: !ClassWithDefaultToString { foo: null, bar: !String \"baz\" }, bar: !String \"baz\" }"

        null.render(typing = SimplyTyped, compression = Never) shouldBe "null"
        "line 1\nline 2".render(typing = SimplyTyped, compression = Never) shouldBe """
            !String 
            line 1
            line 2
        """.trimIndent()
        PrimitiveTypes.double.render(typing = SimplyTyped, compression = Never) shouldBe "!Double 42.12"
        PrimitiveTypes.doubleArray.render(typing = SimplyTyped, compression = Never) shouldBe "!DoubleArray [0x61, 0x72, 0x72, 0x61, 0x79]"
        arrayOf("string", "line 1\nline 2").render(typing = SimplyTyped, compression = Never) shouldBe """
            !Array [
                !String 
                string,
                !String 
                line 1
                line 2
            ]
        """.trimIndent()
        listOf("string", "line 1\nline 2").render(typing = SimplyTyped, compression = Never) shouldBe """
            !List [
                !String 
                string,
                !String 
                line 1
                line 2
            ]
        """.trimIndent()
        mapOf("foo" to "string", "bar" to "line 1\nline 2").render(typing = SimplyTyped, compression = Never) shouldBe """
            !Map {
                foo: !String 
                     string,
                bar: !String 
                     line 1
                     line 2
            }
        """.trimIndent()
        mapOf(DataClass() to "foo", null to "bar").render(typing = SimplyTyped, compression = Never) shouldBe """
            !Map {
                !DataClass DataClass(dataProperty=data-property, openBaseProperty=37): !String 
                                                                                       foo,
                null: !String 
                      bar
            }
        """.trimIndent()
        ClassWithDefaultToString(ClassWithDefaultToString()).render(typing = SimplyTyped, compression = Never) shouldBe """
            !ClassWithDefaultToString {
                foo: !ClassWithDefaultToString {
                         foo: null,
                         bar: !String 
                              baz
                     },
                bar: !String 
                     baz
            }
        """.trimIndent()

        null.render(typing = Untyped, compression = Always) shouldBe "null"
        "line 1\nline 2".render(typing = Untyped, compression = Always) shouldBe "\"line 1\\nline 2\""
        PrimitiveTypes.double.render(typing = Untyped, compression = Always) shouldBe "42.12"
        PrimitiveTypes.doubleArray.render(typing = Untyped, compression = Always) shouldBe "[0x61, 0x72, 0x72, 0x61, 0x79]"
        arrayOf("string", "line 1\nline 2").render(typing = Untyped, compression = Always) shouldBe "[ \"string\", \"line 1\\nline 2\" ]"
        listOf("string", "line 1\nline 2").render(typing = Untyped, compression = Always) shouldBe "[ \"string\", \"line 1\\nline 2\" ]"
        mapOf("foo" to "string", "bar" to "line 1\nline 2").render(
            typing = Untyped,
            compression = Always
        ) shouldBe "{ foo: \"string\", bar: \"line 1\\nline 2\" }"
        mapOf(DataClass() to "foo", null to "bar").render(typing = Untyped, compression = Always) shouldBe """
            { DataClass(dataProperty=data-property, openBaseProperty=37): "foo", null: "bar" }
        """.trimIndent()
        ClassWithDefaultToString(ClassWithDefaultToString()).render(
            typing = Untyped,
            compression = Always
        ) shouldBe "{ foo: { foo: null, bar: \"baz\" }, bar: \"baz\" }"

        null.render(typing = Untyped, compression = Never) shouldBe "null"
        "line 1\nline 2".render(typing = Untyped, compression = Never) shouldBe """
            line 1
            line 2
        """.trimIndent()
        PrimitiveTypes.double.render(typing = Untyped, compression = Never) shouldBe "42.12"
        PrimitiveTypes.doubleArray.render(typing = Untyped, compression = Never) shouldBe "[0x61, 0x72, 0x72, 0x61, 0x79]"
        arrayOf("string", "line 1\nline 2").render(typing = Untyped, compression = Never) shouldBe """
            [
                string,
                line 1
                line 2
            ]
        """.trimIndent()
        listOf("string", "line 1\nline 2").render(typing = Untyped, compression = Never) shouldBe """
            [
                string,
                line 1
                line 2
            ]
        """.trimIndent()
        mapOf("foo" to "string", "bar" to "line 1\nline 2").render(typing = Untyped, compression = Never) shouldBe """
            {
                foo: string,
                bar: line 1
                     line 2
            }
        """.trimIndent()
        mapOf(DataClass() to "foo", null to "bar").render(typing = Untyped, compression = Never) shouldBe """
            {
                DataClass(dataProperty=data-property, openBaseProperty=37): foo,
                null: bar
            }
        """.trimIndent()
        ClassWithDefaultToString(ClassWithDefaultToString()).render(typing = Untyped, compression = Never) shouldBe """
            {
                foo: {
                         foo: null,
                         bar: baz
                     },
                bar: baz
            }
        """.trimIndent()

        null.render() shouldBe "null"
        "line 1\nline 2".render() shouldBe """
            line 1
            line 2
        """.trimIndent()
        PrimitiveTypes.double.render() shouldBe "42.12"
        PrimitiveTypes.doubleArray.render() shouldBe "[0x61, 0x72, 0x72, 0x61, 0x79]"
        arrayOf("string", "line 1\nline 2").render() shouldBe "[ \"string\", \"line 1\\nline 2\" ]"
        listOf("string", "line 1\nline 2").render() shouldBe "[ \"string\", \"line 1\\nline 2\" ]"
        mapOf("foo" to "string", "bar" to "line 1\nline 2").render() shouldBe "{ foo: \"string\", bar: \"line 1\\nline 2\" }"
        mapOf(DataClass() to "foo", null to "bar").render() shouldBe """
            {
                DataClass(dataProperty=data-property, openBaseProperty=37): foo,
                null: bar
            }
        """.trimIndent()
        ClassWithDefaultToString(ClassWithDefaultToString()).render() shouldBe "{ foo: { foo: null, bar: \"baz\" }, bar: \"baz\" }"
    }

    @Test fun render_option_custom_to_string() = tests {
        CollectionTypes.list.render(customToString = IgnoreForPlainCollectionsAndMaps) shouldBe CollectionTypes.list.render(customToString = Ignore)
        ListImplementingSingleton.render(customToString = IgnoreForPlainCollectionsAndMaps) shouldBe ListImplementingSingleton.render(customToString = Ignore)
        ClassTypes.map.render(customToString = IgnoreForPlainCollectionsAndMaps) shouldBe ClassTypes.map.render(customToString = Ignore)
        MapImplementingSingleton.render(customToString = IgnoreForPlainCollectionsAndMaps) shouldBe MapImplementingSingleton.render(customToString = Ignore)

        ClassWithDefaultToString().render(customToString = IgnoreForPlainCollectionsAndMaps) shouldBe ClassWithDefaultToString().render(customToString = Ignore)

        ClassWithCustomToString().render(customToString = IgnoreForPlainCollectionsAndMaps) shouldBe "custom toString"
        ClassWithCustomToString().render(customToString = Ignore) shouldBe "{ foo: null }"
    }

    @Test fun render_with_filter() = tests {
        ClassWithDefaultToString(ClassTypes.triple).render() shouldBe """
            { foo: (39, 40, 41), bar: "baz" }
        """.trimIndent()
        ClassWithDefaultToString(ClassTypes.triple).render { obj, _ -> obj != ClassTypes.triple } shouldBe """
            { foo: , bar: "baz" }
        """.trimIndent()
        ClassWithDefaultToString(ClassTypes.triple).render { _, prop -> prop != "bar" } shouldBe """
            { foo: (39, 40, 41) }
        """.trimIndent()
    }

    @Test fun render_circular_reference() = tests {
        SelfReferencingClass().render() shouldContain "selfProperty: <SelfReferencingClass@-?\\d+>".toRegex()
    }

    @Test fun render_function() = tests {
        ({ }).render() shouldBe when (Platform.Current) {
            Platform.JS -> """
                function () {
                      return Unit_getInstance();
                    }
            """.trimIndent()
            Platform.JVM -> """
                () -> kotlin.Unit
            """.trimIndent()
        }
    }
}
