package com.bkahlert.kommons

/** Throws an [IllegalArgumentException] if the specified [collection] [isEmpty]. */
@Suppress("NOTHING_TO_INLINE")
public inline fun <T : Collection<*>> requireNotEmpty(collection: T): T = collection.also { require(it.isNotEmpty()) }

/** Throws an [IllegalArgumentException] if the specified [array] [isEmpty]. */
@Suppress("NOTHING_TO_INLINE")
public inline fun <T> requireNotEmpty(array: Array<T>): Array<T> = array.also { require(it.isNotEmpty()) }

/** Throws an [IllegalArgumentException] with the result of calling [lazyMessage] if the specified [collection] [isEmpty]. */
@Suppress("NOTHING_TO_INLINE")
public inline fun <T : Collection<*>> requireNotEmpty(collection: T, lazyMessage: () -> Any): T = collection.also { require(it.isNotEmpty(), lazyMessage) }

/** Throws an [IllegalArgumentException] with the result of calling [lazyMessage] if the specified [array] [isEmpty]. */
@Suppress("NOTHING_TO_INLINE")
public inline fun <T> requireNotEmpty(array: Array<T>, lazyMessage: () -> Any): Array<T> = array.also { require(it.isNotEmpty(), lazyMessage) }


/** Throws an [IllegalStateException] if the specified [collection] [isEmpty]. */
@Suppress("NOTHING_TO_INLINE")
public inline fun <T : Collection<*>> checkNotEmpty(collection: T): T = collection.also { check(it.isNotEmpty()) }

/** Throws an [IllegalStateException] if the specified [array] [isEmpty]. */
@Suppress("NOTHING_TO_INLINE")
public inline fun <T> checkNotEmpty(array: Array<T>): Array<T> = array.also { check(it.isNotEmpty()) }

/** Throws an [IllegalStateException] with the result of calling [lazyMessage] if the specified [collection] [isEmpty]. */
@Suppress("NOTHING_TO_INLINE")
public inline fun <T : Collection<*>> checkNotEmpty(collection: T, lazyMessage: () -> Any): T = collection.also { check(it.isNotEmpty(), lazyMessage) }

/** Throws an [IllegalStateException] with the result of calling [lazyMessage] if the specified [array] [isEmpty]. */
@Suppress("NOTHING_TO_INLINE")
public inline fun <T> checkNotEmpty(array: Array<T>, lazyMessage: () -> Any): Array<T> = array.also { check(it.isNotEmpty(), lazyMessage) }


/** Returns this collection if it [isNotEmpty] or `null`, if it is. */
@Suppress("NOTHING_TO_INLINE")
public inline fun <T : Collection<*>> T.takeIfNotEmpty(): T? = takeIf { it.isNotEmpty() }

/** Returns this array if it [isNotEmpty] or `null`, if it is. */
@Suppress("NOTHING_TO_INLINE")
public inline fun <T> Array<T>.takeIfNotEmpty(): Array<T>? = takeIf { it.isNotEmpty() }

/** Returns this collection if it [isNotEmpty] or `null`, if it is. */
@Suppress("NOTHING_TO_INLINE")
public inline fun <T : Collection<*>> T.takeUnlessEmpty(): T? = takeUnless { it.isEmpty() }

/** Returns this array if it [isNotEmpty] or `null`, if it is. */
@Suppress("NOTHING_TO_INLINE")
public inline fun <T> Array<T>.takeUnlessEmpty(): Array<T>? = takeUnless { it.isEmpty() }


/** The first element of this collection. Throws a [NoSuchElementException] if this collection is empty. */
public inline val <T> Iterable<T>.head: T get() = first()

/** The first element of this collection or `null` if this collection is empty. */
public inline val <T> Iterable<T>.headOrNull: T? get() = firstOrNull()

/** A list containing all but the first element of this collection. */
public inline val <T> Iterable<T>.tail: List<T> get() = drop(1)
