package com.bkahlert.kommons.debug

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ToCustomStringOrNullTestJvm {

    @Test
    fun test_lambda() {
        {}.toCustomStringOrNull() shouldBe "() -> kotlin.Unit"
    }
}
