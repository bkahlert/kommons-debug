package com.bkahlert.kommons.debug

public actual fun Function<*>.renderFunctionTypeTo(out: StringBuilder, simplified: Boolean) {
    out.append("Function")
}
