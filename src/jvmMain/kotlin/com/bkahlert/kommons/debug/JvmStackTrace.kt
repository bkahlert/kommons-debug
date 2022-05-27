package com.bkahlert.kommons.debug

import com.bkahlert.kommons.debug.JvmStackTraceElement.Companion.FunctionMangleRegex
import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KFunction
import kotlin.reflect.full.instanceParameter

/** Representation of a single element of a [StackTrace] on [Platform.JVM]. */
public data class JvmStackTraceElement(
    override val receiver: String,
    override val function: String,
    override val file: String?,
    override val line: Int,
    override val column: Int?,
) : StackTraceElement {
    public constructor(native: java.lang.StackTraceElement) : this(native.className, native.methodName, native.fileName, native.lineNumber, null)

    override fun toString(): String = "$receiver.$function($file:$line${column?.let { ":$it" } ?: ""})"

    public companion object {

        public val FunctionMangleRegex: Regex = "\\$.*$".toRegex()
    }
}

public val StackTraceElement.demangledFunction: String? get() = function?.replace(FunctionMangleRegex, "")

/** The [Class] containing the execution point represented by this element. */
public val java.lang.StackTraceElement.`class`: Class<*> get() = Class.forName(className)

/** The [KClass] containing the execution point represented by this element. */
public val java.lang.StackTraceElement.kClass: KClass<*> get() = `class`.kotlin

/** The [Class] containing the execution point represented by this element. */
public val StackTraceElement.`class`: Class<*> get() = Class.forName(receiver)

/** The [KClass] containing the execution point represented by this element. */
public val StackTraceElement.kClass: KClass<*> get() = `class`.kotlin

/** The method containing the execution point represented by this element. */
public val java.lang.StackTraceElement.method: Method get() = `class`.declaredMethods.single { it.name == methodName }

/** The method containing the execution point represented by this element. */
public val StackTraceElement.method: Method get() = `class`.declaredMethods.single { it.name == function }

/** Gets the current [StackTrace]. */
@Suppress("NOTHING_TO_INLINE") // = avoid impact on stack trace
public actual inline fun StackTrace.Companion.get(): StackTrace =
    Thread.currentThread().stackTrace
        .asSequence()
        .dropWhile { it.className == Thread::class.qualifiedName }
        .map { JvmStackTraceElement(it) }
        .let { StackTrace(it) }

/**
 * Finds the [StackTraceElement] that represents the caller
 * invoking the [StackTraceElement] matching a call to the specified [receiver] and [function].
 */
@Suppress("NOTHING_TO_INLINE") // = avoid impact on stack trace
public inline fun StackTrace.Companion.findByLastKnownCallOrNull(receiver: String, function: String): StackTraceElement? {
    var skipInvoke = false
    val demangledFunction = function.replace(FunctionMangleRegex, "")
    return findOrNull {
        if (it.receiver == receiver && it.demangledFunction == demangledFunction) {
            skipInvoke = true
            true
        } else {
            if (skipInvoke) it.function == "invoke" else false
        }
    }
}

/**
 * Finds the [StackTraceElement] that represents the caller
 * invoking the [StackTraceElement] matching a call to the specified [receiver] and [function].
 */
@Suppress("NOTHING_TO_INLINE") // = avoid impact on stack trace
public inline fun StackTrace.Companion.findByLastKnownCallOrNull(receiver: KClassifier, function: String): StackTraceElement? =
    findByLastKnownCallOrNull(receiver.toString(), function)

/**
 * Finds the [StackTraceElement] that represents the caller
 * invoking the [StackTraceElement] matching a call to the specified [function].
 */
@Suppress("NOTHING_TO_INLINE") // = avoid impact on stack trace
public actual inline fun StackTrace.Companion.findByLastKnownCallOrNull(function: String): StackTraceElement? {
    var skipInvoke = false
    val demangledFunction = function.replace(FunctionMangleRegex, "")
    return findOrNull {
        if (it.demangledFunction == demangledFunction) {
            skipInvoke = true
            true
        } else {
            if (skipInvoke) it.function == "invoke" else false
        }
    }
}

/**
 * Finds the [StackTraceElement] that represents the caller
 * invoking the [StackTraceElement] matching a call to the specified [function].
 */
@Suppress("NOTHING_TO_INLINE") // = avoid impact on stack trace
public actual inline fun StackTrace.Companion.findByLastKnownCallOrNull(function: KFunction<*>): StackTraceElement? =
    function.instanceParameter?.type?.classifier
        ?.let { findByLastKnownCallOrNull(it, function.name) }
        ?: findByLastKnownCallOrNull(function.name)
