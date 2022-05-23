package com.bkahlert.kommons.debug

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class RenderTest {

    @Test
    fun render_null() {
        null.render() shouldBe "␀"
    }

    @Test
    fun render_object_with_default_tostring() {
        ClassWithDefaultToString(null).render() shouldBe """ClassWithDefaultToString(foo=␀, bar="baz")"""
        ClassWithDefaultToString("null").render() shouldBe """ClassWithDefaultToString(foo="null", bar="baz")"""
        ClassWithDefaultToString(ClassWithDefaultToString()).render() shouldBe """ClassWithDefaultToString(foo=ClassWithDefaultToString(foo=␀, bar="baz"), bar="baz")"""
        ClassWithDefaultToString(ClassWithDefaultToString()).render(
            "\n",
            " {\n",
            "\n}"
        ) shouldBe """
            ClassWithDefaultToString {
            foo=ClassWithDefaultToString(foo=␀, bar="baz")
            bar="baz"
            }
        """.trimIndent()
    }

    @Test
    fun render_object_with_custom_tostring() {
        ClassWithCustomToString(null).render() shouldBe "custom toString"
        ClassWithCustomToString("null").render() shouldBe "custom toString"
        ClassWithCustomToString(ClassWithCustomToString()).render() shouldBe "custom toString"
    }
}
