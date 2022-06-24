package com.bkahlert.kommons.debug

import com.bkahlert.kommons.test.test
import io.kotest.assertions.asClue
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class PlainTestJvm {

    @Test fun plain_native_collection() = test {
        java.util.LinkedHashSet(listOf("foo", "bar")).asClue { it.isPlain shouldBe true }
    }

    @Test fun plain_native_map() = test {
        java.util.LinkedHashMap(mapOf("foo" to "bar", "baz" to null)).asClue { it.isPlain shouldBe true }
    }
}
