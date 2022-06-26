package com.bkahlert.kommons

import kotlinx.browser.window

/** Platforms this program can be run on. */
public actual sealed interface Platform {

    /** Whether this program is running in debug mode. */
    public actual val isDebugging: Boolean

    /** Supported level for [ANSI escape codes](https://en.wikipedia.org/wiki/ANSI_escape_code). */
    public actual val ansiSupport: AnsiSupport

    /** Registers the specified [handler] as to be called when this program is about to stop. */
    public actual fun onExit(handler: () -> Unit)

    /** JavaScript based platform, e.g. browser. */
    public actual sealed interface JS : Platform {
        /** Browser platform */
        public actual object Browser : JS {
            override val isDebugging: Boolean = false
            override val ansiSupport: AnsiSupport = AnsiSupport.NONE
            override fun onExit(handler: () -> Unit) {
                window.addEventListener(
                    type = "beforeunload",
                    callback = {
                        runCatching(handler).onFailure {
                            console.error("An exception occurred while unloading.", it)
                        }
                    })
            }
        }

        /** NodeJS platform */
        public actual object NodeJS : JS {
            override val isDebugging: Boolean = false
            override val ansiSupport: AnsiSupport = AnsiSupport.NONE
            override fun onExit(handler: () -> Unit) {
                val process = js("require('process')")
                process.on("beforeExit", fun(_: Any?) {
                    runCatching(handler).onFailure {
                        console.error("An exception occurred while unloading.", it)
                    }
                })
            }
        }
    }

    /** Java virtual machine. */
    public actual object JVM : Platform {
        override val isDebugging: Boolean = false
        override val ansiSupport: AnsiSupport = AnsiSupport.NONE
        override fun onExit(handler: () -> Unit): Unit = Unit
    }

    public actual companion object {
        private val currentPlatform by lazy {
            runCatching { kotlinx.browser.window }.fold({ JS.Browser }, { JS.NodeJS })
        }

        /** The platforms this program runs on. */
        public actual val Current: Platform
            get() = currentPlatform
    }
}
