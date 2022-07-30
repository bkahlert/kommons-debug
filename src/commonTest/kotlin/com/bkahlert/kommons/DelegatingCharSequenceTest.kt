package com.bkahlert.kommons

import com.bkahlert.kommons.test.testAll
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.test.Test

class DelegatingCharSequenceTest {

    @Test fun of_empty() = testAll {
        val empty = ""
        val foo = DelegatingCharSequence(empty)
        foo.length shouldBe 0
        shouldThrow<IndexOutOfBoundsException> { foo[-1] }.message shouldBe "index out of range: -1"
        shouldThrow<IndexOutOfBoundsException> { foo[0] }.message shouldBe "index out of range: 0"
        foo.subSequence(0, 0) shouldBe ""
        shouldThrow<IndexOutOfBoundsException> { foo.subSequence(1, 0) }.message shouldBe "begin 1, end 0, length 0"
        foo.toString() shouldBeSameInstanceAs empty
    }

    @Test fun of_string() = testAll {
        val string = "foo"
        val foo = DelegatingCharSequence(string)
        assertFoo(foo)
        foo.toString() shouldBeSameInstanceAs "foo"
    }

    @Test fun of_string_builder() = testAll {
        val stringBuilder = StringBuilder("foo")
        val foo = DelegatingCharSequence(stringBuilder)
        assertFoo(foo)
    }

    @Test fun of_delegate() = testAll {
        val stringBuilder = StringBuilder("foo")
        val delegate = DelegatingCharSequence(stringBuilder)
        val foo = DelegatingCharSequence(delegate)
        assertFoo(foo)

        stringBuilder.append("-bar")
        foo.length shouldBe 7
        foo[0] shouldBe 'f'
        foo[1] shouldBe 'o'
        foo.subSequence(2, 5).toString() shouldBe "o-b"
        foo.toString() shouldBe "foo-bar"
    }

    @Test fun of_delegate_subSequence() = testAll {
        val stringBuilder = StringBuilder("-foo-")
        val delegate = DelegatingCharSequence(stringBuilder)
        val foo = delegate.subSequence(1, 4)
        assertFoo(foo)

        stringBuilder.insert(0, ">")
        foo.length shouldBe 3
        foo[0] shouldBe '-'
        foo[1] shouldBe 'f'
        foo.subSequence(2, 3).toString() shouldBe "o"
        foo.toString() shouldBe "-fo"
    }

    @Test fun instantiation() = testAll {
        shouldNotThrowAny { DelegatingCharSequence("foo") }
        shouldNotThrowAny { DelegatingCharSequence("foo", null) }
        shouldNotThrowAny { DelegatingCharSequence("foo", 0..2) }
        shouldThrow<IndexOutOfBoundsException> { DelegatingCharSequence("foo", 0..3) }.message shouldBe "begin 0, end 4, length 3"
    }

    @Test fun to_string() = testAll {
        DelegatingCharSequence("foo").toString() shouldBe "foo"
        DelegatingCharSequence("foo", 0..0).toString() shouldBe "f"
        DelegatingCharSequence("foo", 0..1).toString() shouldBe "fo"
        DelegatingCharSequence("foo", 0..2).toString() shouldBe "foo"
        DelegatingCharSequence("foo", 1..2).toString() shouldBe "oo"
        DelegatingCharSequence("foo", 2..2).toString() shouldBe "o"
        @Suppress("EmptyRange")
        DelegatingCharSequence("foo", 3..2).toString() shouldBe ""
    }

    @Test fun equality() = testAll {
        DelegatingCharSequence("foo", 0..1) shouldBe DelegatingCharSequence("fo")
        DelegatingCharSequence("foo", 0..1) shouldBe DelegatingCharSequence("foo", 0..1)
        DelegatingCharSequence("foo", 0..1) shouldNotBe DelegatingCharSequence("bar", 0..1)
        DelegatingCharSequence("foo", 0..1) shouldNotBe DelegatingCharSequence("foo", 1..2)
    }

    @Test fun hash_code() = testAll {
        DelegatingCharSequence("foo", 0..1).hashCode() shouldBe "fo".hashCode()
    }
}

private fun assertFoo(foo: CharSequence) {
    foo shouldHaveLength 3
    shouldThrowAny { foo[-1] }
    foo[0] shouldBe 'f'
    foo[1] shouldBe 'o'
    foo[2] shouldBe 'o'
    shouldThrowAny { foo[3] }
    foo.subSequence(0, 3).toString() shouldBe "foo"
    foo.subSequence(0, 2).toString() shouldBe "fo"
    foo.subSequence(1, 2).toString() shouldBe "o"
    foo.subSequence(2, 2).toString() shouldBe ""
}
