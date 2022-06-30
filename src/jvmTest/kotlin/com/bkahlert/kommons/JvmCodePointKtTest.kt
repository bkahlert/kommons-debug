package com.bkahlert.kommons

import com.bkahlert.kommons.test.test
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class JvmCodePointKtTest {

    @Test fun name() = test {
        "a".asCodePoint().name shouldBe "LATIN SMALL LETTER A"
        "¶".asCodePoint().name shouldBe "PILCROW SIGN"
        "☰".asCodePoint().name shouldBe "TRIGRAM FOR HEAVEN"
        "𝕓".asCodePoint().name shouldBe "MATHEMATICAL DOUBLE-STRUCK SMALL B"
        "🫠".asCodePoint().name shouldBe "0x1FAE0"
    }
}
