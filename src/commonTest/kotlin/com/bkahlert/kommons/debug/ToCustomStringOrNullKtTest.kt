package com.bkahlert.kommons.debug

import com.bkahlert.kommons.Platform
import com.bkahlert.kommons.Platform.JS
import com.bkahlert.kommons.Platform.JVM
import com.bkahlert.kommons.test.testAll
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.fail

class ToCustomStringOrNullTest {

    @Test fun test_null() = testAll {
        null.toCustomStringOrNull() shouldBe null
    }

    @Test fun test_string() = testAll {
        "string".toCustomStringOrNull() shouldBe "string"
    }

    @Test fun test_lambda() = testAll {
        {}.toCustomStringOrNull() shouldBe when (Platform.Current) {
            is JS -> """
                function () {
                      return Unit_getInstance();
                    }
            """.trimIndent()

            is JVM -> """
                () -> kotlin.Unit
            """.trimIndent()

            else -> fail("untested platform")
        }
    }

    @Test fun test_object_with_default_tostring() = testAll {
        ClassWithDefaultToString().toCustomStringOrNull() shouldBe null
    }

    @Test fun test_object_with_custom_tostring() = testAll {
        ClassWithCustomToString().toCustomStringOrNull() shouldBe "custom toString"
    }
}
