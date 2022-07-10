package com.bkahlert.kommons

import com.bkahlert.kommons.RoundingMode.Ceiling
import com.bkahlert.kommons.RoundingMode.Floor
import com.bkahlert.kommons.RoundingMode.HalfUp
import com.bkahlert.kommons.debug.asEmoji
import com.bkahlert.kommons.test.test
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class EmojiKtTest {

    @Test fun as_emoji() = test {
        null.asEmoji() shouldBe "❔"
        true.asEmoji() shouldBe "✅"
        false.asEmoji() shouldBe "❌"

        instant0202.asEmoji() shouldBe "🕝"
        instant0202.asEmoji(Ceiling) shouldBe "🕝"
        instant0202.asEmoji(Floor) shouldBe "🕑"
        instant0202.asEmoji(HalfUp) shouldBe "🕑"

        instant2232.asEmoji() shouldBe "🕚"
        instant2232.asEmoji(Ceiling) shouldBe "🕚"
        instant2232.asEmoji(Floor) shouldBe "🕥"
        instant2232.asEmoji(HalfUp) shouldBe "🕥"

        "other".asEmoji() shouldBe "🔣"
    }
}
