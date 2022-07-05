package com.bkahlert.kommons

import com.bkahlert.kommons.test.test
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

class SemanticVersionTest {

    @Test fun parse() = test {
        SemanticVersion.parse("1.10.0-dev.11.uncommitted+refactor.kommons.2e94661") should {
            it.major shouldBe 1
            it.minor shouldBe 10
            it.patch shouldBe 0
            it.preRelease shouldBe "dev.11.uncommitted"
            it.build shouldBe "refactor.kommons.2e94661"
        }

        SemanticVersion.parse("1.10.0-dev.11.uncommitted") should {
            it.major shouldBe 1
            it.minor shouldBe 10
            it.patch shouldBe 0
            it.preRelease shouldBe "dev.11.uncommitted"
            it.build shouldBe null
        }

        SemanticVersion.parse("1.10.0+refactor.kommons.2e94661") should {
            it.major shouldBe 1
            it.minor shouldBe 10
            it.patch shouldBe 0
            it.preRelease shouldBe null
            it.build shouldBe "refactor.kommons.2e94661"
        }

        SemanticVersion.parse("1.10.0") should {
            it.major shouldBe 1
            it.minor shouldBe 10
            it.patch shouldBe 0
            it.preRelease shouldBe null
            it.build shouldBe null
        }
    }

    @Test fun equality() = test {
        SemanticVersion(1, 10, 0, "dev", "2e94661") shouldBe SemanticVersion(1, 10, 0, "dev", "2e94661")
        SemanticVersion(1, 10, 0, "dev", "2e94661") shouldNotBe SemanticVersion(1, 20, 0, "dev")
    }

    @Test fun to_string() = test {
        SemanticVersion(1, 10, 0, "dev", "2e94661").toString() shouldBe "1.10.0-dev+2e94661"
        SemanticVersion(1, 10, 0, "dev", build = null).toString() shouldBe "1.10.0-dev"
        SemanticVersion(1, 10, 0, preRelease = null, "2e94661").toString() shouldBe "1.10.0+2e94661"
        SemanticVersion(1, 10, 0, preRelease = null, build = null).toString() shouldBe "1.10.0"
    }
}
