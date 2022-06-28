package com.bkahlert.kommons

import com.bkahlert.kommons.AnsiSupport.ANSI24
import com.bkahlert.kommons.AnsiSupport.ANSI4
import com.bkahlert.kommons.AnsiSupport.ANSI8
import com.bkahlert.kommons.LineSeparators.LF
import com.bkahlert.kommons.Platform.JVM
import com.bkahlert.kommons.debug.StackTrace
import com.bkahlert.kommons.debug.get
import com.bkahlert.kommons.debug.highlighted
import com.bkahlert.kommons.debug.render
import com.bkahlert.kommons.debug.renderType
import org.slf4j.LoggerFactory
import java.io.PrintWriter
import java.nio.file.Path
import java.util.Locale
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

/** Platforms this program can be run on. */
public actual sealed interface Platform {

    /** Whether this program is running in debug mode. */
    public actual val isDebugging: Boolean

    /** Supported level for [ANSI escape codes](https://en.wikipedia.org/wiki/ANSI_escape_code). */
    public actual val ansiSupport: AnsiSupport

    /** Registers the specified [handler] as to be called when this program is about to stop. */
    public actual fun onExit(handler: () -> Unit)

    /** JavaScript based platform, e.g. browser. */
    public actual sealed interface JS : Platform {
        /** Browser platform */
        public actual object Browser : JS {
            override val isDebugging: Boolean = false
            override val ansiSupport: AnsiSupport = AnsiSupport.NONE
            override fun onExit(handler: () -> Unit): Unit = Unit
        }

        /** NodeJS platform */
        public actual object NodeJS : JS {
            override val isDebugging: Boolean = false
            override val ansiSupport: AnsiSupport = AnsiSupport.NONE
            override fun onExit(handler: () -> Unit): Unit = Unit
        }
    }

    /** Java virtual machine. */
    public actual object JVM : Platform {

        /**
         * Contains the context ClassLoader for the current [Thread].
         *
         * The context [ClassLoader] is provided by the creator of the [Thread] for use
         * by code running in this thread when loading classes and resources.
         */
        public val contextClassLoader: ClassLoader
            get() = Thread.currentThread().contextClassLoader

        private val jvmArgs: List<String>
            get() {
                val classLoader = contextClassLoader
                return classLoader.loadClassOrNull("java.lang.management.ManagementFactory")?.let {
                    val runtimeMxBean: Any = it.getMethod("getRuntimeMXBean").invoke(null)
                    val runtimeMxBeanClass: Class<*> = classLoader.loadClass("java.lang.management.RuntimeMXBean")
                    val inputArgs: Any = runtimeMxBeanClass.getMethod("getInputArguments").invoke(runtimeMxBean)
                    (inputArgs as? List<*>)?.map { arg -> arg.toString() }
                } ?: emptyList()
            }

        private val jvmJavaAgents: List<String>
            get() = jvmArgs.filter { it.startsWith("-javaagent") }

        private val intellijTraits: List<String>
            get() = listOf("jetbrains", "intellij", "idea", "idea_rt.jar")

        /** Whether this program is started by IDEA Intellij. */
        public val isIntelliJ: Boolean
            get() = runCatching { jvmJavaAgents.any { it.containsAny(intellijTraits, ignoreCase = true) } }.getOrElse { false }

        public override val isDebugging: Boolean
            get() = jvmArgs.any { it.startsWith("-agentlib:jdwp") } || jvmJavaAgents.any { it.contains("debugger") }

        override val ansiSupport: AnsiSupport by lazy {
            val termProgram = System.getenv("TERM_PROGRAM")?.lowercase(Locale.getDefault())
            val term = System.getenv("TERM")?.lowercase(Locale.getDefault())
            @Suppress("SpellCheckingInspection")
            when {
                isIntelliJ -> ANSI24
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

        /** Registers the specified [handler] as a new virtual-machine shutdown hook. */
        override fun onExit(handler: () -> Unit): Unit = addShutDownHook(handler.toHook())

        /** Registers the given [thread] as a new virtual-machine shutdown hook. */
        public fun addShutDownHook(thread: Thread): Unit = Runtime.getRuntime().addShutdownHook(thread)

        /** Unregisters the given [thread] from the virtual-machine shutdown hooks. */
        public fun removeShutdownHook(thread: Thread): Any =
            runCatching { Runtime.getRuntime().removeShutdownHook(thread) }.onFailure {
                if (!it.ignore) throw it else Unit
            }
    }

    public actual companion object {
        /** The platforms this program runs on. */
        public actual val Current: Platform
            get() = JVM
    }
}

private val Throwable.ignore: Boolean
    get() = this::class.simpleName?.let { it == "AccessControlException" || it == "IllegalStateException" } ?: false

/**
 * Attempts to load the [Class] with the given [name] using this [ClassLoader].
 *
 * Returns `null` if the class can't be loaded.
 */
public fun ClassLoader.loadClassOrNull(name: String): Class<*>? = kotlin.runCatching { loadClass(name) }.getOrNull()

private val logger = LoggerFactory.getLogger(JVM::class.java)
private val onExitLogLock = ReentrantLock()
private var onExitLog: Path? = null
private fun (() -> Unit).toHook(): Thread {
    val stackTrace = StackTrace.get()
    return thread(start = false) {
        runCatching {
            invoke()
        }.onFailure { exception ->
            onExitLogLock.withLock {
                val exitLog = onExitLog ?: SystemLocations.Temp.createTempFile("kommons.", ".onexit.log").apply { delete() }
                onExitLog = exitLog
                exitLog.useWriter { out ->
                    out.appendLine(
                        "An exception occurred during shutdown.$LF" +
                            "The shutdown hook was registered by:$LF" +
                            "$stackTrace$LF"
                    )
                    exception.printStackTrace(PrintWriter(out, true))
                }
            }
            if (!exception.ignore && System.getenv("com.bkahlert.kommons.testing-shutdown") != "true") {
                logger.info(logger.renderType(), exception)
                logger.error(System.getenv().render(), exception)
                logger.error(
                    "An exception occurred during shutdown.$LF" +
                        "The shutdown hook was registered by:$LF" +
                        stackTrace.joinToString("$LF\t${"at".highlighted} ", postfix = LF),
                    exception
                )
            }
        }
    }
}
