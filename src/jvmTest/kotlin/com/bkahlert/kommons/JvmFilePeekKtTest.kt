package com.bkahlert.kommons

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.paths.shouldExist
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class JvmFilePeekKtTest {

    @Test fun get_caller_file_info() = tests {
        FilePeekMPP.getCallerFileInfo(
            try {
                throw RuntimeException()
            } catch (e: Throwable) {
                e.stackTrace.first()
            }
        ).shouldNotBeNull() should {
            it.lineNumber shouldBe 16
            it.sourceFileName should { name ->
                name shouldEndWith "src/jvmTest/kotlin/com/bkahlert/kommons/JvmFilePeekKtTest.kt"
                Paths.get(name).shouldExist()
            }
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
