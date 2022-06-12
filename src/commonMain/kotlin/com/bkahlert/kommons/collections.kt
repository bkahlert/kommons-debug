package com.bkahlert.kommons

/** Throws an [IllegalArgumentException] if this collection [isEmpty]. */
public fun <T : Collection<*>> T.requireNotEmpty(): T = also { require(it.isNotEmpty()) }

/** Throws an [IllegalArgumentException] if this array [isEmpty]. */
public fun <T> Array<T>.requireNotEmpty(): Array<T> = also { require(it.isNotEmpty()) }

/** Throws an [IllegalArgumentException] with the result of calling [lazyMessage] if this collection [isEmpty]. */
public fun <T : Collection<*>> T.requireNotEmpty(lazyMessage: () -> Any): T = also { require(it.isNotEmpty(), lazyMessage) }

/** Throws an [IllegalArgumentException] with the result of calling [lazyMessage] if this array [isEmpty]. */
public fun <T> Array<T>.requireNotEmpty(lazyMessage: () -> Any): Array<T> = also { require(it.isNotEmpty(), lazyMessage) }


/** Throws an [IllegalStateException] if this collection [isEmpty]. */
public fun <T : Collection<*>> T.checkNotEmpty(): T = also { check(it.isNotEmpty()) }

/** Throws an [IllegalStateException] if this array [isEmpty]. */
public fun <T> Array<T>.checkNotEmpty(): Array<T> = also { check(it.isNotEmpty()) }

/** Throws an [IllegalStateException] with the result of calling [lazyMessage] if this collection [isEmpty]. */
public fun <T : Collection<*>> T.checkNotEmpty(lazyMessage: () -> Any): T = also { check(it.isNotEmpty(), lazyMessage) }

/** Throws an [IllegalStateException] with the result of calling [lazyMessage] if this array [isEmpty]. */
public fun <T> Array<T>.checkNotEmpty(lazyMessage: () -> Any): Array<T> = also { check(it.isNotEmpty(), lazyMessage) }


/** Returns this collection if it [isNotEmpty] or `null`, if it is. */
public fun <T : Collection<*>> T.takeIfNotEmpty(): T? = takeIf { it.isNotEmpty() }

/** Returns this array if it [isNotEmpty] or `null`, if it is. */
public fun <T> Array<T>.takeIfNotEmpty(): Array<T>? = takeIf { it.isNotEmpty() }

/** Returns this collection if it [isNotEmpty] or `null`, if it is. */
public fun <T : Collection<*>> T.takeUnlessEmpty(): T? = takeUnless { it.isEmpty() }

/** Returns this array if it [isNotEmpty] or `null`, if it is. */
public fun <T> Array<T>.takeUnlessEmpty(): Array<T>? = takeUnless { it.isEmpty() }
