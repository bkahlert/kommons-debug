@file:Suppress("DEPRECATION")

package com.bkahlert.kommons.debug

import com.bkahlert.kommons.test.test
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldMatch
import kotlin.test.Test

class InspectJsKtTest {

    @Test fun inspect_js() = test {
        buildString {
            "subject".inspectJs(out = this::append)
        } should {
            it shouldMatch "subject".toRegex()
        }
    }
}
