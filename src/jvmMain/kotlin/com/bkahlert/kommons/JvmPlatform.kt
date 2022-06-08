package com.bkahlert.kommons

/** The platforms this program runs on. */
public actual inline val Platform.Companion.Current: Platform get() = Platform.JVM

/**
 * Contains the context ClassLoader for the current [Thread].
 *
 * The context [ClassLoader] is provided by the creator of the [Thread] for use
 * by code running in this thread when loading classes and resources.
 */
public val Platform.Companion.contextClassLoader: ClassLoader
    get() = Thread.currentThread().contextClassLoader

/**
 * Attempts to load the [Class] with the given [name] using this [ClassLoader].
 *
 * Returns `null` if the class can't be loaded.
 */
public fun ClassLoader.loadClassOrNull(name: String): Class<*>? = kotlin.runCatching { loadClass(name) }.getOrNull()

private val jvmArgs: List<String>
    get() {
        val classLoader = Platform.Companion.contextClassLoader
        return classLoader.loadClassOrNull("java.lang.management.ManagementFactory")?.let {
            val runtimeMxBean: Any = it.getMethod("getRuntimeMXBean").invoke(null)
            val runtimeMxBeanClass: Class<*> = classLoader.loadClass("java.lang.management.RuntimeMXBean")
            val inputArgs: Any = runtimeMxBeanClass.getMethod("getInputArguments").invoke(runtimeMxBean)
            (inputArgs as? List<*>)?.map { arg -> arg.toString() }
        } ?: emptyList()
    }

private val jvmJavaAgents: List<String>
    get() = jvmArgs.filter { it.startsWith("-javaagent") }

private val intellijTraits: List<String> = listOf("jetbrains", "intellij", "idea", "idea_rt.jar")

/** Whether this program is started by IDEA Intellij. */
public val Platform.Companion.isIntelliJ: Boolean
    get() = runCatching { jvmJavaAgents.any { it.containsAny(intellijTraits) } }.getOrElse { false }

/** Whether this program is running in debug mode. */
public val Platform.Companion.isDebugging: Boolean
    get() = jvmArgs.any { it.startsWith("-agentlib:jdwp") } || jvmJavaAgents.any { it.contains("debugger") }
