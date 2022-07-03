package com.bkahlert.kommons

import com.bkahlert.kommons.TextLength.Companion.chars
import com.bkahlert.kommons.TextLength.Companion.codePoints
import com.bkahlert.kommons.TextLength.Companion.graphemes
import com.bkahlert.kommons.test.test
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

class TextLengthKtTest {

    @Test fun xxx() = test {

        shouldThrow<IndexOutOfBoundsException> { TextLengthUnit.CODEPOINTS.length(textLengthUnitText, startIndex = -1) }
    }

    @Test fun text_length_unit__length() = test {
        TextLengthUnit.CHARS.length(emptyTextAbstractionText) shouldBe 0
        TextLengthUnit.CODEPOINTS.length(emptyTextAbstractionText) shouldBe 0
        TextLengthUnit.GRAPHEMES.length(emptyTextAbstractionText) shouldBe 0

        TextLengthUnit.CHARS.length(textLengthUnitText) shouldBe 7
        TextLengthUnit.CODEPOINTS.length(textLengthUnitText) shouldBe 5
        TextLengthUnit.GRAPHEMES.length(textLengthUnitText) shouldBe 4

        TextLengthUnit.CHARS.length(textLengthUnitText, startIndex = 1) shouldBe 6
        TextLengthUnit.CODEPOINTS.length(textLengthUnitText, startIndex = 1) shouldBe 4
        TextLengthUnit.GRAPHEMES.length(textLengthUnitText, startIndex = 1) shouldBe 3

        TextLengthUnit.CHARS.length(textLengthUnitText, endIndex = 6) shouldBe 6
        TextLengthUnit.CODEPOINTS.length(textLengthUnitText, endIndex = 6) shouldBe 5
        TextLengthUnit.GRAPHEMES.length(textLengthUnitText, endIndex = 6) shouldBe 4

        shouldThrow<IndexOutOfBoundsException> { TextLengthUnit.CHARS.length(textLengthUnitText, startIndex = -1) }
            .message shouldBe "begin -1, end 7, length 7"
        shouldThrow<IndexOutOfBoundsException> { TextLengthUnit.CODEPOINTS.length(textLengthUnitText, startIndex = -1) }
            .message shouldBe "begin -1, end 7, length 7"
        shouldThrow<IndexOutOfBoundsException> { TextLengthUnit.GRAPHEMES.length(textLengthUnitText, startIndex = -1) }
            .message shouldBe "begin -1, end 7, length 7"

        shouldThrow<IndexOutOfBoundsException> { TextLengthUnit.CHARS.length(textLengthUnitText, endIndex = 8) }
            .message shouldBe "begin 0, end 8, length 7"
        shouldThrow<IndexOutOfBoundsException> { TextLengthUnit.CODEPOINTS.length(textLengthUnitText, endIndex = 8) }
            .message shouldBe "begin 0, end 8, length 7"
        shouldThrow<IndexOutOfBoundsException> { TextLengthUnit.GRAPHEMES.length(textLengthUnitText, endIndex = 8) }
            .message shouldBe "begin 0, end 8, length 7"

        shouldThrow<IndexOutOfBoundsException> { TextLengthUnit.CHARS.length(textLengthUnitText, startIndex = 3, endIndex = 2) }
        shouldThrow<IndexOutOfBoundsException> { TextLengthUnit.CODEPOINTS.length(textLengthUnitText, startIndex = 3, endIndex = 2) }
        shouldThrow<IndexOutOfBoundsException> { TextLengthUnit.GRAPHEMES.length(textLengthUnitText, startIndex = 3, endIndex = 2) }
    }

    @Test fun text_length_unit__split() = test {
        TextLengthUnit.CHARS.split(emptyTextAbstractionText).isEmpty()
        TextLengthUnit.CODEPOINTS.split(emptyTextAbstractionText).isEmpty()
        TextLengthUnit.GRAPHEMES.split(emptyTextAbstractionText).isEmpty()

        TextLengthUnit.CHARS.split(textLengthUnitText).map { it.toString() } shouldContainExactly textLengthUnitChars
        TextLengthUnit.CODEPOINTS.split(textLengthUnitText).map { it.toString() } shouldContainExactly textLengthUnitCodePoints
        TextLengthUnit.GRAPHEMES.split(textLengthUnitText).map { it.toString() } shouldContainExactly textLengthUnitGraphemes
    }

    @Test fun text_length_unit__join() = test {
        TextLengthUnit.CHARS.let { it.join(it.split(textLengthUnitText)) } shouldBe textLengthUnitText
        TextLengthUnit.CODEPOINTS.let { it.join(it.split(textLengthUnitText)) } shouldBe textLengthUnitText
        TextLengthUnit.GRAPHEMES.let { it.join(it.split(textLengthUnitText)) } shouldBe textLengthUnitText
    }

    @Test fun text_length_unit__transform() = test {
        val transform: (MutableList<CharSequence>).() -> Unit = {
            removeFirst()
            removeLast()
            removeLast()
        }
        TextLengthUnit.CHARS.transform(textLengthUnitText, transform) shouldBe "𝕓c̳"
        TextLengthUnit.CODEPOINTS.transform(textLengthUnitText, transform) shouldBe "𝕓c"
        TextLengthUnit.GRAPHEMES.transform(textLengthUnitText, transform) shouldBe "𝕓"
    }


    @Test fun text_length__instantiate() = test {
        TextLength(42, TextLengthUnit.CODEPOINTS) should {
            it.value shouldBe 42
            it.unit shouldBe TextLengthUnit.CODEPOINTS
        }
    }

    @Test fun text_length__equals() = test {
        TextLength(42, TextLengthUnit.CODEPOINTS) should {
            it shouldBe TextLength(42, TextLengthUnit.CODEPOINTS)
            it shouldNotBe TextLength(37, TextLengthUnit.CODEPOINTS)
            it shouldNotBe TextLength(42, TextLengthUnit.GRAPHEMES)
        }
    }

    @Test fun text_length__compare() = test {
        TextLength(42, TextLengthUnit.CODEPOINTS) should {
            it shouldBeLessThan TextLength(52, TextLengthUnit.CODEPOINTS)
            it shouldBeGreaterThan TextLength(32, TextLengthUnit.CODEPOINTS)

            it shouldBeLessThan TextLength(42, TextLengthUnit.GRAPHEMES)
            it shouldBeGreaterThan TextLength(42, TextLengthUnit.CHARS)
        }
    }

    @Test fun text_length__to_string() = test {
        TextLength(0, TextLengthUnit.CHARS).toString() shouldBe "0 chars"
        TextLength(0, TextLengthUnit.CODEPOINTS).toString() shouldBe "0 codepoints"
        TextLength(0, TextLengthUnit.GRAPHEMES).toString() shouldBe "0 graphemes"

        TextLength(1, TextLengthUnit.CHARS).toString() shouldBe "1 char"
        TextLength(1, TextLengthUnit.CODEPOINTS).toString() shouldBe "1 codepoint"
        TextLength(1, TextLengthUnit.GRAPHEMES).toString() shouldBe "1 grapheme"

        TextLength(42, TextLengthUnit.CHARS).toString() shouldBe "42 chars"
        TextLength(42, TextLengthUnit.CODEPOINTS).toString() shouldBe "42 codepoints"
        TextLength(42, TextLengthUnit.GRAPHEMES).toString() shouldBe "42 graphemes"
    }

    @Test fun text_length__instantiation_extensions() = test {
        42.chars shouldBe TextLength(42, TextLengthUnit.CHARS)
        42.codePoints shouldBe TextLength(42, TextLengthUnit.CODEPOINTS)
        42.graphemes shouldBe TextLength(42, TextLengthUnit.GRAPHEMES)

        42.toTextLength(TextLengthUnit.CHARS) shouldBe TextLength(42, TextLengthUnit.CHARS)
        42.toTextLength(TextLengthUnit.CODEPOINTS) shouldBe TextLength(42, TextLengthUnit.CODEPOINTS)
        42.toTextLength(TextLengthUnit.GRAPHEMES) shouldBe TextLength(42, TextLengthUnit.GRAPHEMES)

        textLengthUnitText.length(TextLengthUnit.CHARS)
            .shouldBe(TextLength(TextLengthUnit.CHARS.length(textLengthUnitText), TextLengthUnit.CHARS))
        textLengthUnitText.length(TextLengthUnit.CODEPOINTS)
            .shouldBe(TextLength(TextLengthUnit.CODEPOINTS.length(textLengthUnitText), TextLengthUnit.CODEPOINTS))
        textLengthUnitText.length(TextLengthUnit.GRAPHEMES)
            .shouldBe(TextLength(TextLengthUnit.GRAPHEMES.length(textLengthUnitText), TextLengthUnit.GRAPHEMES))
        textLengthUnitText.length(TextLengthUnit.CHARS, startIndex = 2, endIndex = 5)
            .shouldBe(TextLength(TextLengthUnit.CHARS.length(textLengthUnitText, startIndex = 2, endIndex = 5), TextLengthUnit.CHARS))
        textLengthUnitText.length(TextLengthUnit.CODEPOINTS, startIndex = 2, endIndex = 5)
            .shouldBe(TextLength(TextLengthUnit.CODEPOINTS.length(textLengthUnitText, startIndex = 2, endIndex = 5), TextLengthUnit.CODEPOINTS))
        textLengthUnitText.length(TextLengthUnit.GRAPHEMES, startIndex = 2, endIndex = 5)
            .shouldBe(TextLength(TextLengthUnit.GRAPHEMES.length(textLengthUnitText, startIndex = 2, endIndex = 5), TextLengthUnit.GRAPHEMES))
    }

    @Test fun text_length__unary_minus() = test {
        -TextLength(42, TextLengthUnit.CHARS) shouldBe TextLength(-42, TextLengthUnit.CHARS)
        -TextLength(42, TextLengthUnit.CODEPOINTS) shouldBe TextLength(-42, TextLengthUnit.CODEPOINTS)
        -TextLength(42, TextLengthUnit.GRAPHEMES) shouldBe TextLength(-42, TextLengthUnit.GRAPHEMES)
    }

    @Test fun text_length__plus() = test {
        TextLength(42, TextLengthUnit.CHARS) + 37 shouldBe TextLength(79, TextLengthUnit.CHARS)
        TextLength(42, TextLengthUnit.CODEPOINTS) + 37 shouldBe TextLength(79, TextLengthUnit.CODEPOINTS)
        TextLength(42, TextLengthUnit.GRAPHEMES) + 37 shouldBe TextLength(79, TextLengthUnit.GRAPHEMES)

        TextLength(42, TextLengthUnit.CHARS) + TextLength(37, TextLengthUnit.CHARS) shouldBe TextLength(79, TextLengthUnit.CHARS)
        TextLength(42, TextLengthUnit.CODEPOINTS) + TextLength(37, TextLengthUnit.CODEPOINTS) shouldBe TextLength(79, TextLengthUnit.CODEPOINTS)
        TextLength(42, TextLengthUnit.GRAPHEMES) + TextLength(37, TextLengthUnit.GRAPHEMES) shouldBe TextLength(79, TextLengthUnit.GRAPHEMES)

        shouldThrow<IllegalArgumentException> {
            TextLength(42, TextLengthUnit.CODEPOINTS) + TextLength(21, TextLengthUnit.GRAPHEMES)
        }.message shouldBe "Cannot sum text lengths with different units."
    }

    @Test fun text_length__minus() = test {
        TextLength(42, TextLengthUnit.CHARS) - 37 shouldBe TextLength(5, TextLengthUnit.CHARS)
        TextLength(42, TextLengthUnit.CODEPOINTS) - 37 shouldBe TextLength(5, TextLengthUnit.CODEPOINTS)
        TextLength(42, TextLengthUnit.GRAPHEMES) - 37 shouldBe TextLength(5, TextLengthUnit.GRAPHEMES)

        TextLength(42, TextLengthUnit.CHARS) - TextLength(37, TextLengthUnit.CHARS) shouldBe TextLength(5, TextLengthUnit.CHARS)
        TextLength(42, TextLengthUnit.CODEPOINTS) - TextLength(37, TextLengthUnit.CODEPOINTS) shouldBe TextLength(5, TextLengthUnit.CODEPOINTS)
        TextLength(42, TextLengthUnit.GRAPHEMES) - TextLength(37, TextLengthUnit.GRAPHEMES) shouldBe TextLength(5, TextLengthUnit.GRAPHEMES)

        shouldThrow<IllegalArgumentException> {
            TextLength(42, TextLengthUnit.CODEPOINTS) - TextLength(21, TextLengthUnit.GRAPHEMES)
        }.message shouldBe "Cannot subtract text lengths with different units."
    }

    @Test fun text_length__times() = test {
        TextLength(42, TextLengthUnit.CHARS) * 2 shouldBe TextLength(84, TextLengthUnit.CHARS)
        TextLength(42, TextLengthUnit.CODEPOINTS) * 2 shouldBe TextLength(84, TextLengthUnit.CODEPOINTS)
        TextLength(42, TextLengthUnit.GRAPHEMES) * 2 shouldBe TextLength(84, TextLengthUnit.GRAPHEMES)

        TextLength(42, TextLengthUnit.CHARS) * 2.4 shouldBe TextLength(100, TextLengthUnit.CHARS)
        TextLength(42, TextLengthUnit.CODEPOINTS) * 2.4 shouldBe TextLength(100, TextLengthUnit.CODEPOINTS)
        TextLength(42, TextLengthUnit.GRAPHEMES) * 2.4 shouldBe TextLength(100, TextLengthUnit.GRAPHEMES)
    }

    @Test fun text_length__div() = test {
        TextLength(42, TextLengthUnit.CHARS) / 2 shouldBe TextLength(21, TextLengthUnit.CHARS)
        TextLength(42, TextLengthUnit.CODEPOINTS) / 2 shouldBe TextLength(21, TextLengthUnit.CODEPOINTS)
        TextLength(42, TextLengthUnit.GRAPHEMES) / 2 shouldBe TextLength(21, TextLengthUnit.GRAPHEMES)

        TextLength(42, TextLengthUnit.CHARS) / 2.4 shouldBe TextLength(17, TextLengthUnit.CHARS)
        TextLength(42, TextLengthUnit.CODEPOINTS) / 2.4 shouldBe TextLength(17, TextLengthUnit.CODEPOINTS)
        TextLength(42, TextLengthUnit.GRAPHEMES) / 2.4 shouldBe TextLength(17, TextLengthUnit.GRAPHEMES)

        TextLength(42, TextLengthUnit.CHARS) / TextLength(21, TextLengthUnit.CHARS) shouldBe 2
        TextLength(42, TextLengthUnit.CODEPOINTS) / TextLength(21, TextLengthUnit.CODEPOINTS) shouldBe 2
        TextLength(42, TextLengthUnit.GRAPHEMES) / TextLength(21, TextLengthUnit.GRAPHEMES) shouldBe 2

        shouldThrow<IllegalArgumentException> {
            TextLength(42, TextLengthUnit.CODEPOINTS) / TextLength(21, TextLengthUnit.GRAPHEMES)
        }.message shouldBe "Cannot divide text lengths with different units."
    }

    @Test fun text_length__is_negative() = test {
        TextLength(42, TextLengthUnit.CHARS).isNegative() shouldBe false
        TextLength(0, TextLengthUnit.CHARS).isNegative() shouldBe false
        TextLength(-42, TextLengthUnit.CHARS).isNegative() shouldBe true
    }

    @Test fun text_length__is_positive() = test {
        TextLength(42, TextLengthUnit.CHARS).isPositive() shouldBe true
        TextLength(0, TextLengthUnit.CHARS).isPositive() shouldBe false
        TextLength(-42, TextLengthUnit.CHARS).isPositive() shouldBe false
    }

    @Test fun text_length__absolute_value() = test {
        TextLength(42, TextLengthUnit.CHARS).absoluteValue shouldBe TextLength(42, TextLengthUnit.CHARS)
        TextLength(0, TextLengthUnit.CHARS).absoluteValue shouldBe TextLength(0, TextLengthUnit.CHARS)
        TextLength(-42, TextLengthUnit.CHARS).absoluteValue shouldBe TextLength(42, TextLengthUnit.CHARS)
    }
}

internal const val emptyTextAbstractionText = ""

internal const val textLengthUnitText = "a𝕓c̳🔤"

internal val textLengthUnitChars = listOf(
    "a",
    "\uD835",
    "\uDD53",
    "c",
    "̳",
    "\uD83D",
    "\uDD24",
)

internal val textLengthUnitCodePoints = listOf(
    "a",
    "𝕓",
    "c",
    "̳",
    "🔤",
)

internal val textLengthUnitGraphemes = listOf(
    "a",
    "𝕓",
    "c̳",
    "🔤",
)
