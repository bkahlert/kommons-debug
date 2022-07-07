package com.bkahlert.kommons

import com.ibm.icu.text.Transliterator

/**
 * Returns this string transliterated according to the specified [id].
 *
 * **Example:**
 * Use `Latin-ASCII` to converts non-ASCII-range punctuation, symbols,
 * and Latin letters in an approximate ASCII-range equivalent (e.g. `©` to `(C)` and `Æ` to `AE`).
 *
 * @see <a href="https://unicode-org.github.io/icu/userguide/transforms/general/#formal-id-syntax">Formal ID Syntax</a>
 * @see <a href="https://unicode-org.github.io/icu-docs/apidoc/released/icu4j/">ICU4J API Specification</a>
 */
public fun String.transliterate(id: String): String =
    Transliterator.getInstance(id).transliterate(this)
