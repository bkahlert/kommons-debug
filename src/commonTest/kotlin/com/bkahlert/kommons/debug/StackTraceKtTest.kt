package com.bkahlert.kommons.debug

import com.bkahlert.kommons.Current
import com.bkahlert.kommons.Platform
import com.bkahlert.kommons.Platform.JS
import com.bkahlert.kommons.Platform.JVM
import com.bkahlert.kommons.test.test
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldNotStartWith
import io.kotest.matchers.string.shouldStartWith
import kotlin.test.Test

class StackTraceTest {

    @Test fun to_string() = test {
        var stackTrace = ""
        foo { bar { StackTrace.get().also { stackTrace = it.toString() }.firstOrNull() } }
        stackTrace.lines() should {
            it.size shouldBeGreaterThan 3
            it.first() shouldNotStartWith "    at "
            it.drop(1).shouldForAll { line -> line shouldStartWith "    at " }
        }
    }

    @Test fun get_first() = test {
        StackTrace.get().first() should {
            when (Platform.Current) {
                JS.Browser -> {
                    it.receiver shouldBe "StackTraceTest"
                    it.function shouldStartWith "get_first"
                    it.file shouldContain "/commons.js"
                    it.line shouldBeGreaterThan 0
                    it.column.shouldNotBeNull().shouldBeGreaterThan(0)
                }
                JS.NodeJS -> {
                    it.receiver shouldBe "StackTraceTest"
                    it.function shouldStartWith "get_first"
                    it.file shouldEndWith "/JsStackTrace.kt"
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

    @Test fun find_or_null() = test {
        foo { bar { StackTrace.get().findOrNull { it.function == "foo" } } } should { it?.function shouldStartWith "find_or_null" }
        foo { bar { StackTrace.get().findOrNull { false } } }.shouldBeNull()
    }

    @Test fun find_by_last_known_call_null() = test {
        foo { bar { StackTrace.get().findByLastKnownCallsOrNull("bar") } } should { it?.function shouldStartWith "foo" }
        foo { bar { StackTrace.get().findByLastKnownCallsOrNull(::bar) } } should { it?.function shouldStartWith "foo" }

        foo { bar { StackTrace.get().findByLastKnownCallsOrNull("foo") } } should { it?.function shouldStartWith "find_by_last_known_call_null" }
        foo { bar { StackTrace.get().findByLastKnownCallsOrNull(::foo) } } should { it?.function shouldStartWith "find_by_last_known_call_null" }

        foo { bar { StackTrace.get().findByLastKnownCallsOrNull("toString") } }.shouldBeNull()
        foo { bar { StackTrace.get().findByLastKnownCallsOrNull(String::toString) } }.shouldBeNull()
    }
}

internal fun foo(block: () -> StackTraceElement?) = block()
internal fun bar(block: () -> StackTraceElement?) = block()
