package com.bkahlert.kommons.debug

import com.bkahlert.kommons.debug.Platform.JS
import com.bkahlert.kommons.debug.Platform.JVM
import com.bkahlert.kommons.tests
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import kotlin.test.Test

class StackTraceTest {

    @Test fun get_first() = tests {
        StackTrace.get().first() should {
            when (Platform.Current) {
                JS -> {
                    it.receiver shouldBe "StackTraceTest"
                    it.function shouldStartWith "get_first"
                    it.file shouldContain "/commons.js"
                    it.line shouldBeGreaterThan 0
                    it.column.shouldNotBeNull().shouldBeGreaterThan(0)
                }
                JVM -> {
                    it.receiver shouldBe "com.bkahlert.kommons.debug.StackTraceTest"
                    it.function shouldBe "get_first"
                    it.file shouldBe "StackTraceKtTest.kt"
                    it.line shouldBeGreaterThan 0
                    it.column.shouldBeNull()
                }
            }
        }
    }

    @Test fun find_or_null() = tests {
        foo { bar { StackTrace.findOrNull { it.function == "foo" } } } should { it?.function shouldStartWith "find_or_null" }
        foo { bar { StackTrace.findOrNull { false } } }.shouldBeNull()
    }

    @Test fun find_by_last_known_call_null() = tests {
        foo { bar { StackTrace.findByLastKnownCallOrNull("bar") } } should { it?.function shouldStartWith "foo" }
        foo { bar { StackTrace.findByLastKnownCallOrNull(::bar) } } should { it?.function shouldStartWith "foo" }

        foo { bar { StackTrace.findByLastKnownCallOrNull("foo") } } should { it?.function shouldStartWith "find_by_last_known_call_null" }
        foo { bar { StackTrace.findByLastKnownCallOrNull(::foo) } } should { it?.function shouldStartWith "find_by_last_known_call_null" }

        foo { bar { StackTrace.findByLastKnownCallOrNull("toString") } }.shouldBeNull()
        foo { bar { StackTrace.findByLastKnownCallOrNull(String::toString) } }.shouldBeNull()
    }
}

internal fun foo(block: () -> StackTraceElement?) = block()
internal fun bar(block: () -> StackTraceElement?) = block()
