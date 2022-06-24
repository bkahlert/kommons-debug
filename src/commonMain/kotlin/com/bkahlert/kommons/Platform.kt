package com.bkahlert.kommons

import com.bkahlert.kommons.Platform.Companion

/** Platforms this program can be run on. */
public sealed class Platform {

    /** JavaScript based platform, e.g. browser. */
    public sealed class JS : Platform() {

        /** Browser platform */
        public object Browser : JS()

        /** NodeJS platform */
        public object NodeJS : JS()
    }

    /** Java virtual machine. */
    public object JVM : Platform()

    public companion object
}

/** The platforms this program runs on. */
public expect val Companion.Current: Platform
