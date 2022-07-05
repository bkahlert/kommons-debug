package com.bkahlert.kommons

import com.bkahlert.kommons.AnsiSupport.ANSI24
import com.bkahlert.kommons.AnsiSupport.ANSI4
import com.bkahlert.kommons.AnsiSupport.ANSI8
import java.util.Locale

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

        override val ansiSupport: AnsiSupport by lazy {
            val termProgram = System.getenv("TERM_PROGRAM")?.lowercase(Locale.getDefault())
            val term = System.getenv("TERM")?.lowercase(Locale.getDefault())
            @Suppress("SpellCheckingInspection")
            when {
                Program.isIntelliJ -> ANSI24
                termProgram == "vscode" -> ANSI8
                System.getenv("COLORTERM")?.lowercase(Locale.getDefault()) in listOf("24bit", "truecolor") -> ANSI24
                System.console() == null -> AnsiSupport.NONE
                termProgram == "hyper" -> ANSI24 // stackoverflow.com/q/7052683
                termProgram == "apple_terminal" -> ANSI8
                termProgram == "iterm.app" -> System.getenv("TERM_PROGRAM_VERSION")?.toIntOrNull()?.takeIf { it > 3 }?.let { ANSI24 } ?: ANSI8
                term?.let { it.endsWith("-256color") || it.endsWith("-256") } == true -> ANSI8
                term == "cygwin" -> ANSI24.takeIf { System.getProperty("os.name")?.startsWith("Windows") == true } ?: ANSI8
                term in listOf("xterm", "vt100", "screen", "ansi") -> ANSI4
                term == "dumb" -> AnsiSupport.NONE
                else -> AnsiSupport.NONE
            }
        }
    }

    public actual companion object {
        /** The platforms this program runs on. */
        public actual val Current: Platform
            get() = JVM
    }
}
