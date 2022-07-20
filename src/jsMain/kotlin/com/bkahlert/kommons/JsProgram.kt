package com.bkahlert.kommons

import kotlinx.browser.window

/** The running program. */
public actual object Program {

    /** The name of running program. */
    public actual val name: String? = null

    /** The group name of running program. */
    public actual val group: String? = null

    /** The version of the running program, or `null` if it can't be determined. */
    public actual val version: SemanticVersion? = null

    /** Whether this program is running in debug mode. */
    public actual val isDebugging: Boolean = false

    private val onExitDelegate by lazy {
        when (Platform.Current) {
            is Platform.JS.Browser -> ::browserOnExit
            is Platform.JS.NodeJS -> ::nodeOnExit
            else -> error("Unsupported platform")
        }
    }

    /** Registers the specified [handler] as to be called when this program is about to stop. */
    public actual fun onExit(handler: () -> Unit): Unit = onExitDelegate(handler)
}

private fun browserOnExit(handler: () -> Unit) {
    window.addEventListener(
        type = "beforeunload",
        callback = {
            runCatching(handler).onFailure {
                console.error("An exception occurred while unloading.", it)
            }
        })
}

private fun nodeOnExit(handler: () -> Unit) {
    val process = js("require('process')")
    process.on("beforeExit", fun(_: Any?) {
        runCatching(handler).onFailure {
            console.error("An exception occurred while unloading.", it)
        }
    })
}
