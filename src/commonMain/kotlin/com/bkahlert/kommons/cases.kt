package com.bkahlert.kommons

/** Returns `true` if this character is upper case. */
public fun Char.isUpperCase(): Boolean = this == uppercaseChar() && this != lowercaseChar()

/** Returns `true` if this character is lower case. */
public fun Char.isLowerCase(): Boolean = this == lowercaseChar() && this != uppercaseChar()


/** Returns this char sequence with its first letter in upper case. */
public fun CharSequence.capitalize(): CharSequence = if (isNotEmpty() && first().isLowerCase()) first().uppercaseChar() + substring(1) else this

/** Returns this string with its first letter in upper case. */
public fun String.capitalize(): String = if (isNotEmpty() && first().isLowerCase()) first().uppercaseChar() + substring(1) else this


/** Returns this char sequence with its first letter in upper case. */
@Suppress("SpellCheckingInspection")
public fun CharSequence.decapitalize(): CharSequence = if (isNotEmpty() && first().isUpperCase()) first().lowercaseChar() + substring(1) else this

/** Returns this string with its first letter in upper case. */
@Suppress("SpellCheckingInspection")
public fun String.decapitalize(): String = if (isNotEmpty() && first().isUpperCase()) first().lowercaseChar() + substring(1) else this
