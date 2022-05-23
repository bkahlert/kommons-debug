package com.bkahlert.kommons.debug

public external object Object {
    /**
     * Returns an array of a given object's own enumerable property names, iterated in the same order that a normal loop would.
     */
    public fun keys(obj: Any): Array<String>

    /**
     * Returns an array of a given object's own enumerable string-keyed property [key, value] pairs.
     */
    public fun entries(obj: Any): Array<Array<Any?>>

    /**
     * Returns an array of all properties (including non-enumerable properties except for those which use Symbol) found directly in a given object.
     */
    public fun getOwnPropertyNames(obj: Any): Array<String>
}

/**
 * Returns an array of a `this` object's own enumerable property names, iterated in the same order that a normal loop would.
 */
public val Any.keys: Array<String> get() = Object.keys(this)

/**
 * Returns an array of a `this` object's own enumerable string-keyed property [key, value] pairs.
 */
public val Any.entries: Array<Array<Any?>> get() = Object.entries(this)
