package com.bkahlert.kommons

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class JsPlatformTest {

    @Test fun current() = tests {
        Platform.Current shouldBe Platform.JS
    }
}
