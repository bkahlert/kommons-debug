package com.bkahlert.kommons.debug

import com.bkahlert.kommons.tests
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

class JsStackTraceTest {

    @Test fun find_by_last_known_call_null() = tests {
        foo { bar { StackTrace.findByLastKnownCallOrNull("bar") } } should { it?.demangledFunction shouldBe "foo" }
        foo { bar { StackTrace.findByLastKnownCallOrNull(::bar) } } should { it?.demangledFunction shouldBe "foo" }

        foo { bar { StackTrace.findByLastKnownCallOrNull("foo") } } should { it?.demangledFunction shouldBe "find_by_last_known_call_null" }
        foo { bar { StackTrace.findByLastKnownCallOrNull(::foo) } } should { it?.demangledFunction shouldBe "find_by_last_known_call_null" }

        foo { bar { StackTrace.findByLastKnownCallOrNull("toString") } }.shouldBeNull()
        foo { bar { StackTrace.findByLastKnownCallOrNull(String::toString) } }.shouldBeNull()
    }

    @Test fun parse() = tests {
        JsStackTraceElement.parseOrNull("").shouldBeNull()
        JsStackTraceElement.parseOrNull("AnyReceiver.anyFun_hash_k\$ ($file:5:20)") shouldBe stackTraceElementWithReceiverAndFunction
        JsStackTraceElement.parseOrNull("AnyReceiver.<anonymous> ($file:5:20)") shouldBe stackTraceElementWithReceiverAndAnonymousFunction
        JsStackTraceElement.parseOrNull("anyFun ($file:5:20)") shouldBe stackTraceElementWithFunction
        JsStackTraceElement.parseOrNull("$file:5:20") shouldBe stackTraceElement
    }

    @Test fun equality() = tests {
        stackTraceElementWithFunction shouldNotBe JsStackTraceElement(null, null, file, 5, 20)
        stackTraceElement shouldBe JsStackTraceElement(null, null, file, 5, 20)
    }

    @Test fun to_string() = tests {
        stackTraceElementWithReceiverAndFunction.toString() shouldBe "AnyReceiver.anyFun_hash_k\$ ($file:5:20)"
        stackTraceElementWithReceiverAndAnonymousFunction.toString() shouldBe "AnyReceiver.<anonymous> ($file:5:20)"
        stackTraceElementWithFunction.toString() shouldBe "anyFun ($file:5:20)"
        stackTraceElement.toString() shouldBe "$file:5:20"
    }
}

private val file = "http://localhost:9876/absolute/your/path/commons.js?09b8"
internal val stackTraceElementWithReceiverAndFunction = JsStackTraceElement("AnyReceiver", "anyFun_hash_k\$", file, 5, 20)
internal val stackTraceElementWithReceiverAndAnonymousFunction = JsStackTraceElement("AnyReceiver", "<anonymous>", file, 5, 20)
internal val stackTraceElementWithFunction = JsStackTraceElement(null, "anyFun", file, 5, 20)
internal val stackTraceElement = JsStackTraceElement(null, null, file, 5, 20)
