package com.bkahlert.kommons

import com.bkahlert.kommons.Text.ChunkedText
import com.bkahlert.kommons.test.testAll
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class CharTest {

    @Test fun text() = testAll {
        Char.name shouldBe "character"
        Char.textOf(String.EMPTY) shouldBe Text.emptyText()
        Char.textOf(emojiString) should beText(
            ChunkedText(
                emojiString,
                0..0,
                1..1,
                2..2,
                3..3,
                4..4,
                5..5,
                6..6,
                7..7,
                8..8,
                9..9,
                10..10,
                11..11,
                12..12,
                13..13,
                14..14,
                15..15,
                16..16,
                17..17,
                18..18,
                19..19,
                20..20,
                21..21,
                22..22,
                23..23,
                24..24,
                25..25,
                26..26,
                transform = CharSequence::get
            ),
            *emojiChars
        )
    }
}
