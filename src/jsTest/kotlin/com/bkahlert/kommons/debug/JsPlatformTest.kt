package com.bkahlert.kommons.debug

import com.bkahlert.kommons.tests
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class JsPlatformTest {

    @Test fun current() = tests {
        Platform.Current shouldBe Platform.JS
    }
}
