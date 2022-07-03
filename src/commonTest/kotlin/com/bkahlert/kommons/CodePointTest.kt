package com.bkahlert.kommons

import com.bkahlert.kommons.test.test
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

class CodePointTest {

    @Test fun to_code_point_list() = test {
        "".toCodePointList().shouldBeEmpty()
        "a".toCodePointList().shouldContainExactly(CodePoint(0x61))
        "¶".toCodePointList().shouldContainExactly(CodePoint(0xB6))
        "☰".toCodePointList().shouldContainExactly(CodePoint(0x2630))
        "𝕓".toCodePointList().shouldContainExactly(CodePoint(0x1D553))
        "a̳o".toCodePointList().shouldContainExactly(CodePoint('a'.code), CodePoint('̳'.code), CodePoint('o'.code))
    }

    @Test fun code_point_count() = test {
        "".codePointCount() shouldBe 0
        "a".codePointCount() shouldBe 1
        "¶".codePointCount() shouldBe 1
        "☰".codePointCount() shouldBe 1
        "𝕓".codePointCount() shouldBe 1
        "a̳o".codePointCount() shouldBe 3
        "a̳o".codePointCount(startIndex = 1) shouldBe 2
        "a̳o".codePointCount(startIndex = 2) shouldBe 1
        "a̳o".codePointCount(startIndex = 3) shouldBe 0
        "a̳o".codePointCount(endIndex = 1) shouldBe 1
        "a̳o".codePointCount(endIndex = 2) shouldBe 2
        "a̳o".codePointCount(endIndex = 3) shouldBe 3
        "🫠🇩🇪👨🏾‍🦱👩‍👩‍👦‍👦".codePointCount() shouldBe 14
    }

    @Test fun instantiate() = test {
        shouldThrow<IndexOutOfBoundsException> { CodePoint(CodePoint.MIN_INDEX - 1) }
        shouldNotThrowAny { CodePoint(CodePoint.MIN_INDEX) }
        shouldNotThrowAny { CodePoint(0x61) }
        shouldNotThrowAny { CodePoint(CodePoint.MAX_INDEX) }
        shouldThrow<IndexOutOfBoundsException> { CodePoint(CodePoint.MAX_INDEX + 1) }
    }

    @Test fun plus() = test {
        CodePoint(0x61) + 0 shouldBe CodePoint(0x61)
        CodePoint(0x61) + 1 shouldBe CodePoint(0x62)
        CodePoint(0x61) + 2 shouldBe CodePoint(0x63)
        shouldThrow<IndexOutOfBoundsException> { CodePoint(0x61) + CodePoint.MAX_INDEX + 1 }
    }

    @Test fun minus() = test {
        shouldThrow<IndexOutOfBoundsException> { CodePoint(0x61) - 0x62 }
        CodePoint(0x61) - 0 shouldBe CodePoint(0x61)
        CodePoint(0x61) - 1 shouldBe CodePoint(0x60)
        CodePoint(0x61) - 2 shouldBe CodePoint(0x5F)
    }

    @Test fun inc() = test {
        var codePoint = CodePoint(0x61)
        ++codePoint shouldBe CodePoint(0x61) + 1
    }

    @Test fun dec() = test {
        var codePoint = CodePoint(0x61)
        --codePoint shouldBe CodePoint(0x61) - 1
    }

    @Test fun range_to() = test {
        CodePoint(0x61)..CodePoint(0xB6) shouldBe CodePointRange(CodePoint(0x61), CodePoint(0xB6))
    }

    @Test fun equality() = test {
        CodePoint(0x61) shouldNotBe CodePoint(0xB6)
        CodePoint(0xB6) shouldBe CodePoint(0xB6)
    }

    @Test fun compare() = test {
        CodePoint(0x61) shouldBeLessThan CodePoint(0xB6)
        CodePoint(0x2630) shouldBeGreaterThan CodePoint(0xB6)
        CodePoint(0xB6) shouldBeEqualComparingTo CodePoint(0xB6)
    }

    @Test fun string() = test {
        CodePoint(0x61).string should {
            it shouldBe "a"
            it.encodeToByteArray() shouldBe ubyteArrayOf(0x61u).toByteArray()
        }
        CodePoint(0xB6).string should {
            it shouldBe "¶"
            it.encodeToByteArray() shouldBe ubyteArrayOf(0xC2u, 0xB6u).toByteArray()
        }
        CodePoint(0x2630).string should {
            it shouldBe "☰"
            it.encodeToByteArray() shouldBe ubyteArrayOf(0xE2u, 0x98u, 0xB0u).toByteArray()
        }
        CodePoint(0x1D553).string should {
            it shouldBe "𝕓"
            it.encodeToByteArray() shouldBe ubyteArrayOf(0xF0u, 0x9Du, 0x95u, 0x93u).toByteArray()
        }
    }

    @Test fun to_string() = test {
        CodePoint(0x61).toString() shouldBe "a"
        CodePoint(0xB6).toString() shouldBe "¶"
        CodePoint(0x2630).toString() shouldBe "☰"
        CodePoint(0x1D553).toString() shouldBe "𝕓"
    }

    @Test fun char() = test {
        CodePoint(0x61).char shouldBe 'a'
        CodePoint(0xB6).char shouldBe '¶'
        CodePoint(0x2630).char shouldBe '☰'
        CodePoint(0x1D553).char shouldBe null
    }

    @Test fun chars() = test {
        CodePoint(0x61).chars shouldBe "a".toCharArray()
        CodePoint(0xB6).chars shouldBe "¶".toCharArray()
        CodePoint(0x2630).chars shouldBe "☰".toCharArray()
        CodePoint(0x1D553).chars shouldBe "𝕓".toCharArray()
    }

    @Test fun char_count() = test {
        CodePoint(0x61).charCount shouldBe 1
        CodePoint(0xB6).charCount shouldBe 1
        CodePoint(0x2630).charCount shouldBe 1
        CodePoint(0x1D553).charCount shouldBe 2
    }

    @Test fun code_point() = test {
        'a'.codePoint shouldBe CodePoint(0x61)
        '¶'.codePoint shouldBe CodePoint(0xB6)
        '☰'.codePoint shouldBe CodePoint(0x2630)
    }

    @Test fun as_code_point() = test {
        0x61.toByte().asCodePoint() shouldBe CodePoint(0x61)
        0xB6.toByte().asCodePoint() shouldBe CodePoint(0xB6)

        shouldThrow<IllegalArgumentException> { "".asCodePoint() }
        "a".asCodePoint() shouldBe CodePoint(0x61)
        "¶".asCodePoint() shouldBe CodePoint(0xB6)
        "☰".asCodePoint() shouldBe CodePoint(0x2630)
        "𝕓".asCodePoint() shouldBe CodePoint(0x1D553)
        shouldThrow<IllegalArgumentException> { "a̳o".asCodePoint() }

        "".asCodePointOrNull() shouldBe null
        "a".asCodePointOrNull() shouldBe CodePoint(0x61)
        "¶".asCodePointOrNull() shouldBe CodePoint(0xB6)
        "☰".asCodePointOrNull() shouldBe CodePoint(0x2630)
        "𝕓".asCodePointOrNull() shouldBe CodePoint(0x1D553)
        "a̳o".asCodePointOrNull() shouldBe null
    }

    @Test fun is_0to9() = test {
        "0123456789".asCodePointSequence().forEach { it.is0to9 shouldBe true }
        "AzΑωष".asCodePointSequence().forEach { it.is0to9 shouldBe false }
    }

    @Test fun is_AtoZ() = test {
        @Suppress("SpellCheckingInspection")
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ".asCodePointSequence().forEach { it.isAtoZ shouldBe true }
        "abc123🜃🜂🜁🜄𝌀𝍖ि".asCodePointSequence().forEach { it.isAtoZ shouldBe false }
    }

    @Suppress("SpellCheckingInspection")
    @Test fun is_atoz() = test {
        "abcdefghijklmnopqrstuvwxyz".asCodePointSequence().forEach { it.isatoz shouldBe true }
        "ABC123🜃🜂🜁🜄𝌀𝍖ि".asCodePointSequence().forEach { it.isatoz shouldBe false }
    }

    @Suppress("SpellCheckingInspection")
    @Test fun is_Atoz() = test {
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".asCodePointSequence().forEach { it.isAtoz shouldBe true }
        "123🜃🜂🜁🜄𝌀𝍖ि".asCodePointSequence().forEach { it.isAtoz shouldBe false }
    }

    @Test fun is_ascii_alphanumeric() = test {
        "Az09".asCodePointSequence().forEach { it.isAsciiAlphanumeric shouldBe true }
        "Αωष🜃🜂🜁🜄𝌀𝍖ि".asCodePointSequence().forEach { it.isAsciiAlphanumeric shouldBe false }
    }

    @Test fun is_alphanumeric() = test {
        "Az09Αωष".asCodePointSequence().forEach { it.isAlphanumeric shouldBe true }
        "🜃🜂🜁🜄𝌀𝍖ि".asCodePointSequence().forEach { it.isAlphanumeric shouldBe false }
    }

    @Test fun is_letter() = test {
        "AzΑωष".asCodePointSequence().forEach { it.isLetter shouldBe true }
        "🜃🜂🜁🜄𝌀𝍖ि09".asCodePointSequence().forEach { it.isLetter shouldBe false }
    }

    @Test fun is_digit() = test {
        "0123456789".asCodePointSequence().forEach { it.isDigit shouldBe true }
        "AzΑωष".asCodePointSequence().forEach { it.isDigit shouldBe false }
    }

    @Test fun is_whitespace() = test {
        listOf(' ', '\u2000').forAll { it.codePoint.isWhitespace shouldBe true }
        "Az09Αω𝌀𝍖षि🜃🜂🜁🜄".asCodePointSequence().forEach { it.isWhitespace shouldBe false }
    }

    @Test fun code_point_range() = test {
        CodePointRange(CodePoint(0x61), CodePoint(0x6A)) should {
            it.start shouldBe CodePoint(0x61)
            it.endInclusive shouldBe CodePoint(0x6A)
            it.iterator().asSequence().joinToString { it.index.toString() } shouldBe (0x61..0x6A).map { it.toString() }.joinToString()
        }
    }
}
