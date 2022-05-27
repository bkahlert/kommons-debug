package com.bkahlert.kommons.debug

import kotlin.reflect.KFunction

/** Representation of a stack trace. */
public class StackTrace(elements: Sequence<StackTraceElement>) : Sequence<StackTraceElement> by elements {
    public companion object {

        /**
         * Finds the [StackTraceElement] that immediately follows the one
         * the specified [predicate] stops returning `true` for the first time.
         *
         * In other words:
         * - The [predicate] is used to drop irrelevant calls for as long as it returns `false`.
         * - As soon as it responds `true` calls that are expected to exist are dropped.
         * - Finally, the next element which represents the caller of the last matched call is returned.
         */
        @Suppress("NOTHING_TO_INLINE") // = avoid impact on stack trace
        public inline fun findOrNull(crossinline predicate: (StackTraceElement) -> Boolean): StackTraceElement? =
            get().dropWhile { !predicate(it) }.dropWhile { predicate(it) }.firstOrNull()

        /**
         * Finds the [StackTraceElement] that immediately follows the one
         * the specified [predicate] stops returning `true` for the first time.
         *
         * In other words:
         * - The [predicate] is used to drop irrelevant calls for as long as it returns `false`.
         * - As soon as it responds `true` calls that are expected to exist are dropped.
         * - Finally, the next element which represents the caller of the last matched call is returned.
         */
        @Suppress("NOTHING_TO_INLINE") // = avoid impact on stack trace
        public inline fun find(crossinline predicate: (StackTraceElement) -> Boolean): StackTraceElement =
            findOrNull(predicate) ?: throw NoSuchElementException()
    }
}

/** Representation of a single element of a [StackTrace]. */
public interface StackTraceElement {

    /** Receiver of the [function] call. */
    public val receiver: String?

    /** Name of the invoked function. */
    public val function: String?

    /** File in which the invocation takes place. */
    public val file: String?

    /** Line in which the invocation takes place. */
    public val line: Int

    /** Column in which the invocation takes place. */
    public val column: Int?
}

/** Gets the current [StackTrace]. */
public expect inline fun StackTrace.Companion.get(): StackTrace


/**
 * Finds the [StackTraceElement] that represents the caller
 * invoking the [StackTraceElement] matching a call to the specified [function].
 */
public expect inline fun StackTrace.Companion.findByLastKnownCallOrNull(function: String): StackTraceElement?

/**
 * Finds the [StackTraceElement] that represents the caller
 * invoking the [StackTraceElement] matching a call to the specified [function].
 */
public expect inline fun StackTrace.Companion.findByLastKnownCallOrNull(function: KFunction<*>): StackTraceElement?
