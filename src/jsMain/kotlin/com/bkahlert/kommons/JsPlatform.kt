package com.bkahlert.kommons

/** The platforms this program runs on. */
public actual val Platform.Companion.Current: Platform
    get() = currentPlatform

private val currentPlatform by lazy {
    runCatching { kotlinx.browser.window }.fold({ Platform.JS.Browser }, { Platform.JS.NodeJS })
}
