package com.bkahlert.kommons.debug

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ToCustomStringOrNullTestJs {

    @Test
    fun test_lambda() {
        {}.toCustomStringOrNull() shouldBe """
            function () {
                  return Unit_getInstance();
                }
        """.trimIndent()
    }
}
