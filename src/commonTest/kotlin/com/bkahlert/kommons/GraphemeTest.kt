package com.bkahlert.kommons

import com.bkahlert.kommons.test.testAll
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.sequences.shouldBeEmpty
import io.kotest.matchers.sequences.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test


class GraphemeTest {

    @Test fun grapheme_position_iterator() = testAll {
        GraphemePositionIterator("").asSequence().shouldBeEmpty()
        GraphemePositionIterator("a").asSequence().shouldContainExactly(0..0)
        GraphemePositionIterator("¶").asSequence().shouldContainExactly(0..0)
        GraphemePositionIterator("☰").asSequence().shouldContainExactly(0..0)
        GraphemePositionIterator("𝕓").asSequence().shouldContainExactly(0..1)
        GraphemePositionIterator("a̳o").asSequence().shouldContainExactly(0..1, 2..2) // combining mark
        GraphemePositionIterator("🫠").asSequence().shouldContainExactly(0..1) // emoji
        GraphemePositionIterator("🇩🇪").asSequence().shouldContainExactly(0..3) // regional indicators
        GraphemePositionIterator("👨🏾‍🦱").asSequence().shouldContainExactly(0..6) // emoji + skin tone modifier + ZWJ + curly hair
        GraphemePositionIterator("👩‍👩‍👦‍👦").asSequence().shouldContainExactly(0..10) // long ZWJ sequence
    }

    @Test fun grapheme_iterator() = testAll {
        GraphemeIterator("").asSequence().shouldBeEmpty()
        GraphemeIterator("a").asSequence().shouldContainExactly(Grapheme("a"))
        GraphemeIterator("¶").asSequence().shouldContainExactly(Grapheme("¶"))
        GraphemeIterator("☰").asSequence().shouldContainExactly(Grapheme("☰"))
        GraphemeIterator("𝕓").asSequence().shouldContainExactly(Grapheme("𝕓"))
        GraphemeIterator("a̳o").asSequence().shouldContainExactly(Grapheme("a̳"), Grapheme("o")) // combining mark
        GraphemeIterator("🫠").asSequence().shouldContainExactly(Grapheme("🫠")) // emoji
        GraphemeIterator("🇩🇪").asSequence().shouldContainExactly(Grapheme("🇩🇪")) // regional indicators
        GraphemeIterator("👨🏾‍🦱").asSequence().shouldContainExactly(Grapheme("👨🏾‍🦱")) // emoji + skin tone modifier + ZWJ + curly hair
        GraphemeIterator("👩‍👩‍👦‍👦").asSequence().shouldContainExactly(Grapheme("👩‍👩‍👦‍👦")) // long ZWJ sequence
    }

    @Test fun as_grapheme_sequence() = testAll {
        "".asGraphemeSequence().shouldBeEmpty()
        "a".asGraphemeSequence().shouldContainExactly(Grapheme("a", 0..0))
        "¶".asGraphemeSequence().shouldContainExactly(Grapheme("¶", 0..0))
        "☰".asGraphemeSequence().shouldContainExactly(Grapheme("☰", 0..0))
        "𝕓".asGraphemeSequence().shouldContainExactly(Grapheme("𝕓", 0..1))
        "a̳o".asGraphemeSequence().shouldContainExactly(Grapheme("a̳o", 0..1), Grapheme("a̳o", 2..2)) // combining mark
        "a̳o".asGraphemeSequence(startIndex = 1).shouldContainExactly(Grapheme("a̳o", 1..1), Grapheme("a̳o", 2..2))
        "a̳o".asGraphemeSequence(startIndex = 2).shouldContainExactly(Grapheme("a̳o", 2..2))
        "a̳o".asGraphemeSequence(startIndex = 3).shouldBeEmpty()
        "a̳o".asGraphemeSequence(endIndex = 1).shouldContainExactly(Grapheme("a̳o", 0..0))
        "a̳o".asGraphemeSequence(endIndex = 2).shouldContainExactly(Grapheme("a̳o", 0..1))
        "a̳o".asGraphemeSequence(endIndex = 3).shouldContainExactly(Grapheme("a̳o", 0..1), Grapheme("a̳o", 2..2))

        shouldThrowWithMessage<IndexOutOfBoundsException>("begin -1, end 0, length 0") { "".asGraphemeSequence(startIndex = -1).toList() }
        shouldThrowWithMessage<IndexOutOfBoundsException>("begin 0, end -1, length 0") { "".asGraphemeSequence(endIndex = -1).toList() }

        "🫠".asGraphemeSequence().shouldContainExactly(Grapheme("🫠", 0..1)) // emoji
        "🇩🇪".asGraphemeSequence().shouldContainExactly(Grapheme("🇩🇪", 0..3)) // regional indicators
        "👨🏾‍🦱".asGraphemeSequence().shouldContainExactly(Grapheme("👨🏾‍🦱", 0..6)) // emoji + skin tone modifier + ZWJ + curly hair
        "👩‍👩‍👦‍👦".asGraphemeSequence().shouldContainExactly(Grapheme("👩‍👩‍👦‍👦", 0..10)) // long ZWJ sequence
    }

    @Test fun to_grapheme_list() = testAll {
        "".toGraphemeList().shouldBeEmpty()
        "a".toGraphemeList().shouldContainExactly(Grapheme("a", 0..0))
        "¶".toGraphemeList().shouldContainExactly(Grapheme("¶", 0..0))
        "☰".toGraphemeList().shouldContainExactly(Grapheme("☰", 0..0))
        "𝕓".toGraphemeList().shouldContainExactly(Grapheme("𝕓", 0..1))
        "a̳o".toGraphemeList().shouldContainExactly(Grapheme("a̳o", 0..1), Grapheme("a̳o", 2..2)) // combining mark
        "a̳o".toGraphemeList(startIndex = 1).shouldContainExactly(Grapheme("a̳o", 1..1), Grapheme("a̳o", 2..2))
        "a̳o".toGraphemeList(startIndex = 2).shouldContainExactly(Grapheme("a̳o", 2..2))
        "a̳o".toGraphemeList(startIndex = 3).shouldBeEmpty()
        "a̳o".toGraphemeList(endIndex = 1).shouldContainExactly(Grapheme("a̳o", 0..0))
        "a̳o".toGraphemeList(endIndex = 2).shouldContainExactly(Grapheme("a̳o", 0..1))
        "a̳o".toGraphemeList(endIndex = 3).shouldContainExactly(Grapheme("a̳o", 0..1), Grapheme("a̳o", 2..2))

        shouldThrowWithMessage<IndexOutOfBoundsException>("begin -1, end 0, length 0") { "".toGraphemeList(startIndex = -1) }
        shouldThrowWithMessage<IndexOutOfBoundsException>("begin 0, end -1, length 0") { "".toGraphemeList(endIndex = -1) }

        "🫠".toGraphemeList().shouldContainExactly(Grapheme("🫠", 0..1)) // emoji
        "🇩🇪".toGraphemeList().shouldContainExactly(Grapheme("🇩🇪", 0..3)) // regional indicators
        "👨🏾‍🦱".toGraphemeList().shouldContainExactly(Grapheme("👨🏾‍🦱", 0..6)) // emoji + skin tone modifier + ZWJ + curly hair
        "👩‍👩‍👦‍👦".toGraphemeList().shouldContainExactly(Grapheme("👩‍👩‍👦‍👦", 0..10)) // long ZWJ sequence
    }

    @Test fun grapheme_count() = testAll {
        "".graphemeCount() shouldBe 0
        "a".graphemeCount() shouldBe 1
        "¶".graphemeCount() shouldBe 1
        "☰".graphemeCount() shouldBe 1
        "𝕓".graphemeCount() shouldBe 1
        "a̳o".graphemeCount() shouldBe 2
        "a̳o".graphemeCount(startIndex = 1) shouldBe 2
        "a̳o".graphemeCount(startIndex = 2) shouldBe 1
        "a̳o".graphemeCount(startIndex = 3) shouldBe 0
        "a̳o".graphemeCount(endIndex = 1) shouldBe 1
        "a̳o".graphemeCount(endIndex = 2) shouldBe 1
        "a̳o".graphemeCount(endIndex = 3) shouldBe 2

        shouldThrowWithMessage<IndexOutOfBoundsException>("begin -1, end 0, length 0") { "".graphemeCount(startIndex = -1) }
        shouldThrowWithMessage<IndexOutOfBoundsException>("begin 0, end -1, length 0") { "".graphemeCount(endIndex = -1) }

        "🫠".graphemeCount() shouldBe 1 // emoji
        "🇩🇪".graphemeCount() shouldBe 1 // regional indicators
        "👨🏾‍🦱".graphemeCount() shouldBe 1 // emoji + skin tone modifier + ZWJ + curly hair
        "👩‍👩‍👦‍👦".graphemeCount() shouldBe 1 // long ZWJ sequence
    }

    @Test fun equality() = testAll {
        Grapheme("a") shouldNotBe Grapheme("¶")
        Grapheme("¶") shouldBe Grapheme("¶")
        Grapheme("¶") shouldBe Grapheme("¶", null)
        Grapheme("¶") shouldBe Grapheme("¶", 0..0)
    }

    @Test fun value() = testAll {
        Grapheme("a").value shouldBe DelegatingCharSequence("a")
        Grapheme("¶").value shouldBe DelegatingCharSequence("¶")
        Grapheme("☰").value shouldBe DelegatingCharSequence("☰")
        Grapheme("𝕓").value shouldBe DelegatingCharSequence("𝕓")
        Grapheme("a̳").value shouldBe DelegatingCharSequence("a̳")
    }

    @Test fun to_string() = testAll {
        Grapheme("a").toString() shouldBe "a"
        Grapheme("¶").toString() shouldBe "¶"
        Grapheme("☰").toString() shouldBe "☰"
        Grapheme("𝕓").toString() shouldBe "𝕓"
        Grapheme("a̳").toString() shouldBe "a̳"
    }

    @Test fun code_points() = testAll {
        Grapheme("a").codePoints shouldBe "a".toCodePointList()
        Grapheme("¶").codePoints shouldBe "¶".toCodePointList()
        Grapheme("☰").codePoints shouldBe "☰".toCodePointList()
        Grapheme("𝕓").codePoints shouldBe "𝕓".toCodePointList()
        Grapheme("a̳").codePoints shouldBe "a̳".toCodePointList()
    }

    @Test fun as_grapheme() = testAll {
        shouldThrow<IllegalArgumentException> { "".asGrapheme() }
        "👨🏾‍🦱".asGrapheme() shouldBe Grapheme("👨🏾‍🦱")
        shouldThrow<IllegalArgumentException> { "👨🏾‍🦱👩‍👩‍👦‍👦".asGrapheme() }

        "".asGraphemeOrNull() shouldBe null
        "👨🏾‍🦱".asGraphemeOrNull() shouldBe Grapheme("👨🏾‍🦱")
        "👨🏾‍🦱👩‍👩‍👦‍👦".asGraphemeOrNull() shouldBe null
    }
}
