package com.bkahlert.kommons

import com.bkahlert.kommons.test.test
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class TuplesKtTest {

    @Test fun too() = test {
        "a" to 1 too listOf(1.2, 3.4) shouldBe Triple("a", 1, listOf(1.2, 3.4))
    }

    @Test fun map() = test {
        Pair("a", 1).map { it.toString() } shouldBe Pair("a", "1")
        Pair(1, 2).map { it * 2 } shouldBe Pair(2, 4)

        Triple("a", 1, listOf(1.2, 3.4)).map { it.toString() } shouldBe Triple("a", "1", "[1.2, 3.4]")
        Triple(1, 2, 3).map { it * 2 } shouldBe Triple(2, 4, 6)
    }
}
