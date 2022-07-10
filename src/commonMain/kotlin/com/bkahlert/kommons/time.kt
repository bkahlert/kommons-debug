package com.bkahlert.kommons

import kotlin.time.Duration

/** An instantaneous point on the time-line. */
public expect class Instant

/** A date without a time-zone in the ISO-8601 calendar system, such as 2007-12-03. */
public expect class LocalDate


/** The current date and time. */
public expect val Now: Instant

/** The current date. */
public expect val Today: LocalDate

/** The current date but 1 day in the past. */
public expect val Yesterday: LocalDate

/** The current date but 1 day in the future. */
public expect val Tomorrow: LocalDate

/** The passed seconds since 1970-01-01T00:00:00Z. */
public expect val Timestamp: Long


/** Returns a copy of this [Instant] with the specified [duration] added. */
public expect operator fun Instant.plus(duration: Duration): Instant

/** Returns a copy of this [Instant] with the specified [duration] subtracted. */
public expect operator fun Instant.minus(duration: Duration): Instant

/** Returns the [Duration] between this and the specified [other]. */
public expect operator fun Instant.minus(other: Instant): Duration


/** Returns a copy of this [LocalDate] with the whole days of the specified [duration] added. */
public expect operator fun LocalDate.plus(duration: Duration): LocalDate

/** Returns a copy of this [LocalDate] with the whole days of the specified [duration] subtracted. */
public expect operator fun LocalDate.minus(duration: Duration): LocalDate

/** Returns the [Duration] between this and the specified [other]. */
public expect operator fun LocalDate.minus(other: LocalDate): Duration


/** The hour of the month according to universal time. */
public expect val Instant.utcHours: Int

/** The minutes of the month according to universal time. */
public expect val Instant.utcMinutes: Int
