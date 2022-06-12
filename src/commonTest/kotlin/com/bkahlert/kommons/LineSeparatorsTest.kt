package com.bkahlert.kommons

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class LineSeparatorsTest {

    @Test
    fun constants() = tests {
        LineSeparators.CRLF shouldBe "\r\n"
        LineSeparators.LF shouldBe "\n"
        LineSeparators.CR shouldBe "\r"
        LineSeparators.NEL shouldBe "\u0085"
        LineSeparators.PS shouldBe "\u2029"
        LineSeparators.LS shouldBe "\u2028"
    }

    @Test
    fun list() = tests {
        LineSeparators.shouldContainExactly(
            LineSeparators.CRLF,
            LineSeparators.LF,
            LineSeparators.CR,
        )
    }

    @Test
    fun common() = tests {
        LineSeparators.Common.shouldContainExactly(
            LineSeparators.CRLF,
            LineSeparators.LF,
            LineSeparators.CR,
        )
    }


    @Test
    fun unicode() = tests {
        LineSeparators.Unicode.shouldContainExactly(
            LineSeparators.CRLF,
            LineSeparators.LF,
            LineSeparators.CR,
            LineSeparators.NEL,
            LineSeparators.PS,
            LineSeparators.LS,
        )
    }
}
