package com.bkahlert.kommons

import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.nio.file.attribute.FileTime
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class TimeOperationsKtTest {

    private val duration = 2.days + 3.hours + 4.minutes + 5.seconds + 6.nanoseconds
    private val javaDuration = duration.toJavaDuration()

    @Test
    fun `should provide now`() = tests {
        Now.instant should {
            it shouldBeLessThanOrEqualTo Instant.now()
            it shouldBeGreaterThanOrEqualTo Instant.now() - 1.seconds
        }
        Now.localTime should {
            it shouldBeLessThanOrEqualTo LocalTime.now()
            it shouldBeGreaterThanOrEqualTo LocalTime.now() - 1.seconds
        }
        Now.localDateTime should {
            it shouldBeLessThanOrEqualTo LocalDateTime.now()
            it shouldBeGreaterThanOrEqualTo LocalDateTime.now() - 1.seconds
        }
        Now.zonedDateTime should {
            it shouldBeLessThanOrEqualTo ZonedDateTime.now()
            it shouldBeGreaterThanOrEqualTo ZonedDateTime.now() - 1.seconds
        }
        Now.offsetDateTime should {
            it shouldBeLessThanOrEqualTo OffsetDateTime.now()
            it shouldBeGreaterThanOrEqualTo OffsetDateTime.now() - 1.seconds
        }
        Now.offsetTime should {
            it shouldBeLessThanOrEqualTo OffsetTime.now()
            it shouldBeGreaterThanOrEqualTo OffsetTime.now() - 1.seconds
        }
        Now.fileTime should {
            it shouldBeLessThanOrEqualTo FileTime.from(Instant.now())
            it shouldBeGreaterThanOrEqualTo FileTime.from(Instant.now()) - 1.seconds
        }
    }

    @Test
    fun `should add`() = tests {
        (Now + duration) should {
            it shouldBeLessThanOrEqualTo Instant.now().plus(javaDuration)
            it shouldBeGreaterThanOrEqualTo Instant.now().plus(javaDuration) - 1.seconds
        }
        (Now.instant + duration) should {
            it shouldBeLessThanOrEqualTo Instant.now().plus(javaDuration)
            it shouldBeGreaterThanOrEqualTo Instant.now().plus(javaDuration) - 1.seconds
        }
        (Now.localTime + duration) should {
            it shouldBeLessThanOrEqualTo LocalTime.now().plus(javaDuration)
            it shouldBeGreaterThanOrEqualTo LocalTime.now().plus(javaDuration) - 1.seconds
        }
        (Now.localDateTime + duration) should {
            it shouldBeLessThanOrEqualTo LocalDateTime.now().plus(javaDuration)
            it shouldBeGreaterThanOrEqualTo LocalDateTime.now().plus(javaDuration) - 1.seconds
        }
        (Now.zonedDateTime + duration) should {
            it shouldBeLessThanOrEqualTo ZonedDateTime.now().plus(javaDuration)
            it shouldBeGreaterThanOrEqualTo ZonedDateTime.now().plus(javaDuration) - 1.seconds
        }
        (Now.offsetDateTime + duration) should {
            it shouldBeLessThanOrEqualTo OffsetDateTime.now().plus(javaDuration)
            it shouldBeGreaterThanOrEqualTo OffsetDateTime.now().plus(javaDuration) - 1.seconds
        }
        (Now.offsetTime + duration) should {
            it shouldBeLessThanOrEqualTo OffsetTime.now().plus(javaDuration)
            it shouldBeGreaterThanOrEqualTo OffsetTime.now().plus(javaDuration) - 1.seconds
        }
        (Now.fileTime + duration) should {
            it shouldBeLessThanOrEqualTo FileTime.from(Instant.now().plus(javaDuration))
            it shouldBeGreaterThanOrEqualTo FileTime.from(Instant.now().plus(javaDuration)) - 1.seconds
        }
    }

    @Test
    fun `should subtract`() = tests {
        (Now - duration) should {
            it shouldBeLessThanOrEqualTo Instant.now().minus(javaDuration)
            it shouldBeGreaterThanOrEqualTo Instant.now().minus(javaDuration) - 1.seconds
        }
        (Now.instant - duration) should {
            it shouldBeLessThanOrEqualTo Instant.now().minus(javaDuration)
            it shouldBeGreaterThanOrEqualTo Instant.now().minus(javaDuration) - 1.seconds
        }
        (Now.localTime - duration) should {
            it shouldBeLessThanOrEqualTo LocalTime.now().minus(javaDuration)
            it shouldBeGreaterThanOrEqualTo LocalTime.now().minus(javaDuration) - 1.seconds
        }
        (Now.localDateTime - duration) should {
            it shouldBeLessThanOrEqualTo LocalDateTime.now().minus(javaDuration)
            it shouldBeGreaterThanOrEqualTo LocalDateTime.now().minus(javaDuration) - 1.seconds
        }
        (Now.zonedDateTime - duration) should {
            it shouldBeLessThanOrEqualTo ZonedDateTime.now().minus(javaDuration)
            it shouldBeGreaterThanOrEqualTo ZonedDateTime.now().minus(javaDuration) - 1.seconds
        }
        (Now.offsetDateTime - duration) should {
            it shouldBeLessThanOrEqualTo OffsetDateTime.now().minus(javaDuration)
            it shouldBeGreaterThanOrEqualTo OffsetDateTime.now().minus(javaDuration) - 1.seconds
        }
        (Now.offsetTime - duration) should {
            it shouldBeLessThanOrEqualTo OffsetTime.now().minus(javaDuration)
            it shouldBeGreaterThanOrEqualTo OffsetTime.now().minus(javaDuration) - 1.seconds
        }
        (Now.fileTime - duration) should {
            it shouldBeLessThanOrEqualTo FileTime.from(Instant.now().minus(javaDuration))
            it shouldBeGreaterThanOrEqualTo FileTime.from(Instant.now().minus(javaDuration)) - 1.seconds
        }
    }

    @Test
    fun `should return FileTime`() {
        val now = Now.instant
        now.toFileTime() shouldBe FileTime.from(now)
    }
}
