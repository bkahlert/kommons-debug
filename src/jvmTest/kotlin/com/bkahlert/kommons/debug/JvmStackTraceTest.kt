package com.bkahlert.kommons.debug

import com.bkahlert.kommons.tests
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test

class JvmStackTraceTest {

    @Test fun find_by_last_known_call_null() = tests {
        val receiver = "com.bkahlert.kommons.debug.StackTraceKtTestKt"
        foo { bar { StackTrace.findByLastKnownCallOrNull(receiver, "bar") } } should { it?.function shouldBe "foo" }
        foo { bar { StackTrace.findByLastKnownCallOrNull("bar") } } should { it?.function shouldBe "foo" }
        foo { bar { StackTrace.findByLastKnownCallOrNull(::bar) } } should { it?.function shouldBe "foo" }

        foo { bar { StackTrace.findByLastKnownCallOrNull(receiver, "foo") } } should { it?.function shouldBe "find_by_last_known_call_null" }
        foo { bar { StackTrace.findByLastKnownCallOrNull("foo") } } should { it?.function shouldBe "find_by_last_known_call_null" }
        foo { bar { StackTrace.findByLastKnownCallOrNull(::foo) } } should { it?.function shouldBe "find_by_last_known_call_null" }

        foo { bar { StackTrace.findByLastKnownCallOrNull(String::class, "toString") } }.shouldBeNull()
        foo { bar { StackTrace.findByLastKnownCallOrNull("toString") } }.shouldBeNull()
        foo { bar { StackTrace.findByLastKnownCallOrNull(String::toString) } }.shouldBeNull()
    }

    @Test fun equality() = tests {
        stackTraceElementWithColumn shouldNotBe JvmStackTraceElement("any.package.AnyReceiver", "anyFun", file, 5, null)
        stackTraceElement shouldBe JvmStackTraceElement("any.package.AnyReceiver", "anyFun", file, 5, null)
    }

    @Test fun to_string() = tests {
        stackTraceElementWithColumn.toString() shouldBe "any.package.AnyReceiver.anyFun($file:5:20)"
        stackTraceElementWithNegativeLine.toString() shouldBe "any.package.AnyReceiver.anyFun($file:-5)"
        stackTraceElement.toString() shouldBe "any.package.AnyReceiver.anyFun($file:5)"
    }
}

private val file = "AnyReceiverImpl.kt"
internal val stackTraceElementWithColumn = JvmStackTraceElement("any.package.AnyReceiver", "anyFun", file, 5, 20)
internal val stackTraceElementWithNegativeLine = JvmStackTraceElement("any.package.AnyReceiver", "anyFun", file, -5, null)
internal val stackTraceElement = JvmStackTraceElement("any.package.AnyReceiver", "anyFun", file, 5, null)
