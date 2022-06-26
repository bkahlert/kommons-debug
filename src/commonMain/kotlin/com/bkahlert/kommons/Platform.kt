package com.bkahlert.kommons

/** Platforms this program can be run on. */
public expect sealed interface Platform {

    /** Whether this program is running in debug mode. */
    public val isDebugging: Boolean

    /** Supported level for [ANSI escape codes](https://en.wikipedia.org/wiki/ANSI_escape_code). */
    public val ansiSupport: AnsiSupport

    /** Registers the specified [handler] as to be called when this program is about to stop. */
    public fun onExit(handler: () -> Unit)

    /** JavaScript based platform, e.g. browser. */
    public sealed interface JS : Platform {

        /** Browser platform */
        public object Browser : JS

        /** NodeJS platform */
        public object NodeJS : JS
    }

    /** Java virtual machine. */
    public object JVM : Platform

    public companion object {

        /** The platforms this program runs on. */
        public val Current: Platform
    }
}
