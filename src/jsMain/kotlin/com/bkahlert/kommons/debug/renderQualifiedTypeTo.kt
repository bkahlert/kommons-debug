package com.bkahlert.kommons.debug

import kotlin.reflect.KClass

internal actual fun KClass<*>.renderQualifiedTypeTo(out: StringBuilder) {
    out.append(js.name.let {
        if (objectRegex.containsMatchIn(it)) "<object>"
        else if (implRegex.containsMatchIn(it)) it.replace(implRegex, "")
        else it
    })
}

private val objectRegex = "\\$\\d+$".toRegex()
private val implRegex = "_\\d+$".toRegex()
