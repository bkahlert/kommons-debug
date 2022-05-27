package com.bkahlert.kommons.debug

/** The platforms this program runs on. */
public actual inline val Platform.Companion.Current: Platform get() = Platform.JS

/** Whether this program is started by IDEA Intellij. */
public actual val Platform.Companion.isIntelliJ: Boolean get() = false

/** Whether this program is running in debug mode. */
public actual val Platform.Companion.isDebugging: Boolean get() = false
