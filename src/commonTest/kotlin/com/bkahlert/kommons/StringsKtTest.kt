package com.bkahlert.kommons

import com.bkahlert.kommons.Platform.JS
import com.bkahlert.kommons.debug.ClassWithCustomToString
import com.bkahlert.kommons.debug.ClassWithDefaultToString
import com.bkahlert.kommons.debug.OrdinaryClass
import com.bkahlert.kommons.debug.ThrowingClass
import com.bkahlert.kommons.test.test
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.string.shouldStartWith
import io.kotest.matchers.throwable.shouldHaveMessage
import kotlin.test.Test

class StringsKtTest {

    @Test fun contains_any() = test {
        "foo bar".containsAny("baz", "o b", "abc") shouldBe true
        "foo bar".containsAny("baz", "O B", "abc", ignoreCase = true) shouldBe true
        "foo bar".containsAny("baz", "O B", "abc") shouldBe false
        "foo bar".containsAny("baz", "---", "abc") shouldBe false
    }

    @Test fun require_not_empty() = test {
        requireNotEmpty(charSequence) shouldBe charSequence
        requireNotEmpty(charSequence) { "error" } shouldBe charSequence
        requireNotEmpty(string) shouldBe string
        requireNotEmpty(string) { "error" } shouldBe string
        shouldThrow<IllegalArgumentException> { requireNotEmpty(emptyCharSequence) }
        shouldThrow<IllegalArgumentException> { requireNotEmpty(emptyCharSequence) { "error" } } shouldHaveMessage "error"
        shouldThrow<IllegalArgumentException> { requireNotEmpty(emptyString) }
        shouldThrow<IllegalArgumentException> { requireNotEmpty(emptyString) { "error" } } shouldHaveMessage "error"
        requireNotEmpty(blankCharSequence) shouldBe blankCharSequence
        requireNotEmpty(blankCharSequence) { "error" } shouldBe blankCharSequence
        requireNotEmpty(blankString) shouldBe blankString
        requireNotEmpty(blankString) { "error" } shouldBe blankString
    }

    @Test fun require_not_blank() = test {
        requireNotBlank(charSequence) shouldBe charSequence
        requireNotBlank(charSequence) { "error" } shouldBe charSequence
        requireNotBlank(string) shouldBe string
        requireNotBlank(string) { "error" } shouldBe string
        shouldThrow<IllegalArgumentException> { requireNotBlank(emptyCharSequence) }
        shouldThrow<IllegalArgumentException> { requireNotBlank(emptyCharSequence) { "error" } } shouldHaveMessage "error"
        shouldThrow<IllegalArgumentException> { requireNotBlank(emptyString) }
        shouldThrow<IllegalArgumentException> { requireNotBlank(emptyString) { "error" } } shouldHaveMessage "error"
        shouldThrow<IllegalArgumentException> { requireNotBlank(blankCharSequence) }
        shouldThrow<IllegalArgumentException> { requireNotBlank(blankCharSequence) { "error" } } shouldHaveMessage "error"
        shouldThrow<IllegalArgumentException> { requireNotBlank(blankString) }
        shouldThrow<IllegalArgumentException> { requireNotBlank(blankString) { "error" } } shouldHaveMessage "error"
    }

    @Test fun check_not_empty() = test {
        checkNotEmpty(charSequence) shouldBe charSequence
        checkNotEmpty(charSequence) { "error" } shouldBe charSequence
        checkNotEmpty(string) shouldBe string
        checkNotEmpty(string) { "error" } shouldBe string
        shouldThrow<IllegalStateException> { checkNotEmpty(emptyCharSequence) }
        shouldThrow<IllegalStateException> { checkNotEmpty(emptyCharSequence) { "error" } } shouldHaveMessage "error"
        shouldThrow<IllegalStateException> { checkNotEmpty(emptyString) }
        shouldThrow<IllegalStateException> { checkNotEmpty(emptyString) { "error" } } shouldHaveMessage "error"
        checkNotEmpty(blankCharSequence) shouldBe blankCharSequence
        checkNotEmpty(blankCharSequence) { "error" } shouldBe blankCharSequence
        checkNotEmpty(blankString) shouldBe blankString
        checkNotEmpty(blankString) { "error" } shouldBe blankString
    }

    @Test fun check_not_blank() = test {
        checkNotBlank(charSequence) shouldBe charSequence
        checkNotBlank(charSequence) { "error" } shouldBe charSequence
        checkNotBlank(string) shouldBe string
        checkNotBlank(string) { "error" } shouldBe string
        shouldThrow<IllegalStateException> { checkNotBlank(emptyCharSequence) }
        shouldThrow<IllegalStateException> { checkNotBlank(emptyCharSequence) { "error" } } shouldHaveMessage "error"
        shouldThrow<IllegalStateException> { checkNotBlank(emptyString) }
        shouldThrow<IllegalStateException> { checkNotBlank(emptyString) { "error" } } shouldHaveMessage "error"
        shouldThrow<IllegalStateException> { checkNotBlank(blankCharSequence) }
        shouldThrow<IllegalStateException> { checkNotBlank(blankCharSequence) { "error" } } shouldHaveMessage "error"
        shouldThrow<IllegalStateException> { checkNotBlank(blankString) }
        shouldThrow<IllegalStateException> { checkNotBlank(blankString) { "error" } } shouldHaveMessage "error"
    }

    @Test fun take_if_not_empty() = test {
        charSequence.takeIfNotEmpty() shouldBe charSequence
        string.takeIfNotEmpty() shouldBe string
        emptyCharSequence.takeIfNotEmpty() shouldBe null
        emptyString.takeIfNotEmpty() shouldBe null
        blankCharSequence.takeIfNotEmpty() shouldBe blankCharSequence
        blankString.takeIfNotEmpty() shouldBe blankString
    }

    @Test fun take_if_not_blank() = test {
        charSequence.takeIfNotBlank() shouldBe charSequence
        string.takeIfNotBlank() shouldBe string
        emptyCharSequence.takeIfNotBlank() shouldBe null
        emptyString.takeIfNotBlank() shouldBe null
        blankCharSequence.takeIfNotBlank() shouldBe null
        blankString.takeIfNotBlank() shouldBe null
    }

    @Test fun take_unless_empty() = test {
        charSequence.takeUnlessEmpty() shouldBe charSequence
        string.takeUnlessEmpty() shouldBe string
        emptyCharSequence.takeUnlessEmpty() shouldBe null
        emptyString.takeUnlessEmpty() shouldBe null
        blankCharSequence.takeUnlessEmpty() shouldBe blankCharSequence
        blankString.takeUnlessEmpty() shouldBe blankString
    }

    @Test fun take_unless_blank() = test {
        charSequence.takeUnlessBlank() shouldBe charSequence
        string.takeUnlessBlank() shouldBe string
        emptyCharSequence.takeUnlessBlank() shouldBe null
        emptyString.takeUnlessBlank() shouldBe null
        blankCharSequence.takeUnlessBlank() shouldBe null
        blankString.takeUnlessBlank() shouldBe null
    }


    @Test fun ansi_contained() = test {
        charSequence.ansiContained shouldBe false
        string.ansiContained shouldBe false
        emptyCharSequence.ansiContained shouldBe false
        emptyString.ansiContained shouldBe false
        blankCharSequence.ansiContained shouldBe false
        blankString.ansiContained shouldBe false
        ansiCsiCharSequence.ansiContained shouldBe true
        ansiCsiString.ansiContained shouldBe true
        ansiOscCharSequence.ansiContained shouldBe true
        ansiOscString.ansiContained shouldBe true
    }

    @Test fun ansi_removed() = test {
        charSequence.ansiRemoved shouldBe charSequence
        string.ansiRemoved shouldBe string
        emptyCharSequence.ansiRemoved shouldBe emptyCharSequence
        emptyString.ansiRemoved shouldBe emptyString
        blankCharSequence.ansiRemoved shouldBe blankCharSequence
        blankString.ansiRemoved shouldBe blankString
        ansiCsiCharSequence.ansiRemoved.toString() shouldBe "bold and blue"
        ansiCsiString.ansiRemoved shouldBe "bold and blue"
        ansiOscCharSequence.ansiRemoved.toString() shouldBe "↗ link"
        ansiOscString.ansiRemoved shouldBe "↗ link"
    }

    @Test fun spaced() = test {
        char.spaced shouldBe " $char "
        blankChar.spaced shouldBe " "
        char.startSpaced shouldBe " $char"
        blankChar.startSpaced shouldBe " "
        char.endSpaced shouldBe "$char "
        blankChar.endSpaced shouldBe " "

        charSequence.spaced shouldBe " $charSequence "
        charSequence.spaced.spaced shouldBe " $charSequence "
        charSequence.startSpaced shouldBe " $charSequence"
        charSequence.startSpaced.startSpaced shouldBe " $charSequence"
        charSequence.endSpaced shouldBe "$charSequence "
        charSequence.endSpaced.endSpaced shouldBe "$charSequence "

        string.spaced shouldBe " $string "
        string.spaced.spaced shouldBe " $string "
        string.startSpaced shouldBe " $string"
        string.startSpaced.startSpaced shouldBe " $string"
        string.endSpaced shouldBe "$string "
        string.endSpaced.endSpaced shouldBe "$string "
    }


    @Test fun truncate() = test {
        withClue("should truncate from center") {
            "12345678901234567890".truncate() shouldBe "123456 … 567890"
        }

        withClue("should truncate using code points") {
            "👨🏾👨🏾👨🏾👨🏾👨🏾".truncate(6) shouldBe "👨🏾 … 🏾"
        }

        withClue("should not truncate if not necessary") {
            "1234567890".truncate() shouldBe "1234567890"
        }

        withClue("should truncate using custom marker") {
            "12345678901234567890".truncate(marker = "...") shouldBe "123456...567890"
        }

        withClue("should truncate long text") {
            longString.truncate() shouldBe "123456 … 567890"
        }

        withClue("should throw if marker is wider than max length") {
            shouldThrow<IllegalArgumentException> {
                "1234567890".truncate(length = 1, marker = "XX")
            }
        }
    }

    @Test fun truncate_start() = test {

        withClue("should truncate from start") {
            "12345678901234567890".truncateStart() shouldBe " … 901234567890"
        }

        withClue("should truncate using code points") {
            "👨🏾👨🏾👨🏾👨🏾👨🏾".truncateStart(4) shouldBe " … 🏾"
        }

        withClue("should not truncate if not necessary") {
            "1234567890".truncateStart() shouldBe "1234567890"
        }

        withClue("should truncate using custom marker") {
            "12345678901234567890".truncateStart(marker = "...") shouldBe "...901234567890"
        }

        withClue("should truncate long text") {
            longString.truncateStart() shouldBe " … 901234567890"
        }

        withClue("should throw if marker is wider than max length") {
            shouldThrow<IllegalArgumentException> {
                "1234567890".truncateStart(length = 1, marker = "XX")
            }
        }
    }

    @Test fun truncate_end() = test {

        withClue("should truncate from end") {
            "12345678901234567890".truncateEnd() shouldBe "123456789012 … "
        }

        withClue("should truncate using columns") {
            "👨🏾👨🏾👨🏾👨🏾👨🏾".truncateEnd(4) shouldBe "👨 … "
        }

        withClue("should not truncate if not necessary") {
            "1234567890".truncateEnd() shouldBe "1234567890"
        }

        withClue("should truncate using custom marker") {
            "12345678901234567890".truncateEnd(marker = "...") shouldBe "123456789012..."
        }

        withClue("should truncate long text") {
            longString.truncateEnd() shouldBe "123456789012 … "
        }

        withClue("should throw if marker is wider than max length") {
            shouldThrow<IllegalArgumentException> {
                "1234567890".truncateEnd(length = 1, marker = "XX")
            }
        }
    }


    @Test fun consolidate_whitespaces_by() = test {
        withClue("should remove whitespaces from the right") {
            "a   b   c".consolidateWhitespacesBy(3) shouldBe "a  b c"
        }

        withClue("should use whitespaces on the right") {
            "a   b   c    ".consolidateWhitespacesBy(3) shouldBe "a   b   c "
        }

        withClue("should use single whitespace on the right") {
            "a   b   c ".consolidateWhitespacesBy(1) shouldBe "a   b   c"
        }

        withClue("should not merge words") {
            "a   b   c".consolidateWhitespacesBy(10) shouldBe "a b c"
        }

        withClue("should consider different whitespaces") {
            val differentWhitespaces = "\u0020\u00A0\u2000\u2003"
            "a ${differentWhitespaces}b".consolidateWhitespacesBy(differentWhitespaces.length) shouldBe "a b"
        }

        withClue("should leave area before startIndex unchanged") {
            "a   b   c".consolidateWhitespacesBy(10, startIndex = 5) shouldBe "a   b c"
        }

        withClue("should leave whitespace sequence below minimal length unchanged") {
            "a      b   c".consolidateWhitespacesBy(3, minWhitespaceLength = 3) shouldBe "a   b   c"
        }

        withClue("regression") {
            val x = "│   nested 1                                                                                            ▮▮"
            val y = "│   nested 1                                                                                      ▮▮"
            val z = "│   nested 1                                                                                         ▮▮"
            x.consolidateWhitespacesBy(3, minWhitespaceLength = 3) should {
                it shouldBe z
                it shouldNotBe y
            }
        }
    }

    @Test fun consolidate_whitespaces() = test {
        withClue("should remove whitespaces from the right") {
            "a   b   c".consolidateWhitespaces(6) shouldBe "a  b c"
        }

        withClue("should use whitespaces on the right") {
            "a   b   c    ".consolidateWhitespaces(10) shouldBe "a   b   c "
        }

        withClue("should use single whitespace on the right") {
            "a   b   c ".consolidateWhitespaces(9) shouldBe "a   b   c"
        }

        withClue("should not merge words") {
            "a   b   c".consolidateWhitespaces(0) shouldBe "a b c"
        }

        withClue("should consider different whitespaces") {
            val differentWhitespaces = "\u0020\u00A0\u2000\u2003"
            "a ${differentWhitespaces}b".consolidateWhitespaces(0) shouldBe "a b"
        }

        withClue("should leave area before startIndex unchanged") {
            "a   b   c".consolidateWhitespaces(0, startIndex = 5) shouldBe "a   b c"
        }

        withClue("should leave whitespace sequence below minimal length unchanged") {
            "a      b   c".consolidateWhitespaces(9, minWhitespaceLength = 3) shouldBe "a   b   c"
        }
    }


    @Test fun with_prefix() = test {
        char.withPrefix("c") shouldBe "c"
        char.withPrefix("b") shouldBe "bc"
        char.withPrefix("bc") shouldBe "bcc"
        charSequence.withPrefix(charSequence) shouldBe charSequence
        charSequence.withPrefix("char") shouldBe charSequence
        charSequence.withPrefix("char-") shouldBe "char-$charSequence"
        string.withPrefix(string) shouldBe string
        string.withPrefix("str") shouldBe string
        string.withPrefix("str-") shouldBe "str-$string"
    }

    @Test fun with_suffix() = test {
        char.withSuffix("c") shouldBe "c"
        char.withSuffix("d") shouldBe "cd"
        char.withSuffix("cd") shouldBe "ccd"
        charSequence.withSuffix(charSequence) shouldBe charSequence
        charSequence.withSuffix("sequence") shouldBe charSequence
        charSequence.withSuffix("-sequence") shouldBe "$charSequence-sequence"
        string.withSuffix(string) shouldBe string
        string.withSuffix("ing") shouldBe string
        string.withSuffix("-ing") shouldBe "$string-ing"
    }

    @Test fun with_random_suffix() = test {
        char.withRandomSuffix() should {
            it shouldMatch Regex("$char--[\\da-zA-Z]{4}")
            it shouldStartWith "$char"
            it.withRandomSuffix() shouldBe it
        }
        charSequence.withRandomSuffix() should {
            it shouldMatch Regex("$charSequence--[\\da-zA-Z]{4}")
            it shouldStartWith charSequence
            it.withRandomSuffix() shouldBe it
        }
        string.withRandomSuffix() should {
            it shouldMatch Regex("$string--[\\da-zA-Z]{4}")
            it shouldStartWith string
            it.withRandomSuffix() shouldBe it
        }
    }

    @Test fun random_string() = test {
        randomString() shouldHaveLength 16
        randomString(7) shouldHaveLength 7

        val allowedByDefault = (('0'..'9') + ('a'..'z') + ('A'..'Z')).toList()
        randomString(100).forAll { allowedByDefault shouldContain it }

        randomString(100, 'A', 'B').forAll { listOf('A', 'B') shouldContain it }
    }

    @Test fun index_of_or_null() = test {
        charSequence.indexOfOrNull('h') shouldBe 1
        string.indexOfOrNull('t') shouldBe 1

        charSequence.indexOfOrNull('x') shouldBe null
        string.indexOfOrNull('x') shouldBe null

        charSequence.indexOfOrNull("seq") shouldBe 5
        string.indexOfOrNull("ring") shouldBe 2

        charSequence.indexOfOrNull("xyz") shouldBe null
        string.indexOfOrNull("xyz") shouldBe null
    }

    @Test fun is_multiline() = test {
        "".isMultiline shouldBe false
        "foo".isMultiline shouldBe false
        LineSeparators.Common.forAll {
            it.isMultiline shouldBe true
            "${it}foo".isMultiline shouldBe true
            "foo${it}".isMultiline shouldBe true
            "foo${it}bar".isMultiline shouldBe true
            "foo${it}bar${it}baz".isMultiline shouldBe true
        }
        LineSeparators.Uncommon.forAll {
            it.isMultiline shouldBe false
            "${it}foo".isMultiline shouldBe false
            "foo${it}".isMultiline shouldBe false
            "foo${it}bar".isMultiline shouldBe false
            "foo${it}bar${it}baz".trimIndent().isMultiline shouldBe false
        }
    }

    @Test fun as_string() = test {
        OrdinaryClass().asString() shouldBe when (Platform.Current) {
            is JS -> """
                OrdinaryClass {
                    baseProperty: "base-property",
                    openBaseProperty: 0x2a,
                    protectedOpenBaseProperty: "protected-open-base-property",
                    privateBaseProperty: "private-base-property",
                    ordinaryProperty: "ordinary-property",
                    privateOrdinaryProperty: "private-ordinary-property"
                }
            """.trimIndent()
            else -> """
                OrdinaryClass {
                    protectedOpenBaseProperty: "protected-open-base-property",
                    openBaseProperty: 0x2a,
                    baseProperty: "base-property",
                    privateOrdinaryProperty: "private-ordinary-property",
                    ordinaryProperty: "ordinary-property"
                }
            """.trimIndent()
        }
        if (Platform.Current !is JS) {
            ThrowingClass().asString() shouldBe """
            ThrowingClass {
                throwingProperty: <error:java.lang.RuntimeException: error reading property>,
                privateThrowingProperty: <error:java.lang.RuntimeException: error reading private property>
            }
        """.trimIndent()
        }

        OrdinaryClass().asString(OrdinaryClass::ordinaryProperty) shouldBe """
            OrdinaryClass { ordinaryProperty: "ordinary-property" }
        """.trimIndent()

        OrdinaryClass().asString(exclude = listOf(OrdinaryClass::ordinaryProperty)) shouldBe when (Platform.Current) {
            is JS -> """
                OrdinaryClass {
                    baseProperty: "base-property",
                    openBaseProperty: 0x2a,
                    protectedOpenBaseProperty: "protected-open-base-property",
                    privateBaseProperty: "private-base-property",
                    privateOrdinaryProperty: "private-ordinary-property"
                }
            """.trimIndent()
            else -> """
                OrdinaryClass {
                    protectedOpenBaseProperty: "protected-open-base-property",
                    openBaseProperty: 0x2a,
                    baseProperty: "base-property",
                    privateOrdinaryProperty: "private-ordinary-property"
                }
            """.trimIndent()
        }

        ClassWithDefaultToString().asString() shouldBe """ClassWithDefaultToString { bar: "baz" }"""
        ClassWithDefaultToString().asString(excludeNullValues = true) shouldBe """ClassWithDefaultToString { bar: "baz" }"""
        ClassWithDefaultToString().asString(excludeNullValues = false) shouldBe """ClassWithDefaultToString { foo: null, bar: "baz" }"""

        ClassWithDefaultToString().let {
            it.asString {
                put(it::bar, "baz")
                put("baz", ClassWithCustomToString())
            }
        } shouldBe """ClassWithDefaultToString { bar: "baz", baz: custom toString }"""
    }
}

internal val char: Char = 'c'
internal val blankChar: Char = ' '

internal val charSequence: CharSequence = StringBuilder("char sequence")
internal val emptyCharSequence: CharSequence = StringBuilder()
internal val blankCharSequence: CharSequence = StringBuilder("   ")

internal const val string: String = "string"
internal const val emptyString: String = ""
internal const val blankString: String = "   "

internal val longString = "1234567890".repeat(1000)

/** [String] containing CSI (`control sequence intro`) escape sequences */
internal const val ansiCsiString: String = "[1mbold [34mand blue[0m"

/** [CharSequence] containing CSI (`control sequence intro`) escape sequences */
internal val ansiCsiCharSequence: CharSequence = StringBuilder().append(ansiCsiString)

/** [String] containing CSI (`control sequence intro`) escape sequences */
internal const val ansiOscString: String = "[34m↗(B[m ]8;;https://example.com\\link]8;;\\"

/** [CharSequence] containing CSI (`control sequence intro`) escape sequences */
internal val ansiOscCharSequence: CharSequence = StringBuilder().append(ansiOscString)
