package com.bkahlert.kommons

import com.bkahlert.kommons.test.test
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class JvmStringsKtTest {

    @Suppress("SpellCheckingInspection")
    @Test fun transliterate() = test {
        withClue("composed") { "ÄÖÜäöüẞß".transliterate("Any-Latin; de_De-ASCII") shouldBe "AEOEUeaeoeueSSss" }
        withClue("decomposed") { "ÄÖÜäöü".transliterate("Any-Latin; de_De-ASCII") shouldBe "AEOEUeaeoeue" }
        withClue("emojis") { "a𝕓🫠🇩🇪👨🏾‍🦱👩‍👩‍👦‍👦".transliterate("Any-Latin; de_De-ASCII") shouldBe "a𝕓🫠🇩🇪👨🏾‍🦱👩‍👩‍👦‍👦" }
        withClue("illegal") {
            shouldThrow<IllegalArgumentException> {
                "a𝕓🫠🇩🇪👨🏾‍🦱👩‍👩‍👦‍👦".transliterate("-illegal-")
            }
        }
    }
}
