package com.bkahlert.kommons.debug

/** Platforms this program can be run on. */
public enum class Platform {
    JS, JVM;

    public companion object {
        /** The platforms this program runs on. */
        public val Current: Platform get() = CurrentPlatform
    }
}

internal expect val CurrentPlatform: Platform
