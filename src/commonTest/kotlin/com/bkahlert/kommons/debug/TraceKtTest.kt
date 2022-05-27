package com.bkahlert.kommons.debug

import com.bkahlert.kommons.debug.Platform.JS
import com.bkahlert.kommons.debug.Platform.JVM
import com.bkahlert.kommons.tests
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import kotlin.test.Test

@Suppress("DEPRECATION")
class TraceTest {

    @Test fun test_no_arguments() = tests {
        buildString {
            "subject".trace(highlight = false, includeCallSite = false, out = this::append)
        } shouldBe """
            ⟨ "subject" ⟩
        """.trimIndent()
    }

    @Test fun test_with_caption() = tests {
        buildString {
            "subject".trace("caption", highlight = false, includeCallSite = false, out = this::append)
        } shouldBe """
            caption ⟨ "subject" ⟩
        """.trimIndent()
    }

    @Test fun test_with_inspect() = tests {
        buildString {
            "subject".trace(highlight = false, includeCallSite = false, out = this::append) { it.length }
        } shouldBe """
            ⟨ "subject" ⟩ { 0x07 }
        """.trimIndent()
    }

    @Test fun test_with_caption_and_inspect() = tests {
        buildString {
            "subject".trace("caption", highlight = false, includeCallSite = false, out = this::append) { it.length }
        } shouldBe """
            caption ⟨ "subject" ⟩ { 0x07 }
        """.trimIndent()
    }

    @Suppress("LongLine")
    @Test fun test_with_highlighting() = tests {
        buildString {
            "subject".trace("caption", highlight = true, includeCallSite = false, out = this::append) { it.length.toString() }
        } shouldBe when (Platform.Current) {
            JS -> """
                <span style="color:#01818F;font-weight:bold;">caption</span> <span style="color:#01818F;font-weight:bold;">⟨</span> <span style="color:#01E6FF;">"subject"</span> <span style="color:#01818F;font-weight:bold;">⟩</span> <span style="color:#01818F;font-weight:bold;">{</span> <span style="color:#01E6FF;">"7"</span> <span style="color:#01818F;font-weight:bold;">}</span>
            """.trimIndent()
            JVM -> """
                [1;36mcaption[0m [1;36m⟨[0m [96m"subject"[0m [1;36m⟩[0m [1;36m{[0m [96m"7"[0m [1;36m}[0m
            """.trimIndent()
        }
    }

    @Test fun test_with_call_site() = tests {
        buildString {
            "subject".trace(highlight = false, out = this::append) { it.length.toString() }
        } should {
            @Suppress("RegExpRedundantEscape")
            when (Platform.Current) {
                JS -> it shouldMatch ".ͭ \\(.*/commons\\.js.*\\) ⟨ \"subject\" ⟩ \\{ \"7\" \\}".toRegex()
                JVM -> it shouldBe """
                    .ͭ (TraceKtTest.kt:62) ⟨ "subject" ⟩ { "7" }
                """.trimIndent()
            }
        }
    }

    @Test fun test_with_custom_render() = tests {
        buildString {
            "subject".trace("caption", highlight = false, includeCallSite = false, render = { ">>> $it <<<" }, out = this::append) { it.length.toString() }
        } shouldBe """
            caption ⟨ >>> subject <<< ⟩ { >>> 7 <<< }
        """.trimIndent()
    }

    @Test fun test_with_multiline() = tests {
        buildString {
            "subject 1\nsubject 2".trace(
                highlight = false,
                includeCallSite = false,
                out = this::append
            )
        } shouldBe """
            ⟨
            subject 1
            subject 2
            ⟩
        """.trimIndent()

        buildString {
            "subject 1\nsubject 2".trace(
                "caption",
                highlight = false,
                includeCallSite = false,
                out = this::append
            )
        } shouldBe """
            caption ⟨
            subject 1
            subject 2
            ⟩
        """.trimIndent()

        buildString {
            "subject 1\nsubject 2".trace(
                "caption",
                highlight = false,
                includeCallSite = false,
                out = this::append
            ) { "inspect" }
        } shouldBe """
            caption ⟨
            subject 1
            subject 2
            ⟩ { "inspect" }
        """.trimIndent()

        buildString {
            "subject".trace(
                "caption",
                highlight = false,
                includeCallSite = false,
                out = this::append
            ) { "inspect 1\ninspect 2" }
        } shouldBe """
            caption ⟨ "subject" ⟩ {
            inspect 1
            inspect 2
            }
        """.trimIndent()

        buildString {
            "subject 1\nsubject 2".trace(
                "caption",
                highlight = false,
                includeCallSite = false,
                out = this::append
            ) { "inspect 1\ninspect 2" }
        } shouldBe """
            caption ⟨
            subject 1
            subject 2
            ⟩ {
            inspect 1
            inspect 2
            }
        """.trimIndent()
    }
}
