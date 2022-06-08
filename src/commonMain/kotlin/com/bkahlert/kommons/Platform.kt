package com.bkahlert.kommons

import com.bkahlert.kommons.Platform.Companion

/** Platforms this program can be run on. */
public enum class Platform {
    /** JavaScript based platform, e.g. browser. */
    JS,

    /** Java virtual machine. */
    JVM;

    public companion object
}

/** The platforms this program runs on. */
public expect val Companion.Current: Platform
