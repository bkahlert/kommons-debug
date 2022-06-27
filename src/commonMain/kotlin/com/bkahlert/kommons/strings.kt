package com.bkahlert.kommons

import com.bkahlert.kommons.debug.Compression.Always
import com.bkahlert.kommons.debug.Typing.Untyped
import com.bkahlert.kommons.debug.getOrNull
import com.bkahlert.kommons.debug.properties
import com.bkahlert.kommons.debug.render
import com.bkahlert.kommons.debug.renderType
import kotlin.random.Random
import kotlin.reflect.KProperty

// containsAny ---------------------------------------------------------------------------------------------------------

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


// requireNotEmpty -----------------------------------------------------------------------------------------------------

/** Throws an [IllegalArgumentException] if the specified [charSequence] [isEmpty]. */
@Suppress("NOTHING_TO_INLINE")
public inline fun requireNotEmpty(charSequence: CharSequence): CharSequence = charSequence.also { require(it.isNotEmpty()) }

/** Throws an [IllegalArgumentException] if the specified [string] [isEmpty]. */
@Suppress("NOTHING_TO_INLINE")
public inline fun requireNotEmpty(string: String): String = string.also { require(it.isNotEmpty()) }

/** Throws an [IllegalArgumentException] with the result of calling [lazyMessage] if the specified [charSequence] [isEmpty]. */
@Suppress("NOTHING_TO_INLINE")
public inline fun requireNotEmpty(charSequence: CharSequence, lazyMessage: () -> Any): CharSequence =
    charSequence.also { require(it.isNotEmpty(), lazyMessage) }

/** Throws an [IllegalArgumentException] with the result of calling [lazyMessage] if the specified [string] [isEmpty]. */
@Suppress("NOTHING_TO_INLINE")
public inline fun requireNotEmpty(string: String, lazyMessage: () -> Any): String = string.also { require(it.isNotEmpty(), lazyMessage) }


// requireNotBlank -----------------------------------------------------------------------------------------------------

/** Throws an [IllegalArgumentException] if the specified [charSequence] [isBlank]. */
@Suppress("NOTHING_TO_INLINE")
public inline fun requireNotBlank(charSequence: CharSequence): CharSequence = charSequence.also { require(charSequence.isNotBlank()) }

/** Throws an [IllegalArgumentException] if the specified [string] [isBlank]. */
@Suppress("NOTHING_TO_INLINE")
public inline fun requireNotBlank(string: String): String = string.also { require(string.isNotBlank()) }

/** Throws an [IllegalArgumentException] with the result of calling [lazyMessage] if the specified [charSequence] [isBlank]. */
@Suppress("NOTHING_TO_INLINE")
public inline fun requireNotBlank(charSequence: CharSequence, lazyMessage: () -> Any): CharSequence =
    charSequence.also { require(it.isNotBlank(), lazyMessage) }

/** Throws an [IllegalArgumentException] with the result of calling [lazyMessage] if the specified [string] [isBlank]. */
@Suppress("NOTHING_TO_INLINE")
public inline fun requireNotBlank(string: String, lazyMessage: () -> Any): String = string.also { require(it.isNotBlank(), lazyMessage) }


// checkNotEmpty -------------------------------------------------------------------------------------------------------
/** Throws an [IllegalStateException] if the specified [charSequence] [isEmpty]. */
@Suppress("NOTHING_TO_INLINE")
public inline fun checkNotEmpty(charSequence: CharSequence): CharSequence = charSequence.also { check(it.isNotEmpty()) }

/** Throws an [IllegalStateException] if the specified [string] [isEmpty]. */
@Suppress("NOTHING_TO_INLINE")
public inline fun checkNotEmpty(string: String): String = string.also { check(it.isNotEmpty()) }

/** Throws an [IllegalStateException] with the result of calling [lazyMessage] if the specified [charSequence] [isEmpty]. */
@Suppress("NOTHING_TO_INLINE")
public inline fun checkNotEmpty(charSequence: CharSequence, lazyMessage: () -> Any): CharSequence = charSequence.also { check(it.isNotEmpty(), lazyMessage) }

/** Throws an [IllegalStateException] with the result of calling [lazyMessage] if the specified [string] [isEmpty]. */
@Suppress("NOTHING_TO_INLINE")
public inline fun checkNotEmpty(string: String, lazyMessage: () -> Any): String = string.also { check(it.isNotEmpty(), lazyMessage) }


// checkNotBlank -------------------------------------------------------------------------------------------------------

/** Throws an [IllegalStateException] if the specified [charSequence] [isBlank]. */
@Suppress("NOTHING_TO_INLINE")
public inline fun checkNotBlank(charSequence: CharSequence): CharSequence = charSequence.also { check(it.isNotBlank()) }

/** Throws an [IllegalStateException] if the specified [string] [isBlank]. */
@Suppress("NOTHING_TO_INLINE")
public inline fun checkNotBlank(string: String): String = string.also { check(it.isNotBlank()) }

/** Throws an [IllegalStateException] with the result of calling [lazyMessage] if the specified [charSequence] [isBlank]. */
@Suppress("NOTHING_TO_INLINE")
public inline fun checkNotBlank(charSequence: CharSequence, lazyMessage: () -> Any): CharSequence = charSequence.also { check(it.isNotBlank(), lazyMessage) }

/** Throws an [IllegalStateException] with the result of calling [lazyMessage] if the specified [string] [isBlank]. */
@Suppress("NOTHING_TO_INLINE")
public inline fun checkNotBlank(string: String, lazyMessage: () -> Any): String = string.also { check(it.isNotBlank(), lazyMessage) }


// takeIfNotEmpty ------------------------------------------------------------------------------------------------------

/** Returns this character sequence if it [isNotEmpty] or `null`, if it is. */
@Suppress("NOTHING_TO_INLINE")
public inline fun CharSequence.takeIfNotEmpty(): CharSequence? = takeIf { it.isNotEmpty() }

/** Returns this string if it [isNotEmpty] or `null`, if it is. */
@Suppress("NOTHING_TO_INLINE")
public inline fun String.takeIfNotEmpty(): String? = takeIf { it.isNotEmpty() }


// takeIfNotBlank ------------------------------------------------------------------------------------------------------

/** Returns this character sequence if it [isNotBlank] or `null`, if it is. */
@Suppress("NOTHING_TO_INLINE")
public inline fun CharSequence.takeIfNotBlank(): CharSequence? = takeIf { it.isNotBlank() }

/** Returns this string if it [isNotBlank] or `null`, if it is. */
@Suppress("NOTHING_TO_INLINE")
public inline fun String.takeIfNotBlank(): String? = takeIf { it.isNotBlank() }


// takeUnlessEmpty -----------------------------------------------------------------------------------------------------

/** Returns this character sequence if it [isNotEmpty] or `null`, if it is. */
@Suppress("NOTHING_TO_INLINE")
public inline fun CharSequence.takeUnlessEmpty(): CharSequence? = takeUnless { it.isEmpty() }

/** Returns this string if it [isNotEmpty] or `null`, if it is. */
@Suppress("NOTHING_TO_INLINE")
public inline fun String.takeUnlessEmpty(): String? = takeUnless { it.isEmpty() }


// takeUnlessBlank -----------------------------------------------------------------------------------------------------

/** Returns this character sequence if it [isNotBlank] or `null`, if it is. */
@Suppress("NOTHING_TO_INLINE")
public inline fun CharSequence.takeUnlessBlank(): CharSequence? = takeUnless { it.isBlank() }

/** Returns this string if it [isNotBlank] or `null`, if it is. */
@Suppress("NOTHING_TO_INLINE")
public inline fun String.takeUnlessBlank(): String? = takeUnless { it.isBlank() }


// ansiContained / ansiRemoved -----------------------------------------------------------------------------------------

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


// spaced / startSpaced / endSpaced ------------------------------------------------------------------------------------

/** Returns this char with a space added to each side if not already present. */
public inline val Char.spaced: String get() = startSpaced.endSpaced

/** Returns this char with a space added to the beginning if not already present. */
public inline val Char.startSpaced: String get() = withPrefix(" ")

/** Returns this char with a space added to the end if not already present. */
public inline val Char.endSpaced: String get() = withSuffix(" ")

/** Returns this character sequence with a space added to each side if not already present. */
public inline val CharSequence.spaced: CharSequence get() = startSpaced.endSpaced

/** Returns this character sequence with a space added to the beginning if not already present. */
public inline val CharSequence.startSpaced: CharSequence get() = withPrefix(" ")

/** Returns this character sequence with a space added to the end if not already present. */
public inline val CharSequence.endSpaced: CharSequence get() = withSuffix(" ")

/** Returns this string with a space added to each side if not already present. */
public inline val String.spaced: String get() = startSpaced.endSpaced

/** Returns this string with a space added to the beginning if not already present. */
public inline val String.startSpaced: String get() = withPrefix(" ")

/** Returns this string with a space added to the end if not already present. */
public inline val String.endSpaced: String get() = withSuffix(" ")





/** Returns this char with the [prefix] prepended if it is not already present. */
public fun Char.withPrefix(prefix: CharSequence): String =
    toString().withPrefix(prefix)

/** Returns this char with the [suffix] appended if it is not already present. */
public fun Char.withSuffix(suffix: CharSequence): String =
    toString().withSuffix(suffix)

/** Returns this character sequence with the [prefix] prepended if it is not already present. */
public fun CharSequence.withPrefix(prefix: CharSequence): CharSequence =
    if (startsWith(prefix)) this else buildString { append(prefix); append(this@withPrefix) }

/** Returns this character sequence with the [suffix] appended if it is not already present. */
public fun CharSequence.withSuffix(suffix: CharSequence): CharSequence =
    if (endsWith(suffix)) this else buildString { append(this@withSuffix);append(suffix) }

/** Returns this string with the [prefix] prepended if it is not already present. */
public fun String.withPrefix(prefix: CharSequence): String =
    if (startsWith(prefix)) this else buildString { append(prefix); append(this@withPrefix) }

/** Returns this string with the [suffix] appended if it is not already present. */
public fun String.withSuffix(suffix: CharSequence): String =
    if (endsWith(suffix)) this else buildString { append(this@withSuffix);append(suffix) }


// withRandomSuffix ----------------------------------------------------------------------------------------------------

private const val randomSuffixLength = 4
private const val randomSuffixSeparator = "--"

@Suppress("RegExpSimplifiable")
private val randomSuffixMatcher: Regex = Regex(".*$randomSuffixSeparator[\\da-zA-Z]{$randomSuffixLength}\$")

/** Returns this char with a random suffix of two dashes dash and four alphanumeric characters. */
public fun Char.withRandomSuffix(): String =
    toString().withRandomSuffix()

/** Returns this character sequence with a random suffix of two dashes dash and four alphanumeric characters. */
public fun CharSequence.withRandomSuffix(): CharSequence =
    if (randomSuffixMatcher.matches(this)) this
    else buildString { append(this@withRandomSuffix); append(randomSuffixSeparator); append(randomString(length = randomSuffixLength)) }

/** Returns this string with a random suffix of two dashes dash and four alphanumeric characters. */
public fun String.withRandomSuffix(): String =
    if (randomSuffixMatcher.matches(this)) this
    else buildString { append(this@withRandomSuffix); append(randomSuffixSeparator); append(randomString(length = randomSuffixLength)) }


// randomString --------------------------------------------------------------------------------------------------------

/** Creates a random string of the specified [length] made up of the specified [allowedCharacters]. */
public fun randomString(length: Int = 16, vararg allowedCharacters: Char = (('0'..'9') + ('a'..'z') + ('A'..'Z')).toCharArray()): String =
    buildString(length) { repeat(length) { append(allowedCharacters[Random.nextInt(0, allowedCharacters.size)]) } }


// indexOfOrNull -------------------------------------------------------------------------------------------------------

/**
 * Returns the index within this string of the first occurrence of the specified character,
 * starting from the specified [startIndex].
 *
 * @param ignoreCase `true` to ignore character case when matching a character. By default `false`.
 * @return An index of the first occurrence of [char] or `null` if none is found.
 */
public fun CharSequence.indexOfOrNull(char: Char, startIndex: Int = 0, ignoreCase: Boolean = false): Int? =
    indexOf(char, startIndex, ignoreCase).takeIf { it >= 0 }

/**
 * Returns the index within this character sequence of the first occurrence of the specified [string],
 * starting from the specified [startIndex].
 *
 * @param ignoreCase `true` to ignore character case when matching a string. By default `false`.
 * @return An index of the first occurrence of [string] or `null` if none is found.
 */
public fun CharSequence.indexOfOrNull(string: String, startIndex: Int = 0, ignoreCase: Boolean = false): Int? =
    indexOf(string, startIndex, ignoreCase).takeIf { it >= 0 }


// isMultiline ---------------------------------------------------------------------------------------------------------

/** Whether this string consists of more than one line. */
public val CharSequence.isMultiline: Boolean get() = lineSequence().take(2).count() > 1


// asString ------------------------------------------------------------------------------------------------------------

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
