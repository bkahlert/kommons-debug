package com.bkahlert.kommons.debug

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ObjectTest {

    @Test
    fun test_keys() {
        Object.keys(nativeObject()) shouldBe arrayOf(
            "property",
        )
        Object.keys(BaseClass()) shouldBe arrayOf(
            "baseProperty_1",
            "openBaseProperty_1",
            "protectedOpenBaseProperty_1",
            "privateBaseProperty_1"
        )
        Object.keys(OrdinaryClass()) shouldBe arrayOf(
            "baseProperty_1",
            "openBaseProperty_1",
            "protectedOpenBaseProperty_1",
            "privateBaseProperty_1",
            "ordinaryProperty_1",
            "privateOrdinaryProperty_1"
        )
        Object.keys(DataClass()) shouldBe arrayOf(
            "baseProperty_1",
            "openBaseProperty_1",
            "protectedOpenBaseProperty_1",
            "privateBaseProperty_1",
            "dataProperty_1",
            "openBaseProperty_2",
            "protectedOpenBaseProperty_2",
            "privateDataProperty_1"
        )
        val customObject = ClassWithDefaultToString(null)
        Object.keys(customObject) shouldBe arrayOf(
            "foo_1",
            "bar_1",
        )
        Object.keys(ClassWithDefaultToString(customObject)) shouldBe arrayOf(
            "foo_1",
            "bar_1",
        )
    }

    @Test fun test_entries() {
        Object.entries(nativeObject()) shouldBe arrayOf(
            arrayOf("property", "Function-property"),
        )
        Object.entries(BaseClass()) shouldBe arrayOf(
            arrayOf("baseProperty_1", "base-property"),
            arrayOf("openBaseProperty_1", 42),
            arrayOf("protectedOpenBaseProperty_1", "protected-open-base-property"),
            arrayOf("privateBaseProperty_1", "private-base-property"),
        )
        Object.entries(OrdinaryClass()) shouldBe arrayOf(
            arrayOf("baseProperty_1", "base-property"),
            arrayOf("openBaseProperty_1", 42),
            arrayOf("protectedOpenBaseProperty_1", "protected-open-base-property"),
            arrayOf("privateBaseProperty_1", "private-base-property"),
            arrayOf("ordinaryProperty_1", "ordinary-property"),
            arrayOf("privateOrdinaryProperty_1", "private-ordinary-property"),
        )
        Object.entries(DataClass()) shouldBe arrayOf(
            arrayOf("baseProperty_1", "base-property"),
            arrayOf("openBaseProperty_1", 42),
            arrayOf("protectedOpenBaseProperty_1", "protected-open-base-property"),
            arrayOf("privateBaseProperty_1", "private-base-property"),
            arrayOf("dataProperty_1", "data-property"),
            arrayOf("openBaseProperty_2", 37),
            arrayOf("protectedOpenBaseProperty_2", "overridden-protected-open-base-property"),
            arrayOf("privateDataProperty_1", "private-data-property"),
        )
        val customObject = ClassWithDefaultToString(null)
        Object.entries(customObject) shouldBe arrayOf(
            arrayOf("foo_1", null),
            arrayOf("bar_1", "baz"),
        )
        Object.entries(ClassWithDefaultToString(customObject)) shouldBe arrayOf(
            arrayOf("foo_1", customObject),
            arrayOf("bar_1", "baz"),
        )
    }

    @Test fun object_get_own_property_names() {
        Object.getOwnPropertyNames(nativeObject()) shouldBe arrayOf(
            "property",
        )
        Object.getOwnPropertyNames(BaseClass()) shouldBe arrayOf(
            "baseProperty_1",
            "openBaseProperty_1",
            "protectedOpenBaseProperty_1",
            "privateBaseProperty_1"
        )
        Object.getOwnPropertyNames(OrdinaryClass()) shouldBe arrayOf(
            "baseProperty_1",
            "openBaseProperty_1",
            "protectedOpenBaseProperty_1",
            "privateBaseProperty_1",
            "ordinaryProperty_1",
            "privateOrdinaryProperty_1"
        )
        Object.getOwnPropertyNames(DataClass()) shouldBe arrayOf(
            "baseProperty_1",
            "openBaseProperty_1",
            "protectedOpenBaseProperty_1",
            "privateBaseProperty_1",
            "dataProperty_1",
            "openBaseProperty_2",
            "protectedOpenBaseProperty_2",
            "privateDataProperty_1"
        )
        val customObject = ClassWithDefaultToString(null)
        Object.getOwnPropertyNames(customObject) shouldBe arrayOf(
            "foo_1",
            "bar_1",
        )
        Object.getOwnPropertyNames(ClassWithDefaultToString(customObject)) shouldBe arrayOf(
            "foo_1",
            "bar_1",
        )
    }

    @Test fun test_keys_extension() {
        nativeObject().keys shouldBe Object.keys(nativeObject())
        BaseClass().keys shouldBe Object.keys(BaseClass())
        OrdinaryClass().keys shouldBe Object.keys(OrdinaryClass())
        DataClass().keys shouldBe Object.keys(DataClass())
        val customObject = ClassWithDefaultToString(null)
        customObject.keys shouldBe Object.keys(customObject)
        ClassWithDefaultToString(customObject).keys shouldBe Object.keys(ClassWithDefaultToString(customObject))
    }

    @Test fun test_entries_extension() {
        nativeObject().entries shouldBe Object.entries(nativeObject())
        BaseClass().entries shouldBe Object.entries(BaseClass())
        OrdinaryClass().entries shouldBe Object.entries(OrdinaryClass())
        DataClass().entries shouldBe Object.entries(DataClass())
        val customObject = ClassWithDefaultToString(null)
        customObject.entries shouldBe Object.entries(customObject)
        ClassWithDefaultToString(customObject).entries shouldBe Object.entries(ClassWithDefaultToString(customObject))
    }
}
