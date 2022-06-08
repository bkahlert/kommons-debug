package com.bkahlert.kommons

/** The platforms this program runs on. */
public actual inline val Platform.Companion.Current: Platform get() = Platform.JS
