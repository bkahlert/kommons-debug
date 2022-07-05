package com.bkahlert.kommons

import com.bkahlert.kommons.test.test
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class JsPlatformTest {

    @Test fun current() = test {
        Platform.Current.shouldBeInstanceOf<Platform.JS>()
    }

    @Test fun ansi_support() = test {
        Platform.Current.ansiSupport shouldBe AnsiSupport.NONE
    }
}
