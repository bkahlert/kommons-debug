package com.bkahlert.kommons

import com.bkahlert.kommons.test.test
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ListsKtTest {

    @Test fun with_negative_indices() = test {
        val list = listOf("a", "b", "c").withNegativeIndices()
        list[-1] shouldBe "c"
        list[-5] shouldBe "b"
        list[6] shouldBe "a"
    }
}
