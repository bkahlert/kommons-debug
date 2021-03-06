package com.bkahlert.kommons

import com.bkahlert.kommons.test.test
import io.kotest.assertions.throwables.shouldThrow
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
        "ΒΆ".toGraphemeList().shouldContainExactly(Grapheme("ΒΆ"))
        "β°".toGraphemeList().shouldContainExactly(Grapheme("β°"))
        "π".toGraphemeList().shouldContainExactly(Grapheme("π"))
        "aΜ³o".toGraphemeList().shouldContainExactly(Grapheme("aΜ³"), Grapheme("o")) // combining mark

        "π« ".toGraphemeList().shouldContainExactly(Grapheme("π« ")) // emoji
        "π©πͺ".toGraphemeList().shouldContainExactly(Grapheme("π©πͺ")) // regional indicators
        "π¨πΎβπ¦±".toGraphemeList().shouldContainExactly(Grapheme("π¨πΎβπ¦±")) // emoji + skin tone modifier + ZWJ + curly hair
        "π©βπ©βπ¦βπ¦".toGraphemeList().shouldContainExactly(Grapheme("π©βπ©βπ¦βπ¦")) // long ZWJ sequence
    }

    @Test fun grapheme_count() = test {
        "".graphemeCount() shouldBe 0
        "a".graphemeCount() shouldBe 1
        "aΜ³o".graphemeCount() shouldBe 2
        "π« π©πͺπ¨πΎβπ¦±π©βπ©βπ¦βπ¦".graphemeCount() shouldBe 4
        "π« π©πͺπ¨πΎβπ¦±π©βπ©βπ¦βπ¦".graphemeCount(startIndex = 2) shouldBe 3
        "π« π©πͺπ¨πΎβπ¦±π©βπ©βπ¦βπ¦".graphemeCount(endIndex = 6) shouldBe 2

        "\uD83C\uDFF3\u200D\uD83C\uDF08".toGraphemeList() shouldHaveSize 1 // rainbow flag
        "\uD83C\uDFF3οΈ\uFE0E\u200D\uD83C\uDF08".toGraphemeList() shouldHaveSize 1 // text-variant flag + ZWJ + rainbow
        "\uD83C\uDFF3οΈ\uFE0F\u200D\uD83C\uDF08".toGraphemeList() shouldHaveSize 1 // emoji-variant flag + ZWJ + rainbow
    }

    @Test fun equality() = test {
        Grapheme("a") shouldNotBe Grapheme("ΒΆ")
        Grapheme("ΒΆ") shouldBe Grapheme("ΒΆ")
    }

    @Test fun string() = test {
        Grapheme("a").string shouldBe "a"
        Grapheme("ΒΆ").string shouldBe "ΒΆ"
        Grapheme("β°").string shouldBe "β°"
        Grapheme("π").string shouldBe "π"
        Grapheme("aΜ³").string shouldBe "aΜ³"
    }

    @Test fun to_string() = test {
        Grapheme("a").toString() shouldBe "a"
        Grapheme("ΒΆ").toString() shouldBe "ΒΆ"
        Grapheme("β°").toString() shouldBe "β°"
        Grapheme("π").toString() shouldBe "π"
        Grapheme("aΜ³").toString() shouldBe "aΜ³"
    }

    @Test fun code_points() = test {
        Grapheme("a").codePoints shouldBe "a".toCodePointList()
        Grapheme("ΒΆ").codePoints shouldBe "ΒΆ".toCodePointList()
        Grapheme("β°").codePoints shouldBe "β°".toCodePointList()
        Grapheme("π").codePoints shouldBe "π".toCodePointList()
        Grapheme("aΜ³").codePoints shouldBe "aΜ³".toCodePointList()
    }

    @Test fun as_grapheme() = test {
        shouldThrow<IllegalArgumentException> { "".asGrapheme() }
        "π¨πΎβπ¦±".asGrapheme() shouldBe Grapheme("π¨πΎβπ¦±")
        shouldThrow<IllegalArgumentException> { "π¨πΎβπ¦±π©βπ©βπ¦βπ¦".asGrapheme() }

        "".asGraphemeOrNull() shouldBe null
        "π¨πΎβπ¦±".asGraphemeOrNull() shouldBe Grapheme("π¨πΎβπ¦±")
        "π¨πΎβπ¦±π©βπ©βπ¦βπ¦".asGraphemeOrNull() shouldBe null
    }
}
