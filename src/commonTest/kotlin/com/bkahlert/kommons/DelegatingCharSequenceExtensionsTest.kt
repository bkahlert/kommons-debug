package com.bkahlert.kommons

import com.bkahlert.kommons.test.testAll
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class DelegatingCharSequenceExtensionsTest {

    @Test fun assume_fixed_with_string() = testAll {
        val foo = "foo"
        val fo: String = foo.dropLast(1)
        fo shouldBe "fo"
    }

    @Test fun assume_fixed_with_char_sequence() = testAll {
        val foo: CharSequence = object : CharSequence by "foo" {}
        val fo: CharSequence = foo.dropLast(1)
        fo.toString() shouldBe "fo"
    }

    @Test fun assume_fixed_with_string_builder() = testAll {
        val foo: StringBuilder = StringBuilder().apply { append("foo") }
        val fo: CharSequence = foo.dropLast(1)

        foo.toString() shouldBe "foo"
        fo.toString() shouldBe "fo"

        foo.insert(0, ">")
        foo.toString() shouldBe ">foo"
        fo.toString() shouldBe "fo"
    }

    @Test fun test_delegating() = testAll {
        val foo = TestCharSequence("foo")
        val fo: CharSequence = DelegatingCharSequence(foo).dropLast(1)

        foo.toString() shouldBe "foo"
        fo.toString() shouldBe "fo"

        foo.update(">foo")
        foo.toString() shouldBe ">foo"
        fo.toString() shouldBe ">f"
    }
}

internal class TestCharSequence(delegate: CharSequence) : DelegatingCharSequence(delegate) {
    fun update(delegate: CharSequence): TestCharSequence {
        this.delegate = delegate
        return this
    }
}
