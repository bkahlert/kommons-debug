package com.bkahlert.kommons.debug

import kotlin.reflect.KClass

internal expect fun KClass<*>.renderQualifiedTypeTo(out: StringBuilder)
