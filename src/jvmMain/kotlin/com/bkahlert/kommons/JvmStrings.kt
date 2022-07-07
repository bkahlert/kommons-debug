package com.bkahlert.kommons

import com.bkahlert.kommons.debug.trace
import com.ibm.icu.text.Transliterator
import java.util.Locale

/**
 * Returns this string transformed according to the specified [id].
 *
 * **Example:**
 * Use `Latin-ASCII` to converts non-ASCII-range punctuation, symbols,
 * and Latin letters in an approximate ASCII-range equivalent (e.g. `©` to `(C)` and `Æ` to `AE`).
 *
 * @see <a href="https://unicode-org.github.io/icu/userguide/transforms/general/#formal-id-syntax">Formal ID Syntax</a>
 * @see <a href="https://unicode-org.github.io/icu-docs/apidoc/released/icu4j/">ICU4J API Specification</a>
 */
public fun String.transform(id: String): String =
    Transliterator.getInstance(id).transliterate(this)

public sealed class TransformationId {
    public class SingleId(
        public val filter: String?,
        public val basicId: String,
        public val customInverse: String?,
    ) {
        override fun toString(): String {
            return buildString {
                filter?.also { append("$it ") }

            }
        }
    }
}

/**
 * The simplest identifier.
 *
 * @see <a href="https://unicode-org.github.io/icu/userguide/transforms/general/#basic-ids">Basic IDs</a>
 */
public class BasicId(
    /** The specifier describing the characters or strings that a transformation will modify. */
    public val source: String?,
    /** The specifier describing the result of a transformation. */
    public val target: String,
    public val variant: String?,
) {
    override fun toString(): String {
        return buildString {
            source?.also { append("$it-") }
            append(target)
            variant?.also { append("/$it") }
        }
    }
}

// TODO Parser (parse, parseOrNull)
// TODO - use in other cases
// TODO - then continue with basic id

/**
 * Returns this string transliterated according to the specified [locale].
 *
 * **Example:**
 * Use `Latin-ASCII` to converts non-ASCII-range punctuation, symbols,
 * and Latin letters in an approximate ASCII-range equivalent (e.g. `©` to `(C)` and `Æ` to `AE`).
 *
 * @see <a href="https://unicode-org.github.io/icu/userguide/transforms/general/#formal-id-syntax">Formal ID Syntax</a>
 * @see <a href="https://unicode-org.github.io/icu-docs/apidoc/released/icu4j/">ICU4J API Specification</a>
 */
public fun String.transliterateTo(locale: Locale): String =
    transform("Any-${locale.toId()}")

private fun Locale.toId() = buildString {
    append(language)
    country.takeUnlessEmpty()?.let { append("_$it") }
    variant.takeUnlessEmpty()?.let { append("/$it") }
}.trace
