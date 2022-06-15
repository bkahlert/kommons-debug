package com.bkahlert.kommons

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.doubles.shouldBeBetween
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import kotlin.random.Random
import kotlin.test.Test

class RangesKtTest {

    @Test fun random() = tests {
        (-4.2..42.0) should { range ->
            repeat(100) { range.random().shouldBeBetween(-4.2, 42.0, 0.1) }
            repeat(100) { range.random(Random(123)).shouldBeBetween(-4.2, 42.0, 0.1) }
        }
        (42.0..-4.2) should { range ->
            shouldThrow<NoSuchElementException> { range.random().shouldBeNull() }
            shouldThrow<NoSuchElementException> { range.random(Random(123)).shouldBeNull() }
        }
    }

    @Test fun random_or_null() = tests {
        (-4.2..42.0) should { range ->
            repeat(100) { range.randomOrNull().shouldNotBeNull().shouldBeBetween(-4.2, 42.0, 0.1) }
            repeat(100) { range.randomOrNull(Random(123)).shouldNotBeNull().shouldBeBetween(-4.2, 42.0, 0.1) }
        }
        (42.0..-4.2) should { range ->
            range.randomOrNull().shouldBeNull()
            range.randomOrNull(Random(123)).shouldBeNull()
        }
    }

    @Test fun as_iterable() = tests {
        (-4..42).asIterable { it + 9 }.map { it }.shouldContainExactly(-4, 5, 14, 23, 32, 41)
        (-4.2..42.0).asIterable { it + 9 }.map { it.toInt() }.shouldContainExactly(-4, 4, 13, 22, 31, 40)
        (42.0..-4.2).asIterable { it + 9 }.map { it.toInt() }.shouldBeEmpty()
    }
}
