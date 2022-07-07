package com.bkahlert.kommons

import com.bkahlert.kommons.Parser.Companion.ParserException
import com.bkahlert.kommons.test.test
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.inspectors.forAll
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class InitKtTest {

    @Test fun parser_exception() = test {
        listOf(
            ParserException("value".cs, Int::class),
            ParserException("value", Int::class),
        ).forAll {
            it.message shouldBe "Failed to parse \"value\" into an instance of Int"
            it.cause shouldBe null
        }

        listOf(
            ParserException("value".cs, Int::class, RuntimeException()),
            ParserException("value", Int::class, RuntimeException()),
        ).forAll {
            it.message shouldBe "Failed to parse \"value\" into an instance of Int"
            it.cause.shouldBeInstanceOf<RuntimeException>()
        }

        listOf(
            ParserException("custom message"),
        ).forAll {
            it.message shouldBe "custom message"
            it.cause shouldBe null
        }

        listOf(
            ParserException("custom message", RuntimeException()),
        ).forAll {
            it.message shouldBe "custom message"
            it.cause.shouldBeInstanceOf<RuntimeException>()
        }
    }

    @Test fun parse_or_null() = test {
        intParser.parseOrNull("1".cs) shouldBe 1
        intParser.parseOrNull("-1-".cs) shouldBe null
        intParser.parseOrNull("1") shouldBe 1
        intParser.parseOrNull("-1-") shouldBe null

        throwingParser.parseOrNull("1".cs) shouldBe 1
        throwingParser.parseOrNull("-1-".cs) shouldBe null
        throwingParser.parseOrNull("1") shouldBe 1
        throwingParser.parseOrNull("-1-") shouldBe null
    }

    @Test fun parse() = test {
        intParser.parse("1".cs) shouldBe 1
        shouldThrow<NumberFormatException> { intParser.parse("-1-".cs) }
        intParser.parse("1") shouldBe 1
        shouldThrow<NumberFormatException> { intParser.parse("-1-") }

        throwingParser.parse("1".cs) shouldBe 1
        shouldThrow<ParserException> { throwingParser.parse("-1-".cs) }.cause shouldBe null
        throwingParser.parse("1") shouldBe 1
        shouldThrow<ParserException> { throwingParser.parse("-1-") }.cause shouldBe null
    }

    @Test fun parser() = test {
        Parser.parser(intParser::parse) should { parser ->
            parser.parseOrNull("1".cs) shouldBe 1
            parser.parseOrNull("-1-".cs)
            parser.parseOrNull("1") shouldBe 1
            parser.parseOrNull("-1-")

            parser.parse("1".cs) shouldBe 1
            shouldThrow<ParserException> { parser.parse("-1-".cs) }.cause.shouldBeInstanceOf<NumberFormatException>()
            parser.parse("1") shouldBe 1
            shouldThrow<ParserException> { parser.parse("-1-") }.cause.shouldBeInstanceOf<NumberFormatException>()
        }

        Parser.parser(throwingParser::parse) should { parser ->
            parser.parseOrNull("1".cs) shouldBe 1
            parser.parseOrNull("-1-".cs)
            parser.parseOrNull("1") shouldBe 1
            parser.parseOrNull("-1-")

            parser.parse("1".cs) shouldBe 1
            shouldThrow<ParserException> { parser.parse("-1-".cs) }.cause shouldBe null
            parser.parse("1") shouldBe 1
            shouldThrow<ParserException> { parser.parse("-1-") }.cause shouldBe null
        }

        Parser.parser { it.toString().toIntOrNull() } should { parser ->
            parser.parseOrNull("1".cs) shouldBe 1
            parser.parseOrNull("-1-".cs)
            parser.parseOrNull("1") shouldBe 1
            parser.parseOrNull("-1-")

            parser.parse("1".cs) shouldBe 1
            shouldThrow<ParserException> { parser.parse("-1-".cs) }.cause shouldBe null
            parser.parse("1") shouldBe 1
            shouldThrow<ParserException> { parser.parse("-1-") }.cause shouldBe null
        }
    }
}

internal val intParser = object : Parser<Int> {
    override fun parse(string: CharSequence): Int = string.toString().toInt()
}

internal val throwingParser = object : Parser<Int> {
    override fun parse(string: CharSequence): Int = string.toString().toIntOrNull() ?: throw ParserException("error parsing $string")
}
