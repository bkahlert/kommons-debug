package com.bkahlert.kommons

import com.bkahlert.kommons.test.test
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class JsProgramTest {

    @Test fun name() = test {
        Program.name shouldBe null
    }

    @Test fun group() = test {
        Program.group shouldBe null
    }

    @Test fun version() = test {
        Program.version shouldBe null
    }

    @Test fun is_debugging() = test {
        Program.isDebugging shouldBe false
    }

    @Test fun on_exit() = test {
        shouldNotThrowAny {
            Program.onExit {
//                console.log("${Platform.Current::class.simpleName} did unload")
            }
        }
    }
}
