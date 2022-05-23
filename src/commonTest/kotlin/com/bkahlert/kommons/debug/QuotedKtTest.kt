package com.bkahlert.kommons.debug

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class QuotedTest {

    @Test
    fun test_quotes() {
        "".quoted shouldBe "\"\""
        "foo".quoted shouldBe "\"foo\""
        "{ bar: \"baz\" }".quoted shouldBe "\"{ bar: \\\"baz\\\" }\""
    }

    @Test
    fun test_escaped_backslash() {
        "\\".quoted shouldBe "\"\\\\\""
    }

    @Test
    fun test_escaped_line_feed() {
        "\n".quoted shouldBe "\"\\n\""
    }

    @Test
    fun test_escaped_carriage_return() {
        "\r".quoted shouldBe "\"\\r\""
    }

    @Test
    fun test_escaped_tab() {
        "\t".quoted shouldBe "\"\\t\""
    }

    @Test
    fun test_escaped_double_quote() {
        "\"".quoted shouldBe "\"\\\"\""
    }
}
