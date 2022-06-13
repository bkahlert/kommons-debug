package com.bkahlert.kommons

import com.bkahlert.kommons.debug.Compression.Always
import com.bkahlert.kommons.debug.Typing.Untyped
import com.bkahlert.kommons.debug.getOrNull
import com.bkahlert.kommons.debug.properties
import com.bkahlert.kommons.debug.render
import com.bkahlert.kommons.debug.renderType
import kotlin.random.Random
import kotlin.reflect.KProperty

/**
 * Returns `true` if this character sequence contains any of the specified [others] as a substring.
 *
 * @param ignoreCase `true` to ignore character case when comparing strings. By default `false`.
 */
public fun <T : CharSequence> CharSequence.containsAny(others: Iterable<T>, ignoreCase: Boolean = false): Boolean =
    others.any { contains(it, ignoreCase = ignoreCase) }

/**
 * Returns `true` if this character sequence contains any of the specified [others] as a substring.
 *
 * @param ignoreCase `true` to ignore character case when comparing strings. By default `false`.
 */
public fun <T : CharSequence> CharSequence.containsAny(vararg others: T, ignoreCase: Boolean = false): Boolean =
    others.any { contains(it, ignoreCase = ignoreCase) }


/** Throws an [IllegalArgumentException] if this character sequence [isEmpty]. */
public fun CharSequence.requireNotEmpty(): CharSequence = also { require(it.isNotEmpty()) }

/** Throws an [IllegalArgumentException] if this string [isEmpty]. */
public fun String.requireNotEmpty(): String = also { require(it.isNotEmpty()) }

/** Throws an [IllegalArgumentException] with the result of calling [lazyMessage] if this character sequence [isEmpty]. */
public fun CharSequence.requireNotEmpty(lazyMessage: () -> Any): CharSequence = also { require(it.isNotEmpty(), lazyMessage) }

/** Throws an [IllegalArgumentException] with the result of calling [lazyMessage] if this string [isEmpty]. */
public fun String.requireNotEmpty(lazyMessage: () -> Any): String = also { require(it.isNotEmpty(), lazyMessage) }

/** Throws an [IllegalArgumentException] if this character sequence [isBlank]. */
public fun CharSequence.requireNotBlank(): CharSequence = also { require(isNotBlank()) }

/** Throws an [IllegalArgumentException] if this string [isBlank]. */
public fun String.requireNotBlank(): String = also { require(isNotBlank()) }

/** Throws an [IllegalArgumentException] with the result of calling [lazyMessage] if this character sequence [isBlank]. */
public fun CharSequence.requireNotBlank(lazyMessage: () -> Any): CharSequence = also { require(it.isNotBlank(), lazyMessage) }

/** Throws an [IllegalArgumentException] with the result of calling [lazyMessage] if this string [isBlank]. */
public fun String.requireNotBlank(lazyMessage: () -> Any): String = also { require(it.isNotBlank(), lazyMessage) }

/** Throws an [IllegalStateException] if this character sequence [isEmpty]. */
public fun CharSequence.checkNotEmpty(): CharSequence = also { check(it.isNotEmpty()) }

/** Throws an [IllegalStateException] if this string [isEmpty]. */
public fun String.checkNotEmpty(): String = also { check(it.isNotEmpty()) }

/** Throws an [IllegalStateException] with the result of calling [lazyMessage] if this character sequence [isEmpty]. */
public fun CharSequence.checkNotEmpty(lazyMessage: () -> Any): CharSequence = also { check(it.isNotEmpty(), lazyMessage) }

/** Throws an [IllegalStateException] with the result of calling [lazyMessage] if this string [isEmpty]. */
public fun String.checkNotEmpty(lazyMessage: () -> Any): String = also { check(it.isNotEmpty(), lazyMessage) }

/** Throws an [IllegalStateException] if this character sequence [isBlank]. */
public fun CharSequence.checkNotBlank(): CharSequence = also { check(it.isNotBlank()) }

/** Throws an [IllegalStateException] if this string [isBlank]. */
public fun String.checkNotBlank(): String = also { check(it.isNotBlank()) }

/** Throws an [IllegalStateException] with the result of calling [lazyMessage] if this character sequence [isBlank]. */
public fun CharSequence.checkNotBlank(lazyMessage: () -> Any): CharSequence = also { check(it.isNotBlank(), lazyMessage) }

/** Throws an [IllegalStateException] with the result of calling [lazyMessage] if this string [isBlank]. */
public fun String.checkNotBlank(lazyMessage: () -> Any): String = also { check(it.isNotBlank(), lazyMessage) }

/** Returns this character sequence if it [isNotEmpty] or `null`, if it is. */
public fun CharSequence.takeIfNotEmpty(): CharSequence? = takeIf { it.isNotEmpty() }

/** Returns this string if it [isNotEmpty] or `null`, if it is. */
public fun String.takeIfNotEmpty(): String? = takeIf { it.isNotEmpty() }

/** Returns this character sequence if it [isNotBlank] or `null`, if it is. */
public fun CharSequence.takeIfNotBlank(): CharSequence? = takeIf { it.isNotBlank() }

/** Returns this string if it [isNotBlank] or `null`, if it is. */
public fun String.takeIfNotBlank(): String? = takeIf { it.isNotBlank() }

/** Returns this character sequence if it [isNotEmpty] or `null`, if it is. */
public fun CharSequence.takeUnlessEmpty(): CharSequence? = takeUnless { it.isEmpty() }

/** Returns this string if it [isNotEmpty] or `null`, if it is. */
public fun String.takeUnlessEmpty(): String? = takeUnless { it.isEmpty() }

/** Returns this character sequence if it [isNotBlank] or `null`, if it is. */
public fun CharSequence.takeUnlessBlank(): CharSequence? = takeUnless { it.isBlank() }

/** Returns this string if it [isNotBlank] or `null`, if it is. */
public fun String.takeUnlessBlank(): String? = takeUnless { it.isBlank() }

private val ansiPatterns = listOf(
    @Suppress("RegExpRedundantEscape") // otherwise "lone quantifier brackets in JS"
    "\\u001B\\]\\d*;[^\\u001B]*\\u001B\\\\".toRegex(), // OSC (operating system command) escape sequences
    "\\u001B[@-Z\\-_]".toRegex(),            // Fe escape sequences
    "\\u001B[ -/][@-~]".toRegex(),           // 2-byte sequences
    "\\u001B\\[[0-?]*[ -/]*[@-~]".toRegex(), // CSI (control sequence intro) escape sequences
)

private val ansiPattern: Regex = ansiPatterns.joinToString("|") { it.pattern }.toRegex()

/** Whether this character sequence contains [ANSI escape codes](https://en.wikipedia.org/wiki/ANSI_escape_code).*/
public val CharSequence.ansiContained: Boolean
    get() = ansiPattern.containsMatchIn(this)

/** Contains this character sequence with all [ANSI escape codes](https://en.wikipedia.org/wiki/ANSI_escape_code) removed. */
public val CharSequence.ansiRemoved: CharSequence
    get() = if (ansiContained) ansiPattern.replace(this, "") else this

/** Contains this character sequence with all [ANSI escape codes](https://en.wikipedia.org/wiki/ANSI_escape_code) removed. */
public val String.ansiRemoved: String
    get() = if (ansiContained) ansiPattern.replace(this, "") else this


/** Returns this char sequence with the [prefix] prepended if it is not already present. */
public fun CharSequence.withPrefix(prefix: CharSequence): CharSequence =
    if (startsWith(prefix)) this else buildString { append(prefix); append(this@withPrefix) }

/** Returns this string with the [prefix] prepended if it is not already present. */
public fun String.withPrefix(prefix: CharSequence): String = if (startsWith(prefix)) this else buildString { append(prefix); append(this@withPrefix) }

/** Returns this char sequence with the [suffix] appended if it is not already present. */
public fun CharSequence.withSuffix(suffix: CharSequence): CharSequence = if (endsWith(suffix)) this else buildString { append(this@withSuffix);append(suffix) }

/** Returns this string with the [suffix] appended if it is not already present. */
public fun String.withSuffix(suffix: CharSequence): String = if (endsWith(suffix)) this else buildString { append(this@withSuffix);append(suffix) }

private const val randomSuffixLength = 4
private const val randomSuffixSeparator = "--"

@Suppress("RegExpSimplifiable")
private val randomSuffixMatcher: Regex = Regex(".*$randomSuffixSeparator[\\da-zA-Z]{$randomSuffixLength}\$")

/** Returns this char sequence with a random suffix of two dashes dash and four alphanumeric characters. */
public fun CharSequence.withRandomSuffix(): CharSequence =
    if (randomSuffixMatcher.matches(this)) this
    else buildString { append(this@withRandomSuffix); append(randomSuffixSeparator); append(randomString(length = randomSuffixLength)) }

/** Returns this string with a random suffix of two dashes dash and four alphanumeric characters. */
public fun String.withRandomSuffix(): String =
    if (randomSuffixMatcher.matches(this)) this
    else buildString { append(this@withRandomSuffix); append(randomSuffixSeparator); append(randomString(length = randomSuffixLength)) }

/** Creates a random string of the specified [length] made up of the specified [allowedCharacters]. */
public fun randomString(length: Int = 16, vararg allowedCharacters: Char = (('0'..'9') + ('a'..'z') + ('A'..'Z')).toCharArray()): String =
    buildString(length) { repeat(length) { append(allowedCharacters[Random.nextInt(0, allowedCharacters.size)]) } }

/** Whether this string consists of more than one line. */
public val CharSequence.isMultiline: Boolean get() = lineSequence().take(2).count() > 1

/**
 * Returns a string representing this object
 * and the properties specified by [include] (default: all)
 * with properties excluded as specified by [excludeNullValues]
 * and [exclude].
 */
public fun <T : Any> T.asString(
    vararg include: KProperty<*>,
    excludeNullValues: Boolean = true,
    exclude: Iterable<KProperty<*>> = emptyList(),
): String {
    val receiver = this
    return asString(excludeNullValues, exclude.map { it.name }) {
        if (include.isEmpty()) putAll(receiver.properties)
        else include.forEach { prop ->
            prop.getOrNull(receiver)?.also { put(prop.name, it) }
        }
    }
}

/**
 * Returns a string representing this object
 * and the properties specified by [include] (default: all)
 * with properties excluded as specified by [excludeNullValues]
 * and [exclude].
 */
public fun <T : Any> T.asString(
    excludeNullValues: Boolean = true,
    exclude: Iterable<String> = emptyList(),
    include: MutableMap<Any, Any?>.() -> Unit,
): String {
    val properties = buildMap(include).mapKeys { (key, _) ->
        when (key) {
            is CharSequence -> key.quoted.removeSurrounding("\"")
            is KProperty<*> -> key.name
            else -> key.render { compression = Always }
        }
    }
    val renderedType = renderType()
    val rendered = properties.render {
        typing = Untyped
        filterProperties { receiver, prop ->
            (!excludeNullValues || receiver != null) && !exclude.contains(prop)
        }
    }
    return buildString {
        append(renderedType)
        append(" ")
        append(rendered.removePrefix(renderedType))
    }
}
