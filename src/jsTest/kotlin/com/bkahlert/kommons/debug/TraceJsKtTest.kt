@file:Suppress("DEPRECATION")

package com.bkahlert.kommons.debug

import com.bkahlert.kommons.tests
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldMatch
import kotlin.test.Test

class TraceJsKtTest {

    @Test fun trace_js() = tests {
        buildString {
            "subject".traceJs(out = this::append)
        } should {
            it shouldMatch "⟨ \"subject\" ⟩".toRegex()
        }
    }
}
