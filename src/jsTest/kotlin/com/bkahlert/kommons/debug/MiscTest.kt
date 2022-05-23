package com.bkahlert.kommons.debug

import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlin.js.json
import kotlin.test.Test

class MiscTest {


    @Test fun iterable_to_json() {
        emptyList<Pair<String, Any?>>().toJson().entries shouldBe json().entries
        listOf("notNull" to 42, "null" to null, "map" to mapOf("foo" to "bar")).toJson().entries shouldBe json(
            "notNull" to 42,
            "null" to null,
            "map" to mapOf("foo" to "bar"),
        ).entries
    }

    @Test fun map_to_json() {
        emptyMap<String, Any?>().toJson().entries shouldBe json().entries
        mapOf("notNull" to 42, "null" to null, "map" to mapOf("foo" to "bar")).toJson().entries shouldBe json(
            "notNull" to 42,
            "null" to null,
            "map" to mapOf("foo" to "bar"),
        ).entries
    }

    @Test fun iterable_to_json_array() {
        emptyList<Any?>().toJsonArray().entries shouldBe json().entries
        listOf(42, null, mapOf("foo" to "bar")).toJsonArray() should {
            it.size shouldBe 3
            it[0] shouldBe 42
            it[1].entries shouldBe emptyArray()
            it[2].entries shouldBe arrayOf(arrayOf("foo", "bar"))
        }
    }

    @Suppress("unused")
    @Test fun map_to_json_array() {
        emptyArray<Any?>().toJsonArray().entries shouldBe json().entries
        arrayOf(42, null, mapOf("foo" to "bar")).toJsonArray() should {
            it.size shouldBe 3
            it[0] shouldBe 42
            it[1].entries shouldBe emptyArray()
            it[2].entries shouldBe arrayOf(arrayOf("foo", "bar"))
        }
    }

    @Test fun object_stringify_extension() {
        null.stringify() shouldBe "null"
        "string".stringify() shouldBe """
            "string"
        """.trimIndent()
        arrayOf("string", 42).stringify() shouldBe """
            [
              "string",
              42
            ]
        """.trimIndent()
        listOf("string", 42).stringify() shouldBe """
            [
              "string",
              42
            ]
        """.trimIndent()
        arrayOf("string" to "value", "digit" to 42).stringify() shouldBe """
            [
              {
                "first": "string",
                "second": "value"
              },
              {
                "first": "digit",
                "second": 42
              }
            ]
        """.trimIndent()
        mapOf("string" to "value", "digit" to 42).stringify() shouldBe """
            {
              "string": "value",
              "digit": 42
            }
        """.trimIndent()
        nativeObject().stringify() shouldBe """
            {
              "property": "Function-property"
            }
            """.trimIndent()
        BaseClass().stringify() shouldBe """
            {
              "baseProperty": "base-property",
              "openBaseProperty": 42,
              "protectedOpenBaseProperty": "protected-open-base-property",
              "privateBaseProperty": "private-base-property"
            }
            """.trimIndent()
        OrdinaryClass().stringify() shouldBe """
            {
              "baseProperty": "base-property",
              "openBaseProperty": 42,
              "protectedOpenBaseProperty": "protected-open-base-property",
              "privateBaseProperty": "private-base-property",
              "ordinaryProperty": "ordinary-property",
              "privateOrdinaryProperty": "private-ordinary-property"
            }
            """.trimIndent()
        DataClass().stringify() shouldBe """
            {
              "baseProperty": "base-property",
              "openBaseProperty": 37,
              "protectedOpenBaseProperty": "overridden-protected-open-base-property",
              "privateBaseProperty": "private-base-property",
              "dataProperty": "data-property",
              "privateDataProperty": "private-data-property"
            }
            """.trimIndent()
    }

    @Test fun string_parse_extension() {
        null.stringify().parse().entries shouldBe json().entries
        "string".stringify().parse().entries shouldBe "string".entries
        arrayOf("string", 42).stringify().parse().entries shouldBe json(
            "0" to "string",
            "1" to 42,
        ).entries
        listOf("string", 42).stringify().parse().entries shouldBe json(
            "0" to "string",
            "1" to 42,
        ).entries
        arrayOf("string" to "value", "digit" to 42).stringify().parse().stringify() shouldBe arrayOf(
            "string" to "value",
            "digit" to 42,
        ).stringify()
        mapOf("string" to "value", "digit" to 42).stringify().parse().entries shouldBe json(
            "string" to "value",
            "digit" to 42
        ).entries
        nativeObject().stringify().parse().entries shouldBe json(
            "property" to "Function-property",
        ).entries
        BaseClass().stringify().parse().entries shouldBe json(
            "baseProperty" to "base-property",
            "openBaseProperty" to 42,
            "protectedOpenBaseProperty" to "protected-open-base-property",
            "privateBaseProperty" to "private-base-property",
        ).entries
        OrdinaryClass().stringify().parse().entries shouldBe json(
            "baseProperty" to "base-property",
            "openBaseProperty" to 42,
            "protectedOpenBaseProperty" to "protected-open-base-property",
            "privateBaseProperty" to "private-base-property",
            "ordinaryProperty" to "ordinary-property",
            "privateOrdinaryProperty" to "private-ordinary-property",
        ).entries
        DataClass().stringify().parse().entries shouldBe json(
            "baseProperty" to "base-property",
            "openBaseProperty" to 37,
            "protectedOpenBaseProperty" to "overridden-protected-open-base-property",
            "privateBaseProperty" to "private-base-property",
            "dataProperty" to "data-property",
            "privateDataProperty" to "private-data-property",
        ).entries
    }

    @Test fun any_to_json_extension() {
        null.toJson().entries shouldBe null.stringify().parse().entries
        "string".toJson().entries shouldBe "string".stringify().parse().entries
        arrayOf("string", 42).toJson().entries shouldBe arrayOf("string", 42).stringify().parse().entries
        listOf("string", 42).toJson().entries shouldBe listOf("string", 42).stringify().parse().entries
        arrayOf("string" to "value", "digit" to 42).toJson().stringify() shouldBe arrayOf("string" to "value", "digit" to 42).stringify().parse().stringify()
        mapOf("string" to "value", "digit" to 42).toJson().entries shouldBe mapOf("string" to "value", "digit" to 42).stringify().parse().entries
        nativeObject().toJson().entries shouldBe nativeObject().stringify().parse().entries
        BaseClass().toJson().entries shouldBe BaseClass().stringify().parse().entries
        OrdinaryClass().toJson().entries shouldBe OrdinaryClass().stringify().parse().entries
        DataClass().toJson().entries shouldBe DataClass().stringify().parse().entries
    }
}
