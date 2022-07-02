package com.bkahlert.kommons

import com.bkahlert.kommons.LineSeparators.Common as CommonLineSeparators
import com.bkahlert.kommons.LineSeparators.Uncommon as UncommonLineSeparators
import com.bkahlert.kommons.LineSeparators.Unicode as UnicodeLineSeparators

/** Whether this regular expression is a group—no matter if named, anonymous, or indexed. */
public val Regex.isGroup: Boolean
    get() {
        if (pattern.length < 2) return false
        val unescaped = pattern.replace(Regex("\\\\."), "X")
        if (unescaped[0] != '(' || unescaped[unescaped.lastIndex] != ')') return false

        var depth = 1
        for (cp in unescaped.toCodePointList().drop(1).dropLast(1)) {
            when (cp.char) {
                '(' -> depth++
                ')' -> depth--
            }
            if (depth <= 0) return false
        }
        return true
    }

/** Whether this regular expression is a named group. */
public val Regex.isNamedGroup: Boolean
    get() = isGroup && pattern.startsWith("(?<")

/** Whether this regular expression is an anonymous group. */
public val Regex.isAnonymousGroup: Boolean
    get() = isGroup && pattern.startsWith("(?:")

/** Whether this regular expression is an indexed group. */
public val Regex.isIndexedGroup: Boolean
    get() = isGroup && !pattern.startsWith("(?<") && !pattern.startsWith("(?:")

/** The contents of this regular expression's group if any, or this [Regex] otherwise. */
public val Regex.groupContents: Regex
    get() = if (isGroup) {
        if (pattern.startsWith("(?<")) Regex(pattern.substring(pattern.indexOf('>') + 1, pattern.length - 1))
        else if (pattern.startsWith("(?:")) Regex(pattern.substring(3, pattern.length - 1))
        else Regex(pattern.substring(1, pattern.length - 1))
    } else this


/** Returns a [Regex] that matches this regex followed by the specified [other]. */
public operator fun Regex.plus(other: Regex): Regex = Regex("$pattern${other.pattern}")

/** Returns a [Regex] that matches this regex followed by the specified [pattern]. */
public operator fun Regex.plus(pattern: CharSequence): Regex = Regex("${this.pattern}$pattern")


/** Returns a [Regex] consisting of this and the specified [other] concatenated with `|`. */
public infix fun Regex.or(other: Regex): Regex = Regex("$pattern|${other.pattern}")

/** Returns a [Regex] consisting of this and the specified [otherPattern] concatenated with `|`. */
public infix fun Regex.or(otherPattern: CharSequence): Regex = Regex("$pattern|$otherPattern")

/**
 * Returns a [Regex] that groups this [Regex].
 *
 * If a [name] is specified, a named group (e.g. `(?<name>foo)` is returned.
 *
 * If no [name] is specified **and** this regex is not already grouped,
 * an anonymous/non-capturing group (e.g. `(?:foo)`) is returned.
 *
 * In other words: No unnecessary brackets are added.
 */
public fun Regex.group(name: String? = null): Regex {
    return when (name) {
        null -> {
            if (isGroup) this
            else Regex("(?:$pattern)")
        }
        else -> {
            requireValidGroupName(name)
            if (isGroup) {
                if (pattern.startsWith("(?<")) Regex("(?<$name>$pattern)")
                else if (pattern.startsWith("(?:")) Regex("(?<$name>${pattern.substring(3, pattern.length - 1)})")
                else Regex("(?<$name>${pattern.substring(1, pattern.length - 1)})")
            } else Regex("(?<$name>$pattern)")
        }
    }
}

/**
 * Returns this regular expression if it [isGroup] already or
 * this regular expression as an anonymous group otherwise.
 *
 * @see group
 */
public val Regex.grouped: Regex get() = group(null)

/** Returns the specified [name] if it is a valid group name or throws an [IllegalArgumentException] otherwise. */
private fun requireValidGroupName(name: String): String = name.apply {
    require(this.all { it in 'a'..'z' || it in 'A'..'Z' }) {
        "Group name $this must only consist of letters a..z and A..Z."
    }
}

/**
 * Returns a [Regex] that optionally matches this [Regex].
 *
 * Example: `foo` becomes `(?:foo)?`
 */
public fun Regex.optional(): Regex = Regex("${grouped.pattern}?")

/**
 * Returns a [Regex] that matches this [Regex] any number of times.
 *
 * Example: `foo` becomes `(?:foo)*`
 */
public fun Regex.repeatAny(): Regex = Regex("${grouped.pattern}*")

/**
 * Returns a [Regex] that matches this [Regex] at least once.
 *
 * Example: `foo` becomes `(?:foo)+`
 */
public fun Regex.repeatAtLeastOnce(): Regex = Regex("${grouped.pattern}+")

/**
 * Returns a [Regex] that matches this [Regex] between [min] and [max] times.
 *
 * Example: `foo` becomes `(?:foo){2,5}`
 */
public fun Regex.repeat(min: Int? = 0, max: Int? = null): Regex {
    if (min == 0 && max == 1) return optional()
    if (min == 0 && max == null) return repeatAny()
    if (min == 1 && max == null) return repeatAtLeastOnce()
    val minString = min?.toString() ?: ""
    val maxString = max?.toString() ?: ""
    return Regex("${grouped.pattern}{$minString,$maxString}")
}


/**
 * Returns a named group with the specified [name].
 * @return An instance of [MatchGroup] if the group with the specified [name] was matched or `null` otherwise.
 * @throws IllegalArgumentException if there is no group with the specified [name] defined in the regex pattern.
 * @throws UnsupportedOperationException if this match group collection doesn't support getting match groups by name,
 * for example, when it's not supported by the current platform.
 */
public operator fun MatchGroupCollection.get(name: String): MatchGroup? {
    val named = (this as? MatchNamedGroupCollection)
        ?: throw UnsupportedOperationException("This match group collection doesn't support getting match groups by name.")
    return named[name]
}

/** Returns the value of the matched [MatchGroup] with the provided [index]. */
public fun MatchResult.groupValue(index: Int): String? = groups[index]?.value

/** Returns the value of the matched [MatchGroup] with the provided [name]. */
public fun MatchResult.groupValue(name: String): String? = groups[name]?.value

/**
 * Returns a sequence of all occurrences of this regular expression within
 * the [input] string, beginning at the specified [startIndex].
 *
 * @throws IndexOutOfBoundsException if [startIndex] is less than zero or
 *         greater than the length of the [input] character sequence.
 */
public fun Regex.findAllValues(input: CharSequence, startIndex: Int = 0): Sequence<String> =
    findAll(input, startIndex).map { it.value }


private val anyCharacterRegex = Regex("[\\s\\S]")

/** A [Regex] that matches any character including line breaks. */
public val Regex.Companion.AnyCharacterRegex: Regex get() = anyCharacterRegex


private val urlRegex = Regex("(?<schema>https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")
private val uriRegex: Regex = Regex("\\w+:(?:/?/?)[^\\s]+")

/** A [Regex] that matches URLs. */
public val Regex.Companion.UrlRegex: Regex get() = urlRegex

/** A [Regex] that matches URIs. */
public val Regex.Companion.UriRegex: Regex get() = uriRegex


private val commonLineSeparatorsRegex = Regex(CommonLineSeparators.joinToString("|"))
private val uncommonLineSeparatorsRegex = Regex(UncommonLineSeparators.joinToString("|"))
private val unicodeLineSeparatorsRegex = Regex(UnicodeLineSeparators.joinToString("|"))

/** A [Regex] that matches [CommonLineSeparators]. */
public val Regex.Companion.CommonLineSeparatorsRegex: Regex get() = commonLineSeparatorsRegex

/** A [Regex] that matches [UncommonLineSeparators]. */
public val Regex.Companion.UncommonLineSeparatorsRegex: Regex get() = uncommonLineSeparatorsRegex

/** A [Regex] that matches [UnicodeLineSeparators]. */
public val Regex.Companion.UnicodeLineSeparatorsRegex: Regex get() = unicodeLineSeparatorsRegex
