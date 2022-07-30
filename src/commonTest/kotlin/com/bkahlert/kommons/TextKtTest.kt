package com.bkahlert.kommons

import com.bkahlert.kommons.Text.Char
import com.bkahlert.kommons.Text.Companion
import com.bkahlert.kommons.Text.Companion.emptyText
import com.bkahlert.kommons.Text.Companion.restrictedText
import com.bkahlert.kommons.Text.Companion.toText
import com.bkahlert.kommons.TextLength.Companion.chars
import com.bkahlert.kommons.TextLength.Companion.codePoints
import com.bkahlert.kommons.TextLength.Companion.graphemes
import com.bkahlert.kommons.TextLength.Companion.toTextLength
import com.bkahlert.kommons.TextUnit.Chars
import com.bkahlert.kommons.TextUnit.CodePoints
import com.bkahlert.kommons.TextUnit.Graphemes
import com.bkahlert.kommons.debug.render
import com.bkahlert.kommons.test.testAll
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.inspectors.forAll
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.and
import io.kotest.matchers.be
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.reflect.KClass
import kotlin.test.Test

class TextKtTest {

    @Test fun char_text() = testAll {
        charText should beText(emojiString, *emojiChars)
    }

    @Test fun codepoint_text() = testAll {
        codePointText should beText(emojiString, *emojiCodePoints)
    }

    @Test fun grapheme_text() = testAll {
        graphemeText should beText(emojiString, *emojiGraphemes)
    }

    // TODO toText(Chars)
    // TODO toText<Char>()
    // TODO toText<Unit>()
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
        it.toString() shouldBe ""
    }

    @Test fun restricted_text() = testAll {
        listOf(
            restrictedText<Char>("foo"),
            restrictedText<CodePoint>("foo"),
            restrictedText<Grapheme>("foo"),
        ).forAll {
            it shouldBe "foo".toText()
            it.length shouldBe 3
            (-1..4).forEach { index ->
                shouldThrow<UnsupportedOperationException> { it[index] }
            }
            shouldThrow<IllegalArgumentException> { it.subSequence(1, 3) }.message shouldBe "startIndex must be 0"
            shouldThrow<IllegalArgumentException> { it.subSequence(0, 2) }.message shouldBe "endIndex must be 3"
            it.subSequence(0, 3) shouldBe "foo"
            it.toString() shouldBe "foo"
        }

        listOf<(CharSequence) -> Text<CharSequence>>(
            { restrictedText<Char>(it) },
            { restrictedText<CodePoint>(it) },
            { restrictedText<Grapheme>(it) },
        ).forAll {
            shouldThrow<IllegalArgumentException> { it("🫠") }.message shouldBe "text must not contain surrogates"
        }
    }

    @Test fun text_length() = testAll {
        42.chars shouldBe TextLength(42, Char::class)
        42.codePoints shouldBe TextLength(42, CodePoint::class)
        42.graphemes shouldBe TextLength(42, Grapheme::class)
        42.chars shouldBe 42.toTextLength()
        42.codePoints shouldBe 42.toTextLength()
        42.graphemes shouldBe 42.toTextLength()

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

    // TODO test take
}

internal val codePoint: CodePoint = CodePoint(0x1FAE0) // 🫠
internal val grapheme: Grapheme = Grapheme("👨🏾‍🦱")
internal val charText: Text<Char> = emojiString.toText(Chars)
internal val codePointText: Text<CodePoint> = emojiString.toText(CodePoints)
internal val graphemeText: Text<Grapheme> = emojiString.toText(Graphemes)


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

internal class NonSingletonUnit() : TextUnit<String> by TextUnit.build(Companion::restrictedText)
internal object WordUnit : TextUnit<String> by TextUnit.build({ Text.from(it, { text -> text.split(" ") }) })


fun <T> haveStringRepresentation(string: String) =
    haveProp<T>("string representation", string) { it.toString() }

fun <T : CharSequence> haveLength(length: Int) =
    haveProp<Text<T>>("length", length) { it.length }

fun <T : CharSequence> haveChunk(index: Int, chunk: T) =
    haveProp<Text<T>>("chunk", "at $index", chunk) { it[index] }

fun <T : CharSequence> haveSubSequence(startIndex: Int, endIndex: Int, expected: CharSequence) =
    haveProp<Text<T>>("sub sequence", "from $startIndex to $endIndex", expected) { it.subSequence(startIndex, endIndex) }

fun <T : CharSequence> haveChunkFailure(index: Int) =
    havePropFailure<Text<T>, IndexOutOfBoundsException>("chunk", "at $index", "index out of range: $index") { it[index] }

fun <T : CharSequence> haveSubSequenceFailure(startIndex: Int, endIndex: Int) =
    havePropFailure<Text<T>, IndexOutOfBoundsException>(
        "sub sequence",
        "from $startIndex to $endIndex",
        "begin $startIndex, end $endIndex, length *"
    ) { it.subSequence(startIndex, endIndex) }


inline fun <reified T : CharSequence> beText(
    expected: CharSequence,
    vararg chunks: T,
): Matcher<Text<T>> {
    val text = expected.toText<T>()
    return be(text)
        .and(haveLength(chunks.size))
        .and(haveChunk(0, chunks[0]))
        .and(haveChunk(chunks.size - 1, chunks.last()))
        .and(haveChunkFailure(-1))
        .and(haveChunkFailure(chunks.size))
        .and(haveSubSequence(0, 1, ComposingCharSequence(chunks[0])))
        .and(haveSubSequence(1, 2, ComposingCharSequence(chunks[1])))
        .and(haveSubSequence(0, chunks.size - 1, ComposingCharSequence(chunks.slice(0 until chunks.size - 1))))
        .and(haveSubSequence(0, chunks.size, ComposingCharSequence(chunks.slice(chunks.indices))))
        .and(haveSubSequence(0, 0, ""))
        .and(haveSubSequenceFailure(1, 0))
        .and(haveStringRepresentation(expected.toString()))
}
