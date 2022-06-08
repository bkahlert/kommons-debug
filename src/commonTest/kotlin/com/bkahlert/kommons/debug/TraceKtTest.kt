package com.bkahlert.kommons.debug

import com.bkahlert.kommons.Current
import com.bkahlert.kommons.Platform
import com.bkahlert.kommons.Platform.JS
import com.bkahlert.kommons.Platform.JVM
import com.bkahlert.kommons.debug.CustomToString.Ignore
import com.bkahlert.kommons.debug.Typing.SimplyTyped
import com.bkahlert.kommons.tests
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import kotlin.test.Test

@Suppress("DEPRECATION")
class TraceTest {

    @Test fun trace_with_no_arguments() = tests {
        buildString {
            "subject".trace(highlight = false, includeCallSite = false, out = this::append)
        } shouldBe """
            ⟨ "subject" ⟩
        """.trimIndent()
    }

    @Test fun trace_with_caption() = tests {
        buildString {
            "subject".trace("caption", highlight = false, includeCallSite = false, out = this::append)
        } shouldBe """
            caption ⟨ "subject" ⟩
        """.trimIndent()
    }

    @Test fun trace_with_inspect() = tests {
        buildString {
            "subject".trace(highlight = false, includeCallSite = false, out = this::append) { length }
        } shouldBe """
            ⟨ "subject" ⟩ { 0x07 }
        """.trimIndent()
    }

    @Test fun trace_with_caption_and_inspect() = tests {
        buildString {
            "subject".trace("caption", highlight = false, includeCallSite = false, out = this::append) { length }
        } shouldBe """
            caption ⟨ "subject" ⟩ { 0x07 }
        """.trimIndent()
    }

    @Suppress("LongLine")
    @Test fun trace_with_highlighting() = tests {
        buildString {
            "subject".trace("caption", highlight = true, includeCallSite = false, out = this::append) { length.toString() }
        } shouldBe when (Platform.Current) {
            JS -> """
                <span style="color:#01818F;font-weight:bold;">caption</span> <span style="color:#01818F;font-weight:bold;">⟨</span> <span style="color:#01E6FF;">"subject"</span> <span style="color:#01818F;font-weight:bold;">⟩</span> <span style="color:#01818F;font-weight:bold;">{</span> <span style="color:#01E6FF;">"7"</span> <span style="color:#01818F;font-weight:bold;">}</span>
            """.trimIndent()
            JVM -> """
                [1;36mcaption[0m [1;36m⟨[0m [96m"subject"[0m [1;36m⟩[0m [1;36m{[0m [96m"7"[0m [1;36m}[0m
            """.trimIndent()
        }
    }

    @Test fun trace_with_call_site() = tests {
        buildString {
            "subject".trace(highlight = false, out = this::append) { length.toString() }
        } should {
            @Suppress("RegExpRedundantEscape")
            when (Platform.Current) {
                JS -> it shouldMatch ".ͭ \\(.*/commons\\.js.*\\) ⟨ \"subject\" ⟩ \\{ \"7\" \\}".toRegex()
                JVM -> it shouldBe """
                    .ͭ (TraceKtTest.kt:66) ⟨ "subject" ⟩ { "7" }
                """.trimIndent()
            }
        }
    }

    @Test fun trace_with_custom_render() = tests {
        buildString {
            "subject".trace("caption", highlight = false, includeCallSite = false, render = { ">>> $it <<<" }, out = this::append) { length.toString() }
        } shouldBe """
            caption ⟨ >>> subject <<< ⟩ { >>> 7 <<< }
        """.trimIndent()
    }

    @Test fun trace_with_multiline() = tests {
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

    @Test fun inspect() = tests {
        buildString {
            "subject".inspect(
                highlight = false,
                includeCallSite = false,
                out = this::append
            ) { length.toString() }
        } shouldBe buildString {
            "subject".trace(
                highlight = false,
                includeCallSite = false,
                render = { it.render(typing = SimplyTyped, customToString = Ignore) },
                out = this::append
            ) { length.toString() }
        }
    }

    @Test fun inspect_with_call_site() = tests {
        buildString {
            "subject".inspect(highlight = false, out = this::append)
        } should {
            @Suppress("RegExpRedundantEscape")
            when (Platform.Current) {
                JS -> it shouldMatch ".ͭ \\(.*/commons\\.js.*\\) ⟨ !String \"subject\" ⟩".toRegex()
                JVM -> it shouldBe """
                    .ͭ (TraceKtTest.kt:179) ⟨ !String "subject" ⟩
                """.trimIndent()
            }
        }
    }
}
