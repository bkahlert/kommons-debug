package com.bkahlert.kommons

import java.nio.file.attribute.FileTime
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.Year
import java.time.YearMonth
import java.time.ZonedDateTime
import kotlin.time.Duration
import kotlin.time.toJavaDuration

/** Provider of the current moment in time in various formats. */
public object Now {
    /** Returns the current date and time as an [Instant]. */
    public val instant: Instant get() = Instant.now()

    /** Returns the current time as a [LocalTime]. */
    public val localTime: LocalTime get() = LocalTime.now()

    /** Returns the current date as a [LocalDate]. */
    public val localDate: LocalDate get() = LocalDate.now()

    /** Returns the current date and time as a [LocalDateTime]. */
    public val localDateTime: LocalDateTime get() = LocalDateTime.now()

    /** Returns the current date and time as a [ZonedDateTime]. */
    public val zonedDateTime: ZonedDateTime get() = ZonedDateTime.now()

    /** Returns the current date and time as a [OffsetDateTime]. */
    public val offsetDateTime: OffsetDateTime get() = OffsetDateTime.now()

    /** Returns the current time as a [OffsetTime]. */
    public val offsetTime: OffsetTime get() = OffsetTime.now()

    /** Returns the current year and month as a [YearMonth]. */
    public val yearMonth: YearMonth get() = YearMonth.now()

    /** Returns the current year as a [Year]. */
    public val year: Year get() = Year.now()

    /** Returns the current date and time as a [FileTime]. */
    public val fileTime: FileTime get() = FileTime.from(instant)

    /** Returns the passes seconds since 1970-01-01T00:00:00Z as a [Long]. */
    public val millis: Long get() = instant.toEpochMilli()

    /** Returns the current date and time with the specified amount added. */
    public operator fun plus(duration: Duration): Instant = instant + duration

    /** Returns the current date and time with the specified amount subtracted. */
    public operator fun minus(duration: Duration): Instant = instant - duration

    public override fun toString(): String = "$instant"
}

@Suppress("NOTHING_TO_INLINE")
/** Returns a copy of this [Instant] with the specified [duration] added. */
public inline operator fun Instant.plus(duration: Duration): Instant = this.plus(duration.toJavaDuration())

@Suppress("NOTHING_TO_INLINE")
/** Returns a copy of this [LocalTime] with the specified [duration] added. */
public inline operator fun LocalTime.plus(duration: Duration): LocalTime = this.plus(duration.toJavaDuration())

@Suppress("NOTHING_TO_INLINE")
/** Returns a copy of this [LocalDateTime] with the specified [duration] added. */
public inline operator fun LocalDateTime.plus(duration: Duration): LocalDateTime = this.plus(duration.toJavaDuration())

@Suppress("NOTHING_TO_INLINE")
/** Returns a copy of this [ZonedDateTime] with the specified [duration] added. */
public inline operator fun ZonedDateTime.plus(duration: Duration): ZonedDateTime = this.plus(duration.toJavaDuration())

@Suppress("NOTHING_TO_INLINE")
/** Returns a copy of this [OffsetDateTime] with the specified [duration] added. */
public inline operator fun OffsetDateTime.plus(duration: Duration): OffsetDateTime = this.plus(duration.toJavaDuration())

@Suppress("NOTHING_TO_INLINE")
/** Returns a copy of this [OffsetTime] with the specified [duration] added. */
public inline operator fun OffsetTime.plus(duration: Duration): OffsetTime = this.plus(duration.toJavaDuration())

@Suppress("NOTHING_TO_INLINE")
/** Returns a copy of this [FileTime] with the specified [duration] added. */
public inline operator fun FileTime.plus(duration: Duration): FileTime = FileTime.from(toInstant().plus(duration))

@Suppress("NOTHING_TO_INLINE")
/** Returns a copy of this [Instant] with the specified [duration] subtracted. */
public inline operator fun Instant.minus(duration: Duration): Instant = this.minus(duration.toJavaDuration())

@Suppress("NOTHING_TO_INLINE")
/** Returns a copy of this [LocalTime] with the specified [duration] subtracted. */
public inline operator fun LocalTime.minus(duration: Duration): LocalTime = this.minus(duration.toJavaDuration())

@Suppress("NOTHING_TO_INLINE")
/** Returns a copy of this [LocalDateTime] with the specified [duration] subtracted. */
public inline operator fun LocalDateTime.minus(duration: Duration): LocalDateTime = this.minus(duration.toJavaDuration())

@Suppress("NOTHING_TO_INLINE")
/** Returns a copy of this [ZonedDateTime] with the specified [duration] subtracted. */
public inline operator fun ZonedDateTime.minus(duration: Duration): ZonedDateTime = this.minus(duration.toJavaDuration())

@Suppress("NOTHING_TO_INLINE")
/** Returns a copy of this [OffsetDateTime] with the specified [duration] subtracted. */
public inline operator fun OffsetDateTime.minus(duration: Duration): OffsetDateTime = this.minus(duration.toJavaDuration())

@Suppress("NOTHING_TO_INLINE")
/** Returns a copy of this [OffsetTime] with the specified [duration] subtracted. */
public inline operator fun OffsetTime.minus(duration: Duration): OffsetTime = this.minus(duration.toJavaDuration())

@Suppress("NOTHING_TO_INLINE")
/** Returns a copy of this [FileTime] with the specified [duration] subtracted. */
public inline operator fun FileTime.minus(duration: Duration): FileTime = FileTime.from(toInstant().minus(duration))

@Suppress("NOTHING_TO_INLINE")
/** Returns a [FileTime] representing the same point of time value on the time-line as this instant. */
public fun Instant.toFileTime(): FileTime = FileTime.from(this)
