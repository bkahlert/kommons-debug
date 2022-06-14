package com.bkahlert.kommons

import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

class CodePointTest {

    @Test fun to_code_point_list() = tests {
        "a".toCodePointList().shouldContainExactly(CodePoint(0x61))
        "¶".toCodePointList().shouldContainExactly(CodePoint(0xB6))
        "☰".toCodePointList().shouldContainExactly(CodePoint(0x2630))
        "𝕓".toCodePointList().shouldContainExactly(CodePoint(0x1D553))
        "a̳o".toCodePointList().shouldContainExactly(CodePoint('a'.code), CodePoint('̳'.code), CodePoint('o'.code))
    }

    @Test fun equality() = tests {
        CodePoint(0x61) shouldNotBe CodePoint(0xB6)
        CodePoint(0xB6) shouldBe CodePoint(0xB6)
    }

    @Test fun compare() = tests {
        CodePoint(0x61) shouldBeLessThan CodePoint(0xB6)
        CodePoint(0x2630) shouldBeGreaterThan CodePoint(0xB6)
        CodePoint(0xB6) shouldBeEqualComparingTo CodePoint(0xB6)
    }

    @Test fun string() = tests {
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

    @Test fun to_string() = tests {
        CodePoint(0x61).toString() shouldBe "a"
        CodePoint(0xB6).toString() shouldBe "¶"
        CodePoint(0x2630).toString() shouldBe "☰"
        CodePoint(0x1D553).toString() shouldBe "𝕓"
    }

    @Test fun char() = tests {
        CodePoint(0x61).char shouldBe 'a'
        CodePoint(0xB6).char shouldBe '¶'
        CodePoint(0x2630).char shouldBe '☰'
        CodePoint(0x1D553).char shouldBe null
    }

    @Test fun code_point() = tests {
        'a'.codePoint shouldBe CodePoint(0x61)
        '¶'.codePoint shouldBe CodePoint(0xB6)
        '☰'.codePoint shouldBe CodePoint(0x2630)
    }

    @Test fun as_code_point() = tests {
        0x61.toByte().asCodePoint() shouldBe CodePoint(0x61)
        0xB6.toByte().asCodePoint() shouldBe CodePoint(0xB6)
    }

    @Test fun is_0to9() = tests {
        "0123456789".asCodePointSequence().forEach { it.is0to9 shouldBe true }
        "AzΑωष".asCodePointSequence().forEach { it.is0to9 shouldBe false }
    }

    @Test fun is_AtoZ() = tests {
        @Suppress("SpellCheckingInspection")
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ".asCodePointSequence().forEach { it.isAtoZ shouldBe true }
        "abc123🜃🜂🜁🜄𝌀𝍖ि".asCodePointSequence().forEach { it.isAtoZ shouldBe false }
    }

    @Suppress("SpellCheckingInspection")
    @Test fun is_atoz() = tests {
        "abcdefghijklmnopqrstuvwxyz".asCodePointSequence().forEach { it.isatoz shouldBe true }
        "ABC123🜃🜂🜁🜄𝌀𝍖ि".asCodePointSequence().forEach { it.isatoz shouldBe false }
    }

    @Suppress("SpellCheckingInspection")
    @Test fun is_Atoz() = tests {
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".asCodePointSequence().forEach { it.isAtoz shouldBe true }
        "123🜃🜂🜁🜄𝌀𝍖ि".asCodePointSequence().forEach { it.isAtoz shouldBe false }
    }

    @Test fun is_ascii_alphanumeric() = tests {
        "Az09".asCodePointSequence().forEach { it.isAsciiAlphanumeric shouldBe true }
        "Αωष🜃🜂🜁🜄𝌀𝍖ि".asCodePointSequence().forEach { it.isAsciiAlphanumeric shouldBe false }
    }

    @Test fun is_alphanumeric() = tests {
        "Az09Αωष".asCodePointSequence().forEach { it.isAlphanumeric shouldBe true }
        "🜃🜂🜁🜄𝌀𝍖ि".asCodePointSequence().forEach { it.isAlphanumeric shouldBe false }
    }

    @Test fun is_letter() = tests {
        "AzΑωष".asCodePointSequence().forEach { it.isLetter shouldBe true }
        "🜃🜂🜁🜄𝌀𝍖ि09".asCodePointSequence().forEach { it.isLetter shouldBe false }
    }

    @Test fun is_digit() = tests {
        "0123456789".asCodePointSequence().forEach { it.isDigit shouldBe true }
        "AzΑωष".asCodePointSequence().forEach { it.isDigit shouldBe false }
    }

    @Test fun is_whitespace() = tests {
        listOf(' ', '\u2000').forEach { withClue(it.codePoint.index.toByte().toHexadecimalString()) { it.codePoint.isWhitespace shouldBe true } }
        "Az09Αω𝌀𝍖षि🜃🜂🜁🜄".asCodePointSequence().forEach { it.isWhitespace shouldBe false }
    }
}
