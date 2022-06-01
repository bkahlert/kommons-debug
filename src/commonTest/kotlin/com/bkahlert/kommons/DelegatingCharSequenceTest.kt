package com.bkahlert.kommons

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Suppress("ReplaceCallWithBinaryOperator")
class DelegatingCharSequenceTest {

    @Test fun of_string() = tests {
        val string = "foo"
        val foo = DelegatingCharSequence(string)
        assertFoo(foo)
        foo.toString() shouldBeSameInstanceAs "foo"
    }

    @Test fun of_string_builder() = tests {
        val stringBuilder = StringBuilder("foo")
        val foo = DelegatingCharSequence(stringBuilder)
        assertFoo(foo)
    }

    @Test fun of_delegate() = tests {
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

    @Test fun of_delegate_subSequence() = tests {
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
}

private fun assertFoo(foo: CharSequence) {
    assertEquals(3, foo.length)
    assertTrue { runCatching { foo[-1] }.isFailure }
    assertEquals('f', foo[0])
    assertEquals('o', foo[1])
    assertEquals('o', foo[2])
    assertTrue { runCatching { foo[3] }.isFailure }
    assertEquals("foo", foo.subSequence(0, 3).toString())
    assertEquals("fo", foo.subSequence(0, 2).toString())
    assertEquals("o", foo.subSequence(1, 2).toString())
    assertEquals("", foo.subSequence(2, 2).toString())
}
