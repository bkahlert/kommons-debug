package com.bkahlert.kommons.debug

import com.bkahlert.kommons.tests
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class QuotedTest {

    @Test fun test_quotes() = tests {
        "".quoted shouldBe "\"\""
        "foo".quoted shouldBe "\"foo\""
        "{ bar: \"baz\" }".quoted shouldBe "\"{ bar: \\\"baz\\\" }\""
    }

    @Test fun test_escaped_backslash() = tests {
        "\\".quoted shouldBe "\"\\\\\""
    }

    @Test fun test_escaped_line_feed() = tests {
        "\n".quoted shouldBe "\"\\n\""
    }

    @Test fun test_escaped_carriage_return() = tests {
        "\r".quoted shouldBe "\"\\r\""
    }

    @Test fun test_escaped_tab() = tests {
        "\t".quoted shouldBe "\"\\t\""
    }

    @Test fun test_escaped_double_quote() = tests {
        "\"".quoted shouldBe "\"\\\"\""
    }
}
