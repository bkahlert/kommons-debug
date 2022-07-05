package com.bkahlert.kommons

import com.bkahlert.kommons.Platform.JVM
import com.bkahlert.kommons.test.test
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

class JvmPlatformTest {

    @Test fun current() = test {
        Platform.Current shouldBe JVM
    }

    @Test fun ansi_support() = test {
        if (Program.isIntelliJ) Platform.Current.ansiSupport shouldNotBe AnsiSupport.NONE
        else Platform.Current.ansiSupport shouldNotBe AnsiSupport.ANSI24
    }
}
