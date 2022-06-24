package com.bkahlert.kommons

import com.bkahlert.kommons.test.test
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class LineSeparatorsTest {

    @Test
    fun constants() = test {
        LineSeparators.CRLF shouldBe "\r\n"
        LineSeparators.LF shouldBe "\n"
        LineSeparators.CR shouldBe "\r"
        LineSeparators.NEL shouldBe "\u0085"
        LineSeparators.PS shouldBe "\u2029"
        LineSeparators.LS shouldBe "\u2028"
    }

    @Test
    fun list() = test {
        LineSeparators.shouldContainExactly(
            LineSeparators.CRLF,
            LineSeparators.LF,
            LineSeparators.CR,
        )
    }

    @Test
    fun common() = test {
        LineSeparators.Common.shouldContainExactly(
            LineSeparators.CRLF,
            LineSeparators.LF,
            LineSeparators.CR,
        )
    }

    @Test
    fun unicode() = test {
        LineSeparators.Unicode.shouldContainExactly(
            LineSeparators.CRLF,
            LineSeparators.LF,
            LineSeparators.CR,
            LineSeparators.NEL,
            LineSeparators.PS,
            LineSeparators.LS,
        )
    }

    @Test
    fun uncommon() = test {
        LineSeparators.Uncommon.shouldContainExactly(
            LineSeparators.NEL,
            LineSeparators.PS,
            LineSeparators.LS,
        )
    }
}
