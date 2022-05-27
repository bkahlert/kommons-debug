package com.bkahlert.kommons

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

class GraphemeTest {

    @Test fun to_grapheme_list() = tests {
        "a".toGraphemeList().shouldContainExactly(Grapheme("a"))
        "¶".toGraphemeList().shouldContainExactly(Grapheme("¶"))
        "☰".toGraphemeList().shouldContainExactly(Grapheme("☰"))
        "𝕓".toGraphemeList().shouldContainExactly(Grapheme("𝕓"))
        "a̳o".toGraphemeList().shouldContainExactly(Grapheme("a̳"), Grapheme("o"))

        listOf("A", "曲", "🟥", "a̠", "😀").forEach { it.toGraphemeList().shouldContainExactly(Grapheme(it)) }
    }

    @Test fun equality() = tests {
        Grapheme("a") shouldNotBe Grapheme("¶")
        Grapheme("¶") shouldBe Grapheme("¶")
    }

    @Test fun string() = tests {
        Grapheme("a").string shouldBe "a"
        Grapheme("¶").string shouldBe "¶"
        Grapheme("☰").string shouldBe "☰"
        Grapheme("𝕓").string shouldBe "𝕓"
        Grapheme("a̳").string shouldBe "a̳"
    }

    @Test fun to_string() = tests {
        Grapheme("a").toString() shouldBe "a"
        Grapheme("¶").toString() shouldBe "¶"
        Grapheme("☰").toString() shouldBe "☰"
        Grapheme("𝕓").toString() shouldBe "𝕓"
        Grapheme("a̳").toString() shouldBe "a̳"
    }
}
