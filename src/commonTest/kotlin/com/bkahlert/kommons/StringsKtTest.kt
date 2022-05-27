package com.bkahlert.kommons

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.string.shouldStartWith
import io.kotest.matchers.throwable.shouldHaveMessage
import kotlin.test.Test

class StringsTest {

    @Test fun contains_any() = tests {
        "foo bar".containsAny("baz", "o b", "abc") shouldBe true
        "foo bar".containsAny("baz", "O B", "abc", ignoreCase = true) shouldBe true
        "foo bar".containsAny("baz", "O B", "abc") shouldBe false
        "foo bar".containsAny("baz", "---", "abc") shouldBe false
    }

    @Test fun require_not_empty() = tests {
        charSequence.requireNotEmpty() shouldBe charSequence
        charSequence.requireNotEmpty { "error" } shouldBe charSequence
        string.requireNotEmpty() shouldBe string
        string.requireNotEmpty { "error" } shouldBe string
        shouldThrow<IllegalArgumentException> { emptyCharSequence.requireNotEmpty() }
        shouldThrow<IllegalArgumentException> { emptyCharSequence.requireNotEmpty { "error" } } shouldHaveMessage "error"
        shouldThrow<IllegalArgumentException> { emptyString.requireNotEmpty() }
        shouldThrow<IllegalArgumentException> { emptyString.requireNotEmpty { "error" } } shouldHaveMessage "error"
        blankCharSequence.requireNotEmpty() shouldBe blankCharSequence
        blankCharSequence.requireNotEmpty { "error" } shouldBe blankCharSequence
        blankString.requireNotEmpty() shouldBe blankString
        blankString.requireNotEmpty { "error" } shouldBe blankString
    }

    @Test fun require_not_blank() = tests {
        charSequence.requireNotBlank() shouldBe charSequence
        charSequence.requireNotBlank { "error" } shouldBe charSequence
        string.requireNotBlank() shouldBe string
        string.requireNotBlank { "error" } shouldBe string
        shouldThrow<IllegalArgumentException> { emptyCharSequence.requireNotBlank() }
        shouldThrow<IllegalArgumentException> { emptyCharSequence.requireNotBlank { "error" } } shouldHaveMessage "error"
        shouldThrow<IllegalArgumentException> { emptyString.requireNotBlank() }
        shouldThrow<IllegalArgumentException> { emptyString.requireNotBlank { "error" } } shouldHaveMessage "error"
        shouldThrow<IllegalArgumentException> { blankCharSequence.requireNotBlank() }
        shouldThrow<IllegalArgumentException> { blankCharSequence.requireNotBlank { "error" } } shouldHaveMessage "error"
        shouldThrow<IllegalArgumentException> { blankString.requireNotBlank() }
        shouldThrow<IllegalArgumentException> { blankString.requireNotBlank { "error" } } shouldHaveMessage "error"
    }

    @Test fun check_not_empty() = tests {
        charSequence.checkNotEmpty() shouldBe charSequence
        charSequence.checkNotEmpty { "error" } shouldBe charSequence
        string.checkNotEmpty() shouldBe string
        string.checkNotEmpty { "error" } shouldBe string
        shouldThrow<IllegalStateException> { emptyCharSequence.checkNotEmpty() }
        shouldThrow<IllegalStateException> { emptyCharSequence.checkNotEmpty { "error" } } shouldHaveMessage "error"
        shouldThrow<IllegalStateException> { emptyString.checkNotEmpty() }
        shouldThrow<IllegalStateException> { emptyString.checkNotEmpty { "error" } } shouldHaveMessage "error"
        blankCharSequence.checkNotEmpty() shouldBe blankCharSequence
        blankCharSequence.checkNotEmpty { "error" } shouldBe blankCharSequence
        blankString.checkNotEmpty() shouldBe blankString
        blankString.checkNotEmpty { "error" } shouldBe blankString
    }

    @Test fun check_not_blank() = tests {
        charSequence.checkNotBlank() shouldBe charSequence
        charSequence.checkNotBlank { "error" } shouldBe charSequence
        string.checkNotBlank() shouldBe string
        string.checkNotBlank { "error" } shouldBe string
        shouldThrow<IllegalStateException> { emptyCharSequence.checkNotBlank() }
        shouldThrow<IllegalStateException> { emptyCharSequence.checkNotBlank { "error" } } shouldHaveMessage "error"
        shouldThrow<IllegalStateException> { emptyString.checkNotBlank() }
        shouldThrow<IllegalStateException> { emptyString.checkNotBlank { "error" } } shouldHaveMessage "error"
        shouldThrow<IllegalStateException> { blankCharSequence.checkNotBlank() }
        shouldThrow<IllegalStateException> { blankCharSequence.checkNotBlank { "error" } } shouldHaveMessage "error"
        shouldThrow<IllegalStateException> { blankString.checkNotBlank() }
        shouldThrow<IllegalStateException> { blankString.checkNotBlank { "error" } } shouldHaveMessage "error"
    }

    @Test fun take_if_not_empty() = tests {
        charSequence.takeIfNotEmpty() shouldBe charSequence
        string.takeIfNotEmpty() shouldBe string
        emptyCharSequence.takeIfNotEmpty() shouldBe null
        emptyString.takeIfNotEmpty() shouldBe null
        blankCharSequence.takeIfNotEmpty() shouldBe blankCharSequence
        blankString.takeIfNotEmpty() shouldBe blankString
    }

    @Test fun take_if_not_blank() = tests {
        charSequence.takeIfNotBlank() shouldBe charSequence
        string.takeIfNotBlank() shouldBe string
        emptyCharSequence.takeIfNotBlank() shouldBe null
        emptyString.takeIfNotBlank() shouldBe null
        blankCharSequence.takeIfNotBlank() shouldBe null
        blankString.takeIfNotBlank() shouldBe null
    }

    @Test fun take_unless_empty() = tests {
        charSequence.takeUnlessEmpty() shouldBe charSequence
        string.takeUnlessEmpty() shouldBe string
        emptyCharSequence.takeUnlessEmpty() shouldBe null
        emptyString.takeUnlessEmpty() shouldBe null
        blankCharSequence.takeUnlessEmpty() shouldBe blankCharSequence
        blankString.takeUnlessEmpty() shouldBe blankString
    }

    @Test fun take_unless_blank() = tests {
        charSequence.takeUnlessBlank() shouldBe charSequence
        string.takeUnlessBlank() shouldBe string
        emptyCharSequence.takeUnlessBlank() shouldBe null
        emptyString.takeUnlessBlank() shouldBe null
        blankCharSequence.takeUnlessBlank() shouldBe null
        blankString.takeUnlessBlank() shouldBe null
    }


    @Test fun ansi_contained() = tests {
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

    @Test fun ansi_removed() = tests {
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

    @Test fun with_prefix() = tests {
        charSequence.withPrefix(charSequence) shouldBe charSequence
        charSequence.withPrefix("char") shouldBe charSequence
        charSequence.withPrefix("char-") shouldBe "char-$charSequence"
        string.withPrefix(string) shouldBe string
        string.withPrefix("str") shouldBe string
        string.withPrefix("str-") shouldBe "str-$string"
    }

    @Test fun with_suffix() = tests {
        charSequence.withSuffix(charSequence) shouldBe charSequence
        charSequence.withSuffix("sequence") shouldBe charSequence
        charSequence.withSuffix("-sequence") shouldBe "$charSequence-sequence"
        string.withSuffix(string) shouldBe string
        string.withSuffix("ing") shouldBe string
        string.withSuffix("-ing") shouldBe "$string-ing"
    }

    @Test fun with_random_suffix() = tests {
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

    @Test fun random_string() = tests {
        randomString() shouldHaveLength 16
        randomString(7) shouldHaveLength 7

        val allowedByDefault = (('0'..'9') + ('a'..'z') + ('A'..'Z')).toList()
        randomString(100).forAll { allowedByDefault shouldContain it }

        randomString(100, 'A', 'B').forAll { listOf('A', 'B') shouldContain it }
    }
}

internal val charSequence: CharSequence = StringBuilder().append("char sequence")
internal val emptyCharSequence: CharSequence = StringBuilder()
internal val blankCharSequence: CharSequence = StringBuilder().append("   ")

internal val string: String = "string"
internal val emptyString: String = ""
internal val blankString: String = "   "


/** [String] containing CSI (`control sequence intro`) escape sequences */
internal val ansiCsiString: String = "[1mbold [34mand blue[0m"

/** [CharSequence] containing CSI (`control sequence intro`) escape sequences */
internal val ansiCsiCharSequence: CharSequence = StringBuilder().append(ansiCsiString)

/** [String] containing CSI (`control sequence intro`) escape sequences */
internal val ansiOscString: String = "[34m↗(B[m ]8;;https://example.com\\link]8;;\\"

/** [CharSequence] containing CSI (`control sequence intro`) escape sequences */
internal val ansiOscCharSequence: CharSequence = StringBuilder().append(ansiOscString)
