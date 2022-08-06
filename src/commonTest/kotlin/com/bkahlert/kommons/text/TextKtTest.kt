package com.bkahlert.kommons.text

import com.bkahlert.kommons.debug.render
import com.bkahlert.kommons.mapToRanges
import com.bkahlert.kommons.test.testAll
import com.bkahlert.kommons.text.Text.ChunkedText
import com.bkahlert.kommons.text.Text.Companion.asText
import com.bkahlert.kommons.text.Text.Companion.emptyText
import com.bkahlert.kommons.text.Text.Companion.mapText
import com.bkahlert.kommons.text.Text.TextComposite
import com.bkahlert.kommons.text.TextLength.Companion.chars
import com.bkahlert.kommons.text.TextLength.Companion.codePoints
import com.bkahlert.kommons.text.TextLength.Companion.graphemes
import com.bkahlert.kommons.text.Word.Letters
import com.bkahlert.kommons.text.Word.Space
import com.bkahlert.kommons.toList
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.inspectors.forAll
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.and
import io.kotest.matchers.be
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.iterator.shouldBeEmpty
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.reflect.KClass
import kotlin.test.Test

class TextKtTest {

    @Test fun empty_text() = testAll(
        emptyText<Char>(),
        emptyText<CodePoint>(),
        emptyText<Grapheme>(),
    ) {
        it.length shouldBe 0
        shouldThrow<IndexOutOfBoundsException> { it[-1] }.message shouldBe "index out of range: -1"
        shouldThrow<IndexOutOfBoundsException> { it[0] }.message shouldBe "index out of range: 0"
        it.subSequence(0, 0) shouldBe ""
        shouldThrow<IndexOutOfBoundsException> { it.subSequence(1, 0) }.message shouldBe "begin 1, end 0, length 0"
        it.subText(0, 0) shouldBeSameInstanceAs emptyText<Char>()
        shouldThrow<IndexOutOfBoundsException> { it.subText(1, 0) }.message shouldBe "begin 1, end 0, length 0"
        it.iterator().shouldBeEmpty()
        it.toString() shouldBe ""
    }

    @Test fun chunked_text() = testAll {
        val transform: (CharSequence, IntRange) -> List<kotlin.Char> = { text, range -> text.subSequence(range).toList() }
        ChunkedText(string, listOf(0..1, 2..3, 4..5), transform) should {
            it shouldBe ChunkedText(string, listOf(2, 4, 6).iterator(), transform)
            it shouldBe ChunkedText(string, 0..1, 2..3, 4..5, transform = transform)
            it should beText(
                ChunkedText(string, listOf(2, 4, 6).iterator(), transform),
                listOf('s', 't'),
                listOf('r', 'i'),
                listOf('n', 'g'),
            )
            it.iterator().toList().shouldContainExactly(
                ChunkedText(string, 0..1, transform = transform),
                ChunkedText(string, 2..3, transform = transform),
                ChunkedText(string, 4..5, transform = transform),
            )

            it.subSequence(0, 3) shouldBeSameInstanceAs string
            it.subText(0, 3) shouldBeSameInstanceAs it
        }
    }

    @Test fun text_composite() = testAll {
        val text1 = CodePoint.textOf(string)
        val text2 = CodePoint.textOf(emojiString)
        TextComposite(text1, text2) should {
            it should beText(
                TextComposite(CodePoint.textOf(string), CodePoint.textOf(emojiString)),
                CodePoint('s'),
                CodePoint('t'),
                CodePoint('r'),
                CodePoint('i'),
                CodePoint('n'),
                CodePoint('g'),
                *emojiCodePoints,
            )
            it.subText(5, 11).toString() shouldBe "ga𝕓🫠🇩🇪"
            it.iterator().toList().shouldContainExactly(
                text1.subText(0, 1),
                text1.subText(1, 2),
                text1.subText(2, 3),
                text1.subText(3, 4),
                text1.subText(4, 5),
                text1.subText(5, 6),
                text2.subText(0, 1),
                text2.subText(1, 2),
                text2.subText(2, 3),
                text2.subText(3, 4),
                text2.subText(4, 5),
                text2.subText(5, 6),
                text2.subText(6, 7),
                text2.subText(7, 8),
                text2.subText(8, 9),
                text2.subText(9, 10),
                text2.subText(10, 11),
                text2.subText(11, 12),
                text2.subText(12, 13),
                text2.subText(13, 14),
                text2.subText(14, 15),
                text2.subText(15, 16),
            )

            it.subSequence(0, 22) shouldBe CharSequenceComposite(string, emojiString)
            it.subText(0, 22) shouldBeSameInstanceAs it
        }

        TextComposite(text1, Grapheme.textOf(emojiString)).subText(5, 10).toString() shouldBe "ga𝕓🫠🇩🇪"
    }

    @Test fun as_text() = testAll {
        listOf(
            Char to emojiChars,
            CodePoint to emojiCodePoints,
            Grapheme to emojiGraphemes,
        ).forAll { (unit, chunks) ->
            emojiString.asText(unit) should beText(unit.textOf(emojiString), *chunks)
        }

        charSequence.asText(Word) should beText(
            Word.textOf(charSequence),
            Letters(charSequence, 0..3),
            Space(charSequence, 4..4),
            Letters(charSequence, 5..12),
        )
    }

    @Test fun map_text() = testAll {
        emojiString.mapText(Grapheme) { text ->
            buildText { text.reversed().forEach { add(it) } }
        } shouldBe "👩‍👩‍👦‍👦👨🏾‍🦱🇩🇪🫠𝕓a"
    }

    @Test fun is_empty() = testAll {
        emptyText<Nothing>().isEmpty() shouldBe true
        Char.textOf("").isEmpty() shouldBe true
        Char.textOf(emojiString).isEmpty() shouldBe false
    }

    @Test fun plus() = testAll(Char, CodePoint, Grapheme, Word) { unit ->
        unit.textOf(charSequence) should { text ->
            String.EMPTY.asText(unit) + text shouldBeSameInstanceAs text
            text + String.EMPTY.asText(unit) shouldBeSameInstanceAs text

            text + string.asText(unit) shouldBe TextComposite(text, string.asText(unit))
            string.asText(unit) + text shouldBe TextComposite(string.asText(unit), text)

            val uniqueText = ChunkedText(string, 0..2, 1..3) { s, r -> s.subSequence(r) }
            text + uniqueText shouldBe TextComposite(text, uniqueText)
        }
    }

    // TODO move to corresponding unit
    @Test fun text_unit() = testAll {
        Char.name shouldBe "char"
        Char.textOf(emojiString).asList().shouldContainExactly(*emojiChars)

        CodePoint.name shouldBe "code point"
        CodePoint.textOf(emojiString).asList().shouldContainExactly(*emojiCodePoints)

        Grapheme.name shouldBe "grapheme"
        Grapheme.textOf(emojiString).asList().shouldContainExactly(*emojiGraphemes)

        Word.textOf(charSequence).asList().shouldContainExactly(
            Letters(charSequence, 0..3),
            Space(charSequence, 4..4),
            Letters(charSequence, 5..12),
        )
    }

    @Test fun text_length() = testAll {
        42.chars shouldBe TextLength(42, Char)
        42.codePoints shouldBe TextLength(42, CodePoint)
        42.graphemes shouldBe TextLength(42, Grapheme)

        42.chars shouldNotBe 41.chars
        42.chars shouldNotBe 42.codePoints

        42.chars shouldBeLessThan 43.chars
        42.chars shouldBeGreaterThan 41.chars

        0.chars.toString() shouldBe "0 chars"
        1.chars.toString() shouldBe "1 char"
        42.chars.toString() shouldBe "42 chars"

        -(42.chars) shouldBe (-42).chars
        42.chars + 37 shouldBe 79.chars
        42.chars + 37.chars shouldBe 79.chars
        42.chars - 37 shouldBe 5.chars
        42.chars - 37.chars shouldBe 5.chars
        42.chars * 2 shouldBe 84.chars
        42.chars * 2.4 shouldBe 100.chars
        42.chars / 2 shouldBe 21.chars
        42.chars / 2.4 shouldBe 17.chars
        42.chars.isNegative() shouldBe false
        0.chars.isNegative() shouldBe false
        (-42).chars.isNegative() shouldBe true
        42.chars.isPositive() shouldBe true
        0.chars.isPositive() shouldBe false
        (-42).chars.isPositive() shouldBe false
        42.chars.absoluteValue shouldBe 42.chars
        0.chars.absoluteValue shouldBe 0.chars
        (-42).chars.absoluteValue shouldBe 42.chars
    }


    @Test fun require_not_negative() = testAll {
        requireNotNegative(1) shouldBe 1
        requireNotNegative(0) shouldBe 0
        shouldThrow<IllegalArgumentException> { requireNotNegative(-1) }
    }

    @Test fun build_text() = testAll {
        buildText {
            add(emojiString.asText(CodePoint))
            add(emojiString.asText(Grapheme))
        } should {
            it should beText(
                TextComposite(emojiString.asText(CodePoint), emojiString.asText(Grapheme)),
                *emojiCodePoints,
                *emojiGraphemes,
            )

            it.subSequence(0, 16).toString() shouldBe emojiString
            it.subSequence(0, 22).toString() shouldBe emojiString + emojiString
        }
    }

    @Test fun take() = testAll {
        charSequence.asText(Word) should {
            shouldThrow<IllegalArgumentException> { it.take(-1) }.message shouldBe "Requested text unit count -1 is less than zero."
            it.take(0).asList().shouldBeEmpty()
            it.take(1) shouldBe it.subText(0, 1)
            it.take(2) shouldBe it.subText(0, 2)
            it.take(3) shouldBeSameInstanceAs it
            it.take(4) shouldBeSameInstanceAs it
        }
    }

    @Test fun take_last() = testAll {
        charSequence.asText(Word) should {
            shouldThrow<IllegalArgumentException> { it.takeLast(-1) }.message shouldBe "Requested text unit count -1 is less than zero."
            it.takeLast(0).asList().shouldBeEmpty()
            it.takeLast(1) shouldBe it.subText(2, 3)
            it.takeLast(2) shouldBe it.subText(1, 3)
            it.takeLast(3) shouldBeSameInstanceAs it
            it.takeLast(4) shouldBeSameInstanceAs it
        }
    }


    @Test fun truncate() = testAll {
        listOf(
            Char to "a\uD835 … 👦",
            CodePoint to "a𝕓 … ‍👦",
            Grapheme to "a𝕓 … 👨🏾‍🦱👩‍👩‍👦‍👦",
        ).forAll { (unit, expected) ->
            val text = longString.asText(unit)
            text.truncate(7, " … ".asText(unit)) should {
                it shouldBe TextComposite(text.subText(0, 2), " … ".asText(unit), text.subText(text.length - 2, text.length))
                it.toString() shouldBe expected
            }
            text.truncate(10_000_000, " … ".asText(unit)) should {
                it shouldBeSameInstanceAs text
                it.toString() shouldBeSameInstanceAs longString
            }
            shouldThrow<IllegalArgumentException> { text.truncateEnd(1, " … ".asText(unit)) }
                .message shouldBe "The specified length (1) must be greater or equal than the length of the marker \" … \" (3)."
        }
    }

    @Test fun truncate_start() = testAll {
        listOf(
            Char to "… 👦‍👦",
            CodePoint to "… 👩‍👦‍👦",
            Grapheme to "… 𝕓🫠🇩🇪👨🏾‍🦱👩‍👩‍👦‍👦",
        ).forAll { (unit, expected) ->
            val text = longString.asText(unit)
            text.truncateStart(7, "… ".asText(unit)) should {
                it shouldBe TextComposite("… ".asText(unit), text.subText(text.length - 5, text.length))
                it.toString() shouldBe expected
            }
            text.truncateStart(10_000_000, "… ".asText(unit)) should {
                it shouldBeSameInstanceAs text
                it.toString() shouldBeSameInstanceAs longString
            }
            shouldThrow<IllegalArgumentException> { text.truncateStart(1, "… ".asText(unit)) }
                .message shouldBe "The specified length (1) must be greater or equal than the length of the marker \"… \" (2)."
        }
    }

    @Test fun truncate_end() = testAll {
        listOf(
            Char to "a𝕓🫠 …",
            CodePoint to "a𝕓🫠🇩🇪 …",
            Grapheme to "a𝕓🫠🇩🇪👨🏾‍🦱 …",
        ).forAll { (unit, expected) ->
            val text = longString.asText(unit)
            text.truncateEnd(7, " …".asText(unit)) should {
                it shouldBe TextComposite(text.subText(0, 5), " …".asText(unit))
                it.toString() shouldBe expected
            }
            text.truncateEnd(10_000_000, " …".asText(unit)) should {
                it shouldBeSameInstanceAs text
                it.toString() shouldBeSameInstanceAs longString
            }
            shouldThrow<IllegalArgumentException> { text.truncateEnd(1, " …".asText(unit)) }
                .message shouldBe "The specified length (1) must be greater or equal than the length of the marker \" …\" (2)."
        }
    }
}


fun <T, U> withProp(matcher: Matcher<U>, resolve: (T) -> U): Matcher<T> = Matcher {
    matcher.test(resolve(it))
}

fun <T> haveProp(name: String, expected: Any?, resolve: (T) -> Any?): Matcher<T> = haveProp(name, null, expected, resolve)

fun <T> haveProp(name: String, details: String?, expected: Any?, resolve: (T) -> Any?): Matcher<T> = Matcher {
    val actual = resolve(it)
    MatcherResult(
        actual == expected,
        { "should have $name ${expected.render()}${details.startSpaced} but was ${actual.render()}" },
        { "should not have $name ${expected.render()}${details.startSpaced}" },
    )
}

inline fun <T, reified A : Throwable> havePropFailure(
    name: String,
    details: String?,
    messageGlob: String? = null,
    noinline resolve: (T) -> Any?
): Matcher<T> = havePropFailure(name, details, A::class, messageGlob, resolve)

fun <T> havePropFailure(
    name: String,
    details: String?,
    expected: KClass<out Throwable>,
    messageGlob: String? = null,
    resolve: (T) -> Any?
): Matcher<T> = Matcher {
    runCatching { resolve(it) }.fold(
        { actual ->
            MatcherResult(
                false,
                { "should fail to get $name${details.startSpaced} but got ${actual.render()}" },
                { "should not fail to get $name${details.startSpaced}" },
            )
        },
        { actual ->
            if (expected.isInstance(actual)) {
                MatcherResult(
                    messageGlob == null || actual.message?.matchesGlob(messageGlob) == true,
                    { "should fail to get $name${details.startSpaced} with message $messageGlob but got ${actual.message}" },
                    { "should fail to get $name${details.startSpaced} with message $messageGlob" },
                )
            } else {
                MatcherResult(
                    false,
                    { "should fail to get $name${details.startSpaced} with a ${expected.simpleName} but got a ${actual::class.simpleName}" },
                    { "should not fail to get $name${details.startSpaced} with a ${expected.simpleName}" },
                )
            }
        }
    )
}

internal class WordBreakIterator(
    text: CharSequence,
) : Iterator<Int> by (text.splitToSequence(Regex("\\b"))
    .runningFold(0) { acc, chunk -> acc + chunk.length }
    .distinct()
    .drop(1)
    .iterator())

internal sealed class Word(
    text: CharSequence,
    range: IntRange,
) : CharSequence by CharSequenceDelegate(text, range) {
    internal data class Letters(
        val text: CharSequence,
        val range: IntRange,
    ) : Word(text, range)

    internal data class Space(
        val text: CharSequence,
        val range: IntRange,
    ) : Word(text, range)

    internal data class Mixed(
        val text: CharSequence,
        val range: IntRange,
    ) : Word(text, range)

    companion object : TextUnit<Word> {
        override val name: String = "word"
        override fun textOf(text: CharSequence): Text<Word> = ChunkedText(
            text,
            WordBreakIterator(text).mapToRanges().toList(),
        ) { txt, range ->
            val (whitespaces, nonWhitespaces) = txt.subSequence(range).partition { it.isWhitespace() }
            when {
                whitespaces.isEmpty() -> Letters(txt, range)
                nonWhitespaces.isEmpty() -> Space(txt, range)
                else -> Mixed(txt, range)
            }
        }

        override fun toString(): String = name
    }
}


fun <T> haveStringRepresentation(string: String) =
    haveProp<T>("string representation", string) { it.toString() }

fun <T> haveLength(length: Int) =
    haveProp<Text<T>>("length", length) { it.length }

fun <T> haveChunk(index: Int, chunk: T) =
    haveProp<Text<T>>("chunk", "at $index", chunk) { it[index] }

fun <T> haveSubSequence(startIndex: Int, endIndex: Int, expected: CharSequence) =
    haveProp<Text<T>>("sub sequence", "from $startIndex to $endIndex", expected) { it.subSequence(startIndex, endIndex) }

fun <T> haveSubText(startIndex: Int, endIndex: Int, expected: Text<T>) =
    haveProp<Text<T>>("sub text", "from $startIndex to $endIndex", expected) { it.subText(startIndex, endIndex) }

fun <T> haveChunkFailure(index: Int) =
    havePropFailure<Text<T>, IndexOutOfBoundsException>("chunk", "at $index", "index out of range: $index") { it[index] }

fun <T> haveSubSequenceFailure(startIndex: Int, endIndex: Int) =
    havePropFailure<Text<T>, IndexOutOfBoundsException>(
        "sub sequence",
        "from $startIndex to $endIndex",
        "begin $startIndex, end $endIndex, length *"
    ) { it.subSequence(startIndex, endIndex) }

fun <T> haveSubTextFailure(startIndex: Int, endIndex: Int) =
    havePropFailure<Text<T>, IndexOutOfBoundsException>(
        "sub text",
        "from $startIndex to $endIndex",
        "begin $startIndex, end $endIndex, length *"
    ) { it.subText(startIndex, endIndex) }

fun <T> beText(
    text: Text<T>,
    vararg chunks: T,
): Matcher<Text<T>> {
    return be(text)
        .and(haveLength(chunks.size))
        .and(haveChunk(0, chunks[0]))
        .and(haveChunk(chunks.size - 1, chunks.last()))
        .and(haveChunkFailure(-1))
        .and(haveChunkFailure(chunks.size))
        .and(haveSubSequence(0, 1, text.subSequence(0, 1)))
        .and(haveSubSequence(1, 2, text.subSequence(1, 2)))
        .and(haveSubSequence(0, chunks.size - 1, text.subSequence(endIndex = chunks.size - 1)))
        .and(haveSubSequence(0, chunks.size, text.subSequence()))
        .and(haveSubSequence(0, 0, ""))
        .and(haveSubSequenceFailure(1, 0))
        .and(haveSubSequence(0, 0, ""))
        .and(haveSubSequenceFailure(1, 0))
        .and(haveSubText(0, 1, text.subText(0, 1)))
        .and(haveSubText(1, 2, text.subText(1, 2)))
        .and(haveSubText(0, chunks.size - 1, text.subText(endIndex = chunks.size - 1)))
        .and(haveSubText(0, chunks.size, text.subText()))
        .and(haveSubText(0, 0, emptyText()))
        .and(haveSubTextFailure(1, 0))
        .and(withProp(be(chunks.toList())) { it.asList() })
        .and(haveStringRepresentation(text.toString()))
}
