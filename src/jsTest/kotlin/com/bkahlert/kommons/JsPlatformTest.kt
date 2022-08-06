package com.bkahlert.kommons

import com.bkahlert.kommons.test.testAll
import com.bkahlert.kommons.text.AnsiSupport
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class JsPlatformTest {

    @Test fun current() = testAll {
        Platform.Current.shouldBeInstanceOf<Platform.JS>()
    }

    @Test fun ansi_support() = testAll {
        Platform.Current.ansiSupport shouldBe AnsiSupport.NONE
    }
}
