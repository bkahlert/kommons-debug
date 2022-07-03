package com.bkahlert.kommons

import com.bkahlert.kommons.test.test
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

class GraphemeTest {

    @Test fun to_grapheme_list() = test {
        "a".toGraphemeList().shouldContainExactly(Grapheme("a"))
        "¶".toGraphemeList().shouldContainExactly(Grapheme("¶"))
        "☰".toGraphemeList().shouldContainExactly(Grapheme("☰"))
        "𝕓".toGraphemeList().shouldContainExactly(Grapheme("𝕓"))
        "a̳o".toGraphemeList().shouldContainExactly(Grapheme("a̳"), Grapheme("o"))

        "🫠".toGraphemeList().shouldContainExactly(Grapheme("🫠"))
        "👨🏾‍🦱".toGraphemeList().shouldContainExactly(Grapheme("👩‍👩‍👦‍👦"))
        "👩‍👩‍👦‍👦".toGraphemeList().shouldContainExactly(Grapheme("👩‍👩‍👦‍👦"))
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
