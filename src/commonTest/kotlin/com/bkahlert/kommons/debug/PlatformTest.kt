package com.bkahlert.kommons.debug

import com.bkahlert.kommons.tests
import io.kotest.assertions.throwables.shouldNotThrowAny
import kotlin.test.Test

class PlatformTest {

    @Test fun is_intellij() = tests {
        shouldNotThrowAny { Platform.isIntelliJ }
    }

    @Test fun is_debugging() = tests {
        shouldNotThrowAny { Platform.isDebugging }
    }
}
