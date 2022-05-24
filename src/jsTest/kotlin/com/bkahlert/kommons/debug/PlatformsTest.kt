package com.bkahlert.kommons.debug

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class PlatformsTest {

    @Test
    fun test_current() {
        Platform.Current shouldBe Platform.JS
    }
}
