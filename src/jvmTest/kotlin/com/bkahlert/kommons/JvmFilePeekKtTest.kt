package com.bkahlert.kommons

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class JvmFilePeekKtTest {

    @Test fun get_caller_file_info() = tests {
        FilePeekMPP.getCallerFileInfo(
            try {
                throw RuntimeException()
            } catch (e: Throwable) {
                e.stackTrace.first()
            }
        ).shouldNotBeNull() should {
            it.lineNumber shouldBe 13
            it.sourceFileName shouldBe "/Users/bkahlert/Development/com.bkahlert/kommons-debug/src/jvmTest/kotlin/com/bkahlert/kommons/JvmFilePeekKtTest.kt"
            it.line shouldBe "throw RuntimeException()"
            it.methodName shouldBe "get_caller_file_info"
        }
    }

    @Test fun lambda_body_get_or_null() = tests {
        fun <R> call(block: () -> R): R = block()

        kotlin.runCatching {
            call { throw RuntimeException() }
        }.exceptionOrNull()?.stackTrace?.first()?.let {
            LambdaBody.getOrNull(it)
        } shouldBe "throw RuntimeException()"
    }
}
