package com.bkahlert.kommons

import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlin.js.Date
import kotlin.test.Test
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

class TimeOperationsKtTest {

    private val duration = 2.days + 3.hours + 4.minutes + 5.seconds + 6.nanoseconds

    @Test
    fun now() = tests {
        Now should {
            it.compareTo(Now) shouldBeLessThanOrEqualTo 0
            it.compareTo(Now - 1.seconds) shouldBeGreaterThanOrEqualTo 0
        }
    }

    @Test
    fun components() = tests {
        Date("August 19, 1975 23:15:30 GMT-11:00") should {
            it.time shouldBeGreaterThan 0.0
            it.timezoneOffset shouldBe (-1 * 60).minutes

            it.fullYear shouldBe 1975
            it.month shouldBe 7
            it.date shouldBe 20
            it.day shouldBe 3
            it.hours shouldBe 11
            it.minutes shouldBe 15
            it.seconds shouldBe 30
            it.milliseconds shouldBe 0

            it.utcFullYear shouldBe 1975
            it.utcMonth shouldBe 7
            it.utcDate shouldBe 20
            it.utcDay shouldBe 3
            it.utcHours shouldBe 10
            it.utcMinutes shouldBe 15
            it.utcSeconds shouldBe 30
            it.utcMilliseconds shouldBe 0
        }
    }

    @Test
    fun add() = tests {
        (Date("August 19, 1975 23:15:30 GMT-11:00") + duration).time shouldBe Date("Fri Aug 22 1975 02:19:35 GMT-11:00").time
    }

    @Test
    fun subtract() = tests {
        (Date("August 19, 1975 23:15:30 GMT-11:00") - duration).time shouldBe Date("Mon Aug 18 1975 08:11:25 GMT+0100").time
    }

    @Test
    fun subtract_date() = tests {
        (Now - Now) should {
            it shouldBeLessThanOrEqualTo ZERO
            it shouldBeGreaterThanOrEqualTo ZERO - 1.seconds
        }
    }
}
