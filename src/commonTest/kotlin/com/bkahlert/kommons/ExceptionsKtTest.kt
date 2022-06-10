package com.bkahlert.kommons

import io.kotest.inspectors.forAll
import kotlin.test.Test

class ExceptionsKtTest {

    @Test
    fun root_cause() = tests {
        listOf(
            IllegalArgumentException("error message"),
            IllegalStateException(IllegalArgumentException("error message")),
            RuntimeException(IllegalStateException(IllegalArgumentException("error message"))),
            Error(RuntimeException(IllegalStateException(IllegalArgumentException("error message")))),
            RuntimeException(Error(RuntimeException(IllegalStateException(IllegalArgumentException("error message"))))),
        ).forAll { ex ->
            ex.shouldHaveRootCauseInstanceOf<IllegalArgumentException>()
            ex.shouldHaveRootCauseMessage("error message")
        }
    }
}
