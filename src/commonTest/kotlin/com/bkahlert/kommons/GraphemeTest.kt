package com.bkahlert.kommons

import com.bkahlert.kommons.test.test
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

class GraphemeTest {

    @Test fun to_grapheme_list() = test {
        "".toGraphemeList().shouldBeEmpty()
        "a".toGraphemeList().shouldContainExactly(Grapheme("a"))
        "¶".toGraphemeList().shouldContainExactly(Grapheme("¶"))
        "☰".toGraphemeList().shouldContainExactly(Grapheme("☰"))
        "𝕓".toGraphemeList().shouldContainExactly(Grapheme("𝕓"))
        "a̳o".toGraphemeList().shouldContainExactly(Grapheme("a̳"), Grapheme("o")) // combining mark

        "🫠".toGraphemeList().shouldContainExactly(Grapheme("🫠")) // emoji
        "🇩🇪".toGraphemeList().shouldContainExactly(Grapheme("🇩🇪")) // regional indicators
        "👨🏾‍🦱".toGraphemeList().shouldContainExactly(Grapheme("👨🏾‍🦱")) // emoji + skin tone modifier + ZWJ + curly hair
        "👩‍👩‍👦‍👦".toGraphemeList().shouldContainExactly(Grapheme("👩‍👩‍👦‍👦")) // long ZWJ sequence
    }

    @Test fun grapheme_count() = test {
        "".graphemeCount() shouldBe 0
        "a".graphemeCount() shouldBe 1
        "a̳o".graphemeCount() shouldBe 2
        "🫠🇩🇪👨🏾‍🦱👩‍👩‍👦‍👦".graphemeCount() shouldBe 4
        "🫠🇩🇪👨🏾‍🦱👩‍👩‍👦‍👦".graphemeCount(startIndex = 2) shouldBe 3
        "🫠🇩🇪👨🏾‍🦱👩‍👩‍👦‍👦".graphemeCount(endIndex = 6) shouldBe 2

        "\uD83C\uDFF3\u200D\uD83C\uDF08".toGraphemeList() shouldHaveSize 1 // rainbow flag
        "\uD83C\uDFF3️\uFE0E\u200D\uD83C\uDF08".toGraphemeList() shouldHaveSize 1 // text-variant flag + ZWJ + rainbow
        "\uD83C\uDFF3️\uFE0F\u200D\uD83C\uDF08".toGraphemeList() shouldHaveSize 1 // emoji-variant flag + ZWJ + rainbow
    }

    @Test fun equality() = test {
        Grapheme("a") shouldNotBe Grapheme("¶")
        Grapheme("¶") shouldBe Grapheme("¶")
    }

    @Test fun string() = test {
        Grapheme("a").string shouldBe "a"
        Grapheme("¶").string shouldBe "¶"
        Grapheme("☰").string shouldBe "☰"
        Grapheme("𝕓").string shouldBe "𝕓"
        Grapheme("a̳").string shouldBe "a̳"
    }

    @Test fun to_string() = test {
        Grapheme("a").toString() shouldBe "a"
        Grapheme("¶").toString() shouldBe "¶"
        Grapheme("☰").toString() shouldBe "☰"
        Grapheme("𝕓").toString() shouldBe "𝕓"
        Grapheme("a̳").toString() shouldBe "a̳"
    }

    @Test fun code_points() = test {
        Grapheme("a").codePoints shouldBe "a".toCodePointList()
        Grapheme("¶").codePoints shouldBe "¶".toCodePointList()
        Grapheme("☰").codePoints shouldBe "☰".toCodePointList()
        Grapheme("𝕓").codePoints shouldBe "𝕓".toCodePointList()
        Grapheme("a̳").codePoints shouldBe "a̳".toCodePointList()
    }
}
