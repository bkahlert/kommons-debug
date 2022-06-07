package com.bkahlert.kommons

import kotlin.js.Date
import kotlin.math.absoluteValue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

/** The current date and time. */
public inline val Now: Date get() = Date()

/** The number of milliseconds since [ECMAScript epoch](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Date#the_ecmascript_epoch_and_timestamps). */
public inline val Date.time: Double get() = getTime()

/** The difference between this date as evaluated in the UTC time zone, and the same date as evaluated in the local time zone. */
public inline val Date.timezoneOffset: Duration get() = getTimezoneOffset().minutes

/** The year according to local time. */
public inline val Date.fullYear: Int get() = getFullYear()

/** The year according to universal time. */
public inline val Date.utcFullYear: Int get() = getUTCFullYear()

/** The month according to local time, where `0` represents January. */
public inline val Date.month: Int get() = getMonth()

/** The month according to universal time, where `0` represents January. */
public inline val Date.utcMonth: Int get() = getUTCMonth()

/** The day of the month according to local time. */
public inline val Date.date: Int get() = getDate()

/** The day of the month universal to local time. */
public inline val Date.utcDate: Int get() = getUTCDate()

/** The day of the week according to local time, where `0` represents Sunday. */
public inline val Date.day: Int get() = getDay()

/** The day of the week according to universal time, where `0` represents Sunday. */
public inline val Date.utcDay: Int get() = getUTCDay()

/** The hour of the month according to local time. */
public inline val Date.hours: Int get() = getHours()

/** The hour of the month according to universal time. */
public inline val Date.utcHours: Int get() = getUTCHours()

/** The minutes of the month according to local time. */
public inline val Date.minutes: Int get() = getMinutes()

/** The minutes of the month according to universal time. */
public inline val Date.utcMinutes: Int get() = getUTCMinutes()

/** The seconds according to local time. */
public inline val Date.seconds: Int get() = getSeconds()

/** The seconds according to universal time. */
public inline val Date.utcSeconds: Int get() = getUTCSeconds()

/** The milliseconds according to local time. */
public inline val Date.milliseconds: Int get() = getMilliseconds()

/** The milliseconds according to universal time. */
public inline val Date.utcMilliseconds: Int get() = getUTCMilliseconds()

/** The current date and time. */
public inline operator fun Date.compareTo(other: Date): Int = time.compareTo(other.time)


/** Returns a copy of this [Date] with the specified [duration] added. */
public inline operator fun Date.plus(duration: Duration): Date = duration.toComponents { days, hours, minutes, seconds, nanoseconds ->
    val offset = getTimezoneOffset()
    val offsetHours = offset / 60
    val offsetMinutes = offset.mod(60)
    Date(
        buildString {
            append(this@plus.fullYear)
            append('-')
            append((this@plus.month + 1).toString().padStart(2, '0'))
            append('-')
            append((this@plus.date + days).toString().padStart(2, '0'))
            append('T')
            append((this@plus.hours - (if (offsetHours >= 0) 0 else 12) + hours).toString().padStart(2, '0'))
            append(':')
            append((this@plus.minutes + minutes).toString().padStart(2, '0'))
            append(':')
            append((this@plus.seconds + seconds).toString().padStart(2, '0'))
            append('.')
            append((this@plus.milliseconds + (nanoseconds / 1_000_000)))
            if (offsetHours >= 0) {
                append('+')
                append(offsetHours.toString().padStart(2, '0'))
                append(offsetMinutes.toString().padStart(2, '0'))
            } else {
                append('-')
                append((12 - offsetHours.absoluteValue).toString().padStart(2, '0'))
                append(offsetMinutes.toString().padStart(2, '0'))
            }
        }
    )
}

/** Returns a copy of this [Date] with the specified [duration] subtracted. */
public inline operator fun Date.minus(duration: Duration): Date = Date(getTime().toLong() - duration.inWholeMilliseconds)

/** Returns the [Duration] between this and the specified [date]. */
public inline operator fun Date.minus(date: Date): Duration = (getTime().toLong() - date.getTime().toLong()).milliseconds
