package com.bkahlert.kommons

/** Platforms this program can be run on. */
public expect sealed interface Platform {

    /** Supported level for [ANSI escape codes](https://en.wikipedia.org/wiki/ANSI_escape_code). */
    public val ansiSupport: AnsiSupport

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
