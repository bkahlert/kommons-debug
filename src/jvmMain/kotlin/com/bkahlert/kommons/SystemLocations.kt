package com.bkahlert.kommons

import java.io.UncheckedIOException
import java.nio.file.FileSystems
import java.nio.file.LinkOption.NOFOLLOW_LINKS
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.io.path.createTempDirectory
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.time.Duration

/** Typical locations on this system. */
public object SystemLocations {

    /**
     * Working directory, that is, the directory in which this binary is located.
     */
    @Suppress("PropertyName")
    public val Work: Path by lazy { FileSystems.getDefault().getPath("").toAbsolutePath() }

    /**
     * Home directory of the currently logged-in user.
     */
    @Suppress("PropertyName")
    public val Home: Path by lazy { Paths.get(System.getProperty("user.home")) }

    /**
     * Directory in which temporary data can be stored.
     */
    @Suppress("PropertyName")
    public val Temp: Path by lazy { Paths.get(System.getProperty("java.io.tmpdir")) }

    /** Directory of the currently running Java distribution. */
    @Suppress("PropertyName")
    public val JavaHome: Path by lazy { Paths.get(System.getProperty("java.home")) }
}

/**
 * Runs the given [block] with a temporary directory that
 * is automatically deleted on completion.
 */
public fun <T> withTempDirectory(prefix: String = "", block: Path.() -> T): T =
    createTempDirectory(prefix).run {
        val result = runCatching(block)
        deleteRecursively()
        result.getOrThrow()
    }

/**
 * Checks if this path is inside of one of the System's temporary directories,
 * or throws an [IllegalArgumentException] otherwise.
 */
public fun requireTempSubPath(path: Path): Path =
    path.apply {
        require(fileSystem != FileSystems.getDefault() || isSubPathOf(SystemLocations.Temp)) {
            "${normalize().toAbsolutePath()} is not inside ${SystemLocations.Temp}."
        }
    }


private val cleanUpLock = ReentrantLock()

/**
 * Cleans up this directory by
 * deleting files older than the specified [keepAge] and stopping when [keepCount] files
 * are left.
 *
 * Because this process affects a potentially huge number of files,
 * this directory is required to be located somewhere inside of [SystemLocations.Temp]
 * if not explicitly specified otherwise.
 */
public fun Path.cleanUp(keepAge: Duration, keepCount: Int, enforceTempContainment: Boolean = true): Path {
    if (enforceTempContainment) requireTempSubPath(this)

    cleanUpLock.withLock {
        try {
            if (exists()) {
                listDirectoryEntriesRecursively()
                    .mapNotNull { kotlin.runCatching { if (!it.isDirectory()) it to it.age else null }.getOrNull() }
                    .sortedBy { (_, age) -> age }
                    .filter { (_, age) -> age >= keepAge }
                    .drop(keepCount)
                    .forEach { (file, _) -> file.runCatching { delete(NOFOLLOW_LINKS) } }

                listDirectoryEntriesRecursively()
                    .forEach {
                        kotlin.runCatching {
                            if (it.isDirectory() && it.listDirectoryEntries().isEmpty()) it.delete(NOFOLLOW_LINKS)
                        }
                    }

                kotlin.runCatching { if (listDirectoryEntries().isEmpty()) delete(NOFOLLOW_LINKS) }
            }
        } catch (e: UncheckedIOException) {
            if (e.cause !is NoSuchFileException) throw e
        }
    }

    return this
}
