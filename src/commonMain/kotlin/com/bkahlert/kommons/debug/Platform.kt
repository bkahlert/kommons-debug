package com.bkahlert.kommons.debug

/** Platforms this program can be run on. */
public enum class Platform {
    /** JavaScript based platform, e.g. browser. */
    JS,

    /** Java virtual machine. */
    JVM;

    public companion object
}

/** The platforms this program runs on. */
public expect val Platform.Companion.Current: Platform

/** Whether this program is started by IDEA Intellij. */
public expect val Platform.Companion.isIntelliJ: Boolean

/** Whether this program is running in debug mode. */
public expect val Platform.Companion.isDebugging: Boolean
