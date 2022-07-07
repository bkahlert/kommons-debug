package com.bkahlert.kommons

import kotlin.reflect.KClass

/** Parser than can parse a string into a [T] object. */
public interface Parser<T : Any> {

    /** Returns an object of type [T] representing the parsed [string], or `null` otherwise. */
    public fun parseOrNull(string: CharSequence): T? = kotlin.runCatching { parse(string) }.getOrNull()

    /** Returns an object of type [T] representing the parsed [string], or throws a [ParserException] otherwise. */
    public fun parse(string: CharSequence): T

    public companion object {

        /** Exception thrown by a [Parser] when parsing fails. */
        public class ParserException(
            message: String,
            /** Optional cause of this exception. */
            cause: Throwable? = null,
        ) : IllegalArgumentException(message, cause) {
            /** Creates a new parse exception for the specified [string], the specified [type], and the optional [cause]. */
            public constructor(
                string: CharSequence,
                type: KClass<*>,
                cause: Throwable? = null,
            ) : this("Failed to parse ${string.quoted} into an instance of ${type.simpleName ?: type.toString()}", cause)
        }

        /** Returns a [Parser] that can parse into a [T] object using the specified [parseOrNull]. */
        public inline fun <reified T : Any> parser(crossinline parseOrNull: (CharSequence) -> T?): Parser<T> = object : Parser<T> {
            override fun parse(string: CharSequence): T = kotlin.runCatching {
                parseOrNull(string) ?: throw ParserException(string, T::class)
            }.getOrElse {
                if (it is ParserException) throw it
                throw throw ParserException(string, T::class, it)
            }
        }
    }
}
