package com.bkahlert.kommons.debug

/**
 * Renders the type of this function to the specified [out].
 * @see renderFunctionType
 */
public actual fun Function<*>.renderFunctionTypeTo(out: StringBuilder, simplified: Boolean) {
    out.append("Function")
}
