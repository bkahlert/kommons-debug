package com.bkahlert.kommons.debug

import com.bkahlert.kommons.CaseStyle
import com.bkahlert.kommons.head
import com.bkahlert.kommons.tail
import com.bkahlert.kommons.withSuffix
import java.lang.reflect.AccessibleObject
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.pathString
import kotlin.reflect.KClass

/** Alias for [AccessibleObject.isAccessible] */
public var AccessibleObject.accessible: Boolean
    get() {
        @Suppress("DEPRECATION")
        return isAccessible
    }
    set(value) {
        @Suppress("DEPRECATION")
        isAccessible = value
    }

/**
 * Returns directory (e.g. `/Users/john.doe/dev/project/build/classes/kotlin/jvm/test`)
 * containing the classes the class represented by this Kotlin class belongs to, or `null` if it can't be located.
 */
public fun KClass<*>.findClassesDirectoryOrNull(): Path? = java.findClassesDirectoryOrNull()

/**
 * Returns directory (e.g. `/Users/john.doe/dev/project/build/classes/kotlin/jvm/test`)
 * containing the classes the class represented by this Java class belongs to, or `null` if it can't be located.
 */
public fun Class<*>.findClassesDirectoryOrNull(): Path? {
    val className = name
    val topLevelClassName = className.substringBefore('$')
    val topLevelClass = Thread.currentThread().contextClassLoader.loadClass(topLevelClassName) ?: error(buildString {
        append("error loading class $topLevelClassName")
        if (className != topLevelClassName) append(" (for $className)")
    })
    val url = topLevelClass.protectionDomain?.codeSource?.location ?: return null
    return Paths.get(url.toURI())
}


internal val defaultRelativeClassesPaths = arrayOf(
    Paths.get("out", "classes"),    // IDEA
    Paths.get("build", "classes"),  // Gradle
    Paths.get("target", "classes"), // Maven
)

/**
 * Returns the directory (e.g. `/Users/john.doe/dev/project/src/jvmTest/kotlin`)
 * containing the source code of the class represented by this Kotlin class or `null` if it can't be located.
 */
public fun KClass<*>.findSourceDirectoryOrNull(
    vararg relativeClassesPaths: Path = defaultRelativeClassesPaths,
): Path? = java.findSourceDirectoryOrNull(*relativeClassesPaths)

@Suppress("ReplaceCollectionCountWithSize")
private fun Path.contains(other: Path): Boolean =
    map { it.pathString }.windowed(other.count()).contains(other.map { it.pathString })

/**
 * Returns the directory (e.g. `/Users/john.doe/dev/project/src/jvmTest/kotlin`)
 * containing the source code of the class represented by this Java class or `null` if it can't be located.
 */
public fun Class<*>.findSourceDirectoryOrNull(
    vararg relativeClassesPaths: Path = defaultRelativeClassesPaths,
): Path? {
    val classesDirectory: Path = findClassesDirectoryOrNull() ?: return null
    val buildDir: Path = relativeClassesPaths.firstOrNull { classesDirectory.contains(it) } ?: error("Unknown build directory structure")
    return classesDirectory.pathString.split(buildDir.pathString, limit = 2).run {
        val sourceRoot = Paths.get(first()) / "src"
        val suffix = Paths.get(last())
        val lang = suffix.head.pathString
        val sourceDir = suffix.map { it.pathString }.tail.let { CaseStyle.camelCase.join(it) }
        sourceRoot
            .resolve(sourceDir).takeIf { it.exists() }
            ?.resolve(lang)?.takeIf { it.exists() }
            ?: return null
    }
}

/**
 * Returns the file (e.g. `/Users/john.doe/dev/project/src/jvmTest/kotlin/packages/source.kt`)
 * containing the source code of the class represented by this Kotlin class or `null` if it can't be located.
 */
public fun KClass<*>.findSourceFileOrNull(
    fileNameHint: String? = null,
    vararg relativeClassesPaths: Path = defaultRelativeClassesPaths,
): Path? = java.findSourceFileOrNull(fileNameHint, *relativeClassesPaths)

/**
 * Returns the file (e.g. `/Users/john.doe/dev/project/src/jvmTest/kotlin/packages/source.kt`)
 * containing the source code of the class represented by this Java class or `null` if it can't be located.
 */
public fun Class<*>.findSourceFileOrNull(
    fileNameHint: String? = null,
    vararg relativeClassesPaths: Path = defaultRelativeClassesPaths,
): Path? {
    val sourceDir = findSourceDirectoryOrNull(*relativeClassesPaths) ?: return null
    val pkg = name.split('.').dropLast(1)
    val fileName = (fileNameHint ?: name.split('.').last().substringBefore('$')).withSuffix(".kt")
    val fileNames: List<String> = listOf(fileName, fileName.removeSuffix(".kt").withSuffix("Kt.kt"))
    val sourceFileDir = sourceDir.resolve(Paths.get(pkg.head, *pkg.tail.toTypedArray()))
    return fileNames.map { sourceFileDir.resolve(it) }.firstOrNull { it.exists() }
}
