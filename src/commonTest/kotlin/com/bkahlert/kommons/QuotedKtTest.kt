package com.bkahlert.kommons

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class QuotedKtTest {

    @Test fun test_quotes() = tests {
        "".quoted shouldBe "\"\""
        "foo".quoted shouldBe "\"foo\""
        "{ bar: \"baz\" }".quoted shouldBe "\"{ bar: \\\"baz\\\" }\""
        'a'.quoted shouldBe "\"a\""
        '"'.quoted shouldBe "\"\\\"\""
    }

    @Test fun test_escaped_backslash() = tests {
        "\\".quoted shouldBe "\"\\\\\""
        '\\'.quoted shouldBe "\"\\\\\""
    }

    @Test fun test_escaped_line_feed() = tests {
        "\n".quoted shouldBe "\"\\n\""
        '\n'.quoted shouldBe "\"\\n\""
    }

    @Test fun test_escaped_carriage_return() = tests {
        "\r".quoted shouldBe "\"\\r\""
        '\r'.quoted shouldBe "\"\\r\""
    }

    @Test fun test_escaped_tab() = tests {
        "\t".quoted shouldBe "\"\\t\""
        '\t'.quoted shouldBe "\"\\t\""
    }

    @Test fun test_escaped_double_quote() = tests {
        "\"".quoted shouldBe "\"\\\"\""
        '"'.quoted shouldBe "\"\\\"\""
    }
}
