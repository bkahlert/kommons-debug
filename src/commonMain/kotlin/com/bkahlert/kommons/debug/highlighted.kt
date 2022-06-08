package com.bkahlert.kommons.debug

import com.bkahlert.kommons.Current
import com.bkahlert.kommons.Platform
import com.bkahlert.kommons.Platform.JS
import com.bkahlert.kommons.Platform.JVM
import com.bkahlert.kommons.Unicode

/** This string platform-specifically cyan colored and emphasized. */
public val CharSequence.highlighted: String
    get() = when (Platform.Current) {
        JS -> buildString { append("<span style=\"color:#01818F;font-weight:bold;\">"); append(this@highlighted); append("</span>") }
        JVM -> lineSequence().joinToString("${Unicode.LINE_FEED}") { "\u001b[1;36m$it\u001B[0m" }
    }

/** This string platform-specifically bright cyan colored. */
public val CharSequence.highlightedStrongly: String
    get() = when (Platform.Current) {
        JS -> buildString { append("<span style=\"color:#01E6FF;\">"); append(this@highlightedStrongly); append("</span>") }
        JVM -> lineSequence().joinToString("${Unicode.LINE_FEED}") { "\u001b[96m$it\u001B[0m" }
    }
