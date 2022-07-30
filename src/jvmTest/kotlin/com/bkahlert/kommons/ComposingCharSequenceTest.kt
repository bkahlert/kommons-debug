package com.bkahlert.kommons

import com.bkahlert.kommons.test.testAll
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.inspectors.forAll
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldHaveLength
import org.junit.jupiter.api.Test

class ComposingCharSequenceTest {

    @Test fun length() = testAll {
        composingCharSequence0 shouldHaveLength 0
        composingCharSequence1Empty shouldHaveLength 0
        composingCharSequence1 shouldHaveLength 3
        composingCharSequence2 shouldHaveLength 6
        composingCharSequence3 shouldHaveLength 9

        val testCharSequence = TestCharSequence("test")
        composingCharSequence3(testCharSequence) should {
            it shouldHaveLength 10
            testCharSequence.update("ts")
            it shouldHaveLength 8
        }
    }

    @Test fun get() = testAll {
        shouldThrow<IndexOutOfBoundsException> { composingCharSequence0[0] }.message shouldBe "index out of range: 0"
        shouldThrow<IndexOutOfBoundsException> { composingCharSequence1Empty[0] }.message shouldBe "index out of range: 0"
        composingCharSequence1 should {
            shouldThrow<IndexOutOfBoundsException> { it[-1] }.message shouldBe "index out of range: -1"
            it[0] shouldBe 'f'
            it[1] shouldBe 'o'
            it[2] shouldBe 'o'
            shouldThrow<IndexOutOfBoundsException> { it[3] }.message shouldBe "index out of range: 3"
        }
        composingCharSequence2 should {
            shouldThrow<IndexOutOfBoundsException> { it[-1] }.message shouldBe "index out of range: -1"
            it[0] shouldBe 'f'
            it[1] shouldBe 'o'
            it[2] shouldBe 'o'
            it[3] shouldBe 'b'
            it[4] shouldBe 'a'
            it[5] shouldBe 'r'
            shouldThrow<IndexOutOfBoundsException> { it[6] }.message shouldBe "index out of range: 6"
        }
        composingCharSequence3 should {
            shouldThrow<IndexOutOfBoundsException> { it[-1] }.message shouldBe "index out of range: -1"
            it[0] shouldBe 'f'
            it[1] shouldBe 'o'
            it[2] shouldBe 'o'
            it[3] shouldBe 'b'
            it[4] shouldBe 'a'
            it[5] shouldBe 'r'
            it[6] shouldBe 'b'
            it[7] shouldBe 'a'
            it[8] shouldBe 'z'
            shouldThrow<IndexOutOfBoundsException> { it[9] }.message shouldBe "index out of range: 9"
        }

        val testCharSequence = TestCharSequence("test")
        composingCharSequence3(testCharSequence) should {
            it[6] shouldBe 't'
            it[7] shouldBe 'b'
            testCharSequence.update("ts")
            it[6] shouldBe 'a'
            it[7] shouldBe 'z'
        }
    }

    @Suppress("SpellCheckingInspection")
    @Test fun subSequence() = testAll {
        listOf(composingCharSequence0, composingCharSequence1Empty).forAll {
            it.subSequence(0, 0) shouldBe ""
            shouldThrow<IndexOutOfBoundsException> { it.subSequence(-1, 0) }
            shouldThrow<IndexOutOfBoundsException> { it.subSequence(0, 1) }
        }
        composingCharSequence1 should {
            it.subSequence(0, 3).toString() shouldBe "foo"
            it.subSequence(1, 2).toString() shouldBe "o"
            shouldThrow<IndexOutOfBoundsException> { it.subSequence(-1, 0) }
            shouldThrow<IndexOutOfBoundsException> { it.subSequence(0, 4) }
        }
        composingCharSequence2 should {
            it.subSequence(0, 6).toString() shouldBe "foobar"
            it.subSequence(1, 5).toString() shouldBe "ooba"
            shouldThrow<IndexOutOfBoundsException> { it.subSequence(-1, 0) }
            shouldThrow<IndexOutOfBoundsException> { it.subSequence(0, 7) }
        }
        composingCharSequence3 should {
            it.subSequence(0, 9).toString() shouldBe "foobarbaz"
            it.subSequence(1, 5).toString() shouldBe "ooba"
            it.subSequence(4, 8).toString() shouldBe "arba"
            shouldThrow<IndexOutOfBoundsException> { it.subSequence(-1, 0) }
            shouldThrow<IndexOutOfBoundsException> { it.subSequence(0, 10) }
        }

        val testCharSequence = TestCharSequence("test")
        composingCharSequence3(testCharSequence) should {
            it.subSequence(0, 10).toString() shouldBe "footestbaz"
            it.subSequence(1, 6).toString() shouldBe "ootes"
            it.subSequence(4, 9).toString() shouldBe "estba"
            shouldThrow<IndexOutOfBoundsException> { it.subSequence(-1, 0) }
            shouldThrow<IndexOutOfBoundsException> { it.subSequence(0, 11) }
            testCharSequence.update("bar")
            it.subSequence(0, 9).toString() shouldBe "foobarbaz"
            it.subSequence(1, 5).toString() shouldBe "ooba"
            it.subSequence(4, 8).toString() shouldBe "arba"
            shouldThrow<IndexOutOfBoundsException> { it.subSequence(-1, 0) }
            shouldThrow<IndexOutOfBoundsException> { it.subSequence(0, 10) }
        }
    }

    @Test fun instantiation() = testAll {
        ComposingCharSequence("foo", "bar") shouldBe ComposingCharSequence(listOf("foo", "bar"))
    }

    @Suppress("SpellCheckingInspection")
    @Test fun to_string() = testAll {
        composingCharSequence0.toString() shouldBe ""
        composingCharSequence1Empty.toString() shouldBe ""
        composingCharSequence1.toString() shouldBe "foo"
        composingCharSequence2.toString() shouldBe "foobar"
        composingCharSequence3.toString() shouldBe "foobarbaz"

        val testCharSequence = TestCharSequence("test")
        composingCharSequence3(testCharSequence) should {
            it.toString() shouldBe "footestbaz"
            testCharSequence.update("bar")
            it.toString() shouldBe "foobarbaz"
        }
    }

    @Test fun equality() = testAll {
        composingCharSequence0 shouldNotBe DelegatingCharSequence("")
        composingCharSequence0 shouldBe composingCharSequence1Empty

        val testCharSequence = TestCharSequence("test")
        composingCharSequence3(testCharSequence) should {
            it shouldNotBe composingCharSequence3
            testCharSequence.update("bar")
            it shouldBe composingCharSequence3
        }
    }

    @Test fun hash_code() = testAll {
        composingCharSequence0.hashCode() shouldBe composingCharSequence1Empty.hashCode()

        val testCharSequence = TestCharSequence("test")
        composingCharSequence3(testCharSequence) should {
            it.hashCode() shouldNotBe composingCharSequence3.hashCode()
            testCharSequence.update("bar")
            it.hashCode() shouldBe composingCharSequence3.hashCode()
        }
    }
}

internal val composingCharSequence0 = ComposingCharSequence()
internal val composingCharSequence1Empty = ComposingCharSequence("")
internal val composingCharSequence1 = ComposingCharSequence("foo")
internal val composingCharSequence2 = ComposingCharSequence("foo", "bar")
internal val composingCharSequence3 = ComposingCharSequence("foo", "bar", "baz")
internal fun composingCharSequence3(charSequence: CharSequence) = ComposingCharSequence("foo", charSequence, "baz")
