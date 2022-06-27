package com.bkahlert.kommons

import com.bkahlert.kommons.debug.ClassWithCustomToString
import com.bkahlert.kommons.test.test
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class QuotedKtTest {

    @Test fun test_quotes() = test {
        "".quoted shouldBe "\"\""
        "foo".quoted shouldBe "\"foo\""
        "{ bar: \"baz\" }".quoted shouldBe "\"{ bar: \\\"baz\\\" }\""
        'a'.quoted shouldBe "\"a\""
        '"'.quoted shouldBe "\"\\\"\""
        ClassWithCustomToString().quoted shouldBe "\"custom toString\""
        @Suppress("CAST_NEVER_SUCCEEDS")
        (null as? ClassWithCustomToString).quoted shouldBe "null"
    }

    @Test fun test_escaped_backslash() = test {
        "\\".quoted shouldBe "\"\\\\\""
        '\\'.quoted shouldBe "\"\\\\\""
    }

    @Test fun test_escaped_line_feed() = test {
        "\n".quoted shouldBe "\"\\n\""
        '\n'.quoted shouldBe "\"\\n\""
    }

    @Test fun test_escaped_carriage_return() = test {
        "\r".quoted shouldBe "\"\\r\""
        '\r'.quoted shouldBe "\"\\r\""
    }

    @Test fun test_escaped_tab() = test {
        "\t".quoted shouldBe "\"\\t\""
        '\t'.quoted shouldBe "\"\\t\""
    }

    @Test fun test_escaped_double_quote() = test {
        "\"".quoted shouldBe "\"\\\"\""
        '"'.quoted shouldBe "\"\\\"\""
    }
}
