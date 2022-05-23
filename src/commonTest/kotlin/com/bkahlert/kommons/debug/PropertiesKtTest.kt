package com.bkahlert.kommons.debug

import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class PropertiesTest {

    @Test
    fun object_properties_extension() {
        nativeObject().properties shouldBe mapOf(
            "property" to "Function-property",
        )
        BaseClass().properties shouldBe mapOf(
            "baseProperty" to "base-property",
            "openBaseProperty" to 42,
            "protectedOpenBaseProperty" to "protected-open-base-property",
            "privateBaseProperty" to "private-base-property",
        )
        OrdinaryClass().properties shouldContainAll mapOf(
            "baseProperty" to "base-property",
            "openBaseProperty" to 42,
            "protectedOpenBaseProperty" to "protected-open-base-property",
//            "privateBaseProperty" to "private-base-property",
            "ordinaryProperty" to "ordinary-property",
            "privateOrdinaryProperty" to "private-ordinary-property",
        )
        DataClass().properties shouldContainAll mapOf(
            "baseProperty" to "base-property",
            "openBaseProperty" to 37,
            "protectedOpenBaseProperty" to "overridden-protected-open-base-property",
//            "privateBaseProperty" to "private-base-property",
            "dataProperty" to "data-property",
            "privateDataProperty" to "private-data-property",
        )
        val customObject = ClassWithDefaultToString(null)
        customObject.properties shouldContainAll mapOf(
            "foo" to null,
            "bar" to "baz",
        )
        ClassWithDefaultToString(customObject).properties shouldContainAll mapOf(
            "foo" to customObject,
            "bar" to "baz",
        )
    }
}
