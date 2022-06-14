package com.bkahlert.kommons

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
    fun today() = tests {
        Today should {
            it.fullYear shouldBe Now.fullYear
            it.month shouldBe Now.month
            it.date shouldBe Now.date
            it.hours shouldBe 0
            it.minutes shouldBe 0
            it.seconds shouldBe 0
            it.milliseconds shouldBe 0
        }
    }

    @Test
    fun yesterday() = tests {
        Yesterday should {
            it.fullYear shouldBe Now.fullYear
            it.month shouldBe Now.month
            it.date shouldBe Now.date - 1
            it.hours shouldBe 0
            it.minutes shouldBe 0
            it.seconds shouldBe 0
            it.milliseconds shouldBe 0
        }
    }

    @Test
    fun tomorrow() = tests {
        Tomorrow should {
            it.fullYear shouldBe Now.fullYear
            it.month shouldBe Now.month
            it.date shouldBe Now.date + 1
            it.hours shouldBe 0
            it.minutes shouldBe 0
            it.seconds shouldBe 0
            it.milliseconds shouldBe 0
        }
    }

    @Test
    fun timestamp() = tests {
        Timestamp should {
            it shouldBeLessThanOrEqualTo Date().getTime()
            it shouldBeGreaterThanOrEqualTo Date().getTime() - 1
        }
    }

    @Test
    fun components() = tests {
        Date("August 19, 1975 23:15:30 GMT-11:00") should {
            it.time shouldBe it.getTime()
            it.timezoneOffset shouldBe it.getTimezoneOffset().minutes

            it.fullYear shouldBe it.getFullYear()
            it.month shouldBe it.getMonth()
            it.date shouldBe it.getDate()
            it.day shouldBe it.getDay()
            it.hours shouldBe it.getHours()
            it.minutes shouldBe it.getMinutes()
            it.seconds shouldBe it.getSeconds()
            it.milliseconds shouldBe it.getMilliseconds()

            it.utcFullYear shouldBe it.getUTCFullYear()
            it.utcMonth shouldBe it.getUTCMonth()
            it.utcDate shouldBe it.getUTCDate()
            it.utcDay shouldBe it.getUTCDay()
            it.utcHours shouldBe it.getUTCHours()
            it.utcMinutes shouldBe it.getUTCMinutes()
            it.utcSeconds shouldBe it.getUTCSeconds()
            it.utcMilliseconds shouldBe it.getUTCMilliseconds()
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
