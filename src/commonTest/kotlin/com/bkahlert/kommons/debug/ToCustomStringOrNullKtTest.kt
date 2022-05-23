package com.bkahlert.kommons.debug

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ToCustomStringOrNullTest {

    @Test
    fun test_null() {
        null.toCustomStringOrNull() shouldBe null
    }

    @Test
    fun test_string() {
         "string".toCustomStringOrNull() shouldBe "string"
    }

    // fun test_lambda()

    @Test
    fun test_object_with_default_tostring() {
        ClassWithDefaultToString().toCustomStringOrNull() shouldBe null
    }

    @Test
    fun test_object_with_custom_tostring() {
        ClassWithCustomToString().render() shouldBe "custom toString"
    }
}
