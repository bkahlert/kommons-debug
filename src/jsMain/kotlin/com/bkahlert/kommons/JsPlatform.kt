package com.bkahlert.kommons

/** Platforms this program can be run on. */
public actual sealed interface Platform {

    /** Supported level for [ANSI escape codes](https://en.wikipedia.org/wiki/ANSI_escape_code). */
    public actual val ansiSupport: AnsiSupport

    /** JavaScript based platform, e.g. browser. */
    public actual sealed interface JS : Platform {
        /** Browser platform */
        public actual object Browser : JS {
            override val ansiSupport: AnsiSupport = AnsiSupport.NONE
        }

        /** NodeJS platform */
        public actual object NodeJS : JS {
            override val ansiSupport: AnsiSupport = AnsiSupport.NONE
        }
    }

    /** Java virtual machine. */
    public actual object JVM : Platform {
        override val ansiSupport: AnsiSupport = AnsiSupport.NONE
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
