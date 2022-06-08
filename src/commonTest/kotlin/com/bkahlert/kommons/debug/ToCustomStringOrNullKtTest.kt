package com.bkahlert.kommons.debug

import com.bkahlert.kommons.Current
import com.bkahlert.kommons.Platform
import com.bkahlert.kommons.Platform.JS
import com.bkahlert.kommons.Platform.JVM
import com.bkahlert.kommons.tests
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ToCustomStringOrNullTest {

    @Test fun test_null() = tests {
        null.toCustomStringOrNull() shouldBe null
    }

    @Test fun test_string() = tests {
        "string".toCustomStringOrNull() shouldBe "string"
    }

    @Test fun test_lambda() = tests {
        {}.toCustomStringOrNull() shouldBe when (Platform.Current) {
            JS -> """
                function () {
                      return Unit_getInstance();
                    }
            """.trimIndent()
            JVM -> """
                () -> kotlin.Unit
            """.trimIndent()
        }
    }

    @Test fun test_object_with_default_tostring() = tests {
        ClassWithDefaultToString().toCustomStringOrNull() shouldBe null
    }

    @Test fun test_object_with_custom_tostring() = tests {
        ClassWithCustomToString().render() shouldBe "custom toString"
    }
}
