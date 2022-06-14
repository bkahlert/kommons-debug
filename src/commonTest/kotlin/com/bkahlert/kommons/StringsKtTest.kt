package com.bkahlert.kommons

import com.bkahlert.kommons.Platform.JS
import com.bkahlert.kommons.debug.ClassWithCustomToString
import com.bkahlert.kommons.debug.ClassWithDefaultToString
import com.bkahlert.kommons.debug.OrdinaryClass
import com.bkahlert.kommons.debug.ThrowingClass
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

class StringsKtTest {

    @Test fun contains_any() = tests {
        "foo bar".containsAny("baz", "o b", "abc") shouldBe true
        "foo bar".containsAny("baz", "O B", "abc", ignoreCase = true) shouldBe true
        "foo bar".containsAny("baz", "O B", "abc") shouldBe false
        "foo bar".containsAny("baz", "---", "abc") shouldBe false
    }

    @Test fun require_not_empty() = tests {
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

    @Test fun require_not_blank() = tests {
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

    @Test fun check_not_empty() = tests {
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

    @Test fun check_not_blank() = tests {
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

    @Test fun as_string() = tests {
        OrdinaryClass().asString() shouldBe when (Platform.Current) {
            JS -> """
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
        if (Platform.Current != JS) {
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
            JS -> """
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

internal val charSequence: CharSequence = StringBuilder("char sequence")
internal val emptyCharSequence: CharSequence = StringBuilder()
internal val blankCharSequence: CharSequence = StringBuilder("   ")

internal const val string: String = "string"
internal const val emptyString: String = ""
internal const val blankString: String = "   "


/** [String] containing CSI (`control sequence intro`) escape sequences */
internal const val ansiCsiString: String = "[1mbold [34mand blue[0m"

/** [CharSequence] containing CSI (`control sequence intro`) escape sequences */
internal val ansiCsiCharSequence: CharSequence = StringBuilder().append(ansiCsiString)

/** [String] containing CSI (`control sequence intro`) escape sequences */
internal const val ansiOscString: String = "[34m↗(B[m ]8;;https://example.com\\link]8;;\\"

/** [CharSequence] containing CSI (`control sequence intro`) escape sequences */
internal val ansiOscCharSequence: CharSequence = StringBuilder().append(ansiOscString)
