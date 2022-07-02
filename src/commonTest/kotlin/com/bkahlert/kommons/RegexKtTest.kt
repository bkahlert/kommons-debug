package com.bkahlert.kommons

import com.bkahlert.kommons.LineSeparators.CR
import com.bkahlert.kommons.LineSeparators.CRLF
import com.bkahlert.kommons.LineSeparators.LF
import com.bkahlert.kommons.LineSeparators.LS
import com.bkahlert.kommons.LineSeparators.NEL
import com.bkahlert.kommons.LineSeparators.PS
import com.bkahlert.kommons.Platform.JS
import com.bkahlert.kommons.test.fixtures.GifImageFixture
import com.bkahlert.kommons.test.test
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.sequences.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlin.test.Test

@Suppress(
    "RegExpRepeatedSpace",
    "RegExpSingleCharAlternation",
    "RegExpOctalEscape",
    "RegExpDuplicateAlternationBranch",
    "RegExpEscapedMetaCharacter",
    "RegExpEmptyAlternationBranch",
    "RegExpAnonymousGroup",
    "RegExpUnexpectedAnchor",
    "RegExpDuplicateCharacterInClass",
    "RegExpRedundantNestedCharacterClass",
    "RegExpUnnecessaryNonCapturingGroup",
    "RegExpSuspiciousBackref",
    "RegExpSimplifiable",
    "RegExpRedundantClassElement",
)
class RegexKtTest {

    @Test fun is_group() = test {
        Regex("").isGroup shouldBe false
        Regex(" ").isGroup shouldBe false
        Regex("   ").isGroup shouldBe false

        Regex("^[0-9]*foo()$").isGroup shouldBe false
        Regex("()foo").isGroup shouldBe false

        Regex("()()").isGroup shouldBe false
        Regex("(foo)(bar)").isGroup shouldBe false
        Regex("(())()").isGroup shouldBe false
        Regex("(foo(bar)baz)(bar)").isGroup shouldBe false

        Regex("()").isGroup shouldBe true
        Regex("(foo)").isGroup shouldBe true
        Regex("(())").isGroup shouldBe true
        Regex("(foo())").isGroup shouldBe true
        Regex("(()foo)").isGroup shouldBe true
        Regex("(?:)").isGroup shouldBe true
        Regex("(?:foo)").isGroup shouldBe true
        Regex("(?:())").isGroup shouldBe true
        Regex("(?:foo())").isGroup shouldBe true
        Regex("(?:()foo)").isGroup shouldBe true
        Regex("(?<name>)").isGroup shouldBe true
        Regex("(?<name>foo)").isGroup shouldBe true
        Regex("(?<name>())").isGroup shouldBe true
        Regex("(?<name>foo())").isGroup shouldBe true
        Regex("(?<name>()foo)").isGroup shouldBe true

        Regex("(\\()").isGroup shouldBe true
        Regex("(\\(foo)").isGroup shouldBe true
        Regex("(\\)\\(\\))").isGroup shouldBe true
        Regex("()\\)").isGroup shouldBe false
        Regex("\\((())").isGroup shouldBe false
    }

    @Test fun is_named_group() = test {
        Regex("()").isNamedGroup shouldBe false
        Regex("(foo)").isNamedGroup shouldBe false
        Regex("(())").isNamedGroup shouldBe false
        Regex("(foo())").isNamedGroup shouldBe false
        Regex("(()foo)").isNamedGroup shouldBe false
        Regex("(?:)").isNamedGroup shouldBe false
        Regex("(?:foo)").isNamedGroup shouldBe false
        Regex("(?:())").isNamedGroup shouldBe false
        Regex("(?:foo())").isNamedGroup shouldBe false
        Regex("(?:()foo)").isNamedGroup shouldBe false
        Regex("(?<name>)").isNamedGroup shouldBe true
        Regex("(?<name>foo)").isNamedGroup shouldBe true
        Regex("(?<name>())").isNamedGroup shouldBe true
        Regex("(?<name>foo())").isNamedGroup shouldBe true
        Regex("(?<name>()foo)").isNamedGroup shouldBe true
    }

    @Test fun is_anonymous_group() = test {
        Regex("()").isAnonymousGroup shouldBe false
        Regex("(foo)").isAnonymousGroup shouldBe false
        Regex("(())").isAnonymousGroup shouldBe false
        Regex("(foo())").isAnonymousGroup shouldBe false
        Regex("(()foo)").isAnonymousGroup shouldBe false
        Regex("(?:)").isAnonymousGroup shouldBe true
        Regex("(?:foo)").isAnonymousGroup shouldBe true
        Regex("(?:())").isAnonymousGroup shouldBe true
        Regex("(?:foo())").isAnonymousGroup shouldBe true
        Regex("(?:()foo)").isAnonymousGroup shouldBe true
        Regex("(?<name>)").isAnonymousGroup shouldBe false
        Regex("(?<name>foo)").isAnonymousGroup shouldBe false
        Regex("(?<name>())").isAnonymousGroup shouldBe false
        Regex("(?<name>foo())").isAnonymousGroup shouldBe false
        Regex("(?<name>()foo)").isAnonymousGroup shouldBe false
    }

    @Test fun is_indexed_group() = test {
        Regex("()").isIndexedGroup shouldBe true
        Regex("(foo)").isIndexedGroup shouldBe true
        Regex("(())").isIndexedGroup shouldBe true
        Regex("(foo())").isIndexedGroup shouldBe true
        Regex("(()foo)").isIndexedGroup shouldBe true
        Regex("(?:)").isIndexedGroup shouldBe false
        Regex("(?:foo)").isIndexedGroup shouldBe false
        Regex("(?:())").isIndexedGroup shouldBe false
        Regex("(?:foo())").isIndexedGroup shouldBe false
        Regex("(?:()foo)").isIndexedGroup shouldBe false
        Regex("(?<name>)").isIndexedGroup shouldBe false
        Regex("(?<name>foo)").isIndexedGroup shouldBe false
        Regex("(?<name>())").isIndexedGroup shouldBe false
        Regex("(?<name>foo())").isIndexedGroup shouldBe false
        Regex("(?<name>()foo)").isIndexedGroup shouldBe false
    }

    @Test fun group_contents() = test {
        Regex("").groupContents shouldBe Regex("")
        Regex(" ").groupContents shouldBe Regex(" ")
        Regex("   ").groupContents shouldBe Regex("   ")

        Regex("^[0-9]*foo()$").groupContents shouldBe Regex("^[0-9]*foo()$")
        Regex("()foo").groupContents shouldBe Regex("()foo")

        Regex("()()").groupContents shouldBe Regex("()()")
        Regex("(foo)(bar)").groupContents shouldBe Regex("(foo)(bar)")
        Regex("(())()").groupContents shouldBe Regex("(())()")
        Regex("(foo(bar)baz)(bar)").groupContents shouldBe Regex("(foo(bar)baz)(bar)")

        Regex("()").groupContents shouldBe Regex("")
        Regex("(foo)").groupContents shouldBe Regex("foo")
        Regex("(())").groupContents shouldBe Regex("()")
        Regex("(foo())").groupContents shouldBe Regex("foo()")
        Regex("(()foo)").groupContents shouldBe Regex("()foo")
        Regex("(?:)").groupContents shouldBe Regex("")
        Regex("(?:foo)").groupContents shouldBe Regex("foo")
        Regex("(?:())").groupContents shouldBe Regex("()")
        Regex("(?:foo())").groupContents shouldBe Regex("foo()")
        Regex("(?:()foo)").groupContents shouldBe Regex("()foo")
        Regex("(?<name>)").groupContents shouldBe Regex("")
        Regex("(?<name>foo)").groupContents shouldBe Regex("foo")
        Regex("(?<name>())").groupContents shouldBe Regex("()")
        Regex("(?<name>foo())").groupContents shouldBe Regex("foo()")
        Regex("(?<name>()foo)").groupContents shouldBe Regex("()foo")

        Regex("(\\()").groupContents shouldBe Regex("\\(")
        Regex("(\\(foo)").groupContents shouldBe Regex("\\(foo")
        Regex("(\\)\\(\\))").groupContents shouldBe Regex("\\)\\(\\)")
        Regex("()\\)").groupContents shouldBe Regex("()\\)")
        Regex("\\((())").groupContents shouldBe Regex("\\((())")
    }


    @Test fun plus() = test {
        Regex("foo") + Regex("bar") shouldBe Regex("foobar")
        Regex("foo") + "bar" shouldBe Regex("foobar")
    }

    @Test fun or() = test {
        Regex("foo") or Regex("bar") shouldBe Regex("foo|bar")
        Regex("foo") or "bar" shouldBe Regex("foo|bar")
    }

    @Test fun group() = test {
        Regex("foo").group("other") shouldBe Regex("(?<other>foo)")
        Regex("(foo)").group("other") shouldBe Regex("(?<other>foo)")
        Regex("(?:foo)").group("other") shouldBe Regex("(?<other>foo)")
        Regex("(?<name>foo)").group("other") shouldBe Regex("(?<other>(?<name>foo))")
        shouldThrow<IllegalArgumentException> { Regex("(?<name>foo)").group("in-valid") }

        Regex("foo").group() shouldBe Regex("(?:foo)")
        Regex("(foo)").group() shouldBe Regex("(foo)")
        Regex("(?:foo)").group() shouldBe Regex("(?:foo)")
        Regex("(?<name>foo)").group() shouldBe Regex("(?<name>foo)")
    }

    @Test fun grouped() = test {
        Regex("foo").group() shouldBe Regex("(?:foo)")
        Regex("(foo)").group() shouldBe Regex("(foo)")
        Regex("(?:foo)").group() shouldBe Regex("(?:foo)")
        Regex("(?<name>foo)").group() shouldBe Regex("(?<name>foo)")
    }

    @Test fun optional() = test {
        Regex("foo").optional() shouldBe Regex("(?:foo)?")
        Regex("(foo)").optional() shouldBe Regex("(foo)?")
        Regex("(?:foo)").optional() shouldBe Regex("(?:foo)?")
        Regex("(?<name>foo)").optional() shouldBe Regex("(?<name>foo)?")
    }

    @Test fun repeat_any() = test {
        Regex("foo").repeatAny() shouldBe Regex("(?:foo)*")
        Regex("(foo)").repeatAny() shouldBe Regex("(foo)*")
        Regex("(?:foo)").repeatAny() shouldBe Regex("(?:foo)*")
        Regex("(?<name>foo)").repeatAny() shouldBe Regex("(?<name>foo)*")
    }

    @Test fun repeat_at_least_once() = test {
        Regex("foo").repeatAtLeastOnce() shouldBe Regex("(?:foo)+")
        Regex("(foo)").repeatAtLeastOnce() shouldBe Regex("(foo)+")
        Regex("(?:foo)").repeatAtLeastOnce() shouldBe Regex("(?:foo)+")
        Regex("(?<name>foo)").repeatAtLeastOnce() shouldBe Regex("(?<name>foo)+")
    }


    @Test fun repeat() = test {
        Regex("foo").repeat(2, 5) shouldBe Regex("(?:foo){2,5}")
        Regex("(foo)").repeat(2, 5) shouldBe Regex("(foo){2,5}")
        Regex("(?:foo)").repeat(2, 5) shouldBe Regex("(?:foo){2,5}")
        Regex("(?<name>foo)").repeat(2, 5) shouldBe Regex("(?<name>foo){2,5}")
    }


    @Test fun named_groups() = test {
        val string = "foo bar baz"

        // built-in
        Regex("(ba.)").findAll(string).mapNotNull { it.groups[1] }.map { it.value }.shouldContainExactly("bar", "baz")
        Regex("(?<name>ba.)").findAll(string).mapNotNull { it.groups[1] }.map { it.value }.shouldContainExactly("bar", "baz")

        // extensions
        Regex("(ba.)").findAll(string).mapNotNull { it.groupValue(1) }.shouldContainExactly("bar", "baz")
        Regex("(?<name>ba.)").findAll(string).mapNotNull { it.groupValue(1) }.shouldContainExactly("bar", "baz")

        // name extensions
        shouldThrow<IllegalArgumentException> { Regex("(ba.)").findAll(string).mapNotNull { it.groups["name"] }.toList() }
        Regex("(?<name>ba.)").findAll(string).mapNotNull { it.groups["name"] }.map { it.value }.shouldContainExactly("bar", "baz")
        shouldThrow<IllegalArgumentException> { Regex("(ba.)").findAll(string).mapNotNull { it.groupValue("name") }.toList() }
        Regex("(?<name>ba.)").findAll(string).mapNotNull { it.groupValue("name") }.shouldContainExactly("bar", "baz")
    }


    @Test fun find_all_values() = test {
        val string = "foo bar baz"

        Regex("(ba.)").findAllValues(string).shouldContainExactly("bar", "baz")
        Regex("(?<name>ba.)").findAllValues(string).shouldContainExactly("bar", "baz")

        Regex("(ba.)").findAllValues(string, 6).shouldContainExactly("baz")
        Regex("(?<name>ba.)").findAllValues(string, 6).shouldContainExactly("baz")
    }


    @Test fun any_character_regex() = test {
        stringWithAllLineSeparators.replace(Regex.AnyCharacterRegex, "-") shouldBe "----------------------------"
        stringWithAllLineSeparators.replace(Regex("."), "-") shouldBe when (Platform.Current) {
            is JS -> "---${CRLF}---${LF}---${CR}-------${PS}---${LS}---"
            else -> "---${CRLF}---${LF}---${CR}---${NEL}---${PS}---${LS}---"
        }
    }

    @Test fun url_regex() = test {
        Regex.UrlRegex.findAllValues(
            """
               http://example.net
               https://xn--yp9haa.io/beep/beep?signal=on&timeout=42_000#some-complex-state
               ftp://edu.gov/download/latest-shit
               file:///some/triple-slash/uri/path/to/file.sh
               mailto:someone@somewhere
               abc://example.net
               ${GifImageFixture.dataURI}
               crap
           """.trimIndent()
        ).toList().shouldContainExactly(
            "http://example.net",
            "https://xn--yp9haa.io/beep/beep?signal=on&timeout=42_000#some-complex-state",
            "ftp://edu.gov/download/latest-shit",
            "file:///some/triple-slash/uri/path/to/file.sh",
        )
    }


    @Test fun uri_regex() = test {
        Regex.UriRegex.findAllValues(
            """
               http://example.net
               https://xn--yp9haa.io/beep/beep?signal=on&timeout=42_000#some-complex-state
               ftp://edu.gov/download/latest-shit
               file:///some/triple-slash/uri/path/to/file.sh
               mailto:someone@somewhere
               abc://example.net
               ${GifImageFixture.dataURI}
               crap
           """.trimIndent()
        ).toList().shouldContainExactly(
            "http://example.net",
            "https://xn--yp9haa.io/beep/beep?signal=on&timeout=42_000#some-complex-state",
            "ftp://edu.gov/download/latest-shit",
            "file:///some/triple-slash/uri/path/to/file.sh",
            "mailto:someone@somewhere",
            "abc://example.net",
            GifImageFixture.dataURI,
        )
    }

    @Test fun common_line_separators_regex() = test {
        stringWithAllLineSeparators.replace(Regex.CommonLineSeparatorsRegex, "-") shouldBe "foo-foo-foo-foo${NEL}foo${PS}foo${LS}foo"
    }

    @Test fun uncommon_line_separators_regex() = test {
        stringWithAllLineSeparators.replace(Regex.UncommonLineSeparatorsRegex, "-") shouldBe "foo${CRLF}foo${LF}foo${CR}foo-foo-foo-foo"
    }

    @Test fun unicode_line_separators_regex() = test {
        stringWithAllLineSeparators.replace(Regex.UnicodeLineSeparatorsRegex, "-") shouldBe "foo-foo-foo-foo-foo-foo-foo"
    }
}
