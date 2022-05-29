package com.bkahlert.kommons

import java.io.UncheckedIOException
import java.nio.file.FileSystems
import java.nio.file.LinkOption.NOFOLLOW_LINKS
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.PosixFilePermission
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.setPosixFilePermissions
import kotlin.time.Duration

/**
 * Typical locations on a system.
 */
public interface Locations {

    /**
     * Working directory, that is, the directory in which this binary is located.
     */
    public val Work: Path

    /**
     * Home directory of the currently logged-in user.
     */
    public val Home: Path

    /**
     * Directory in which temporary data can be stored.
     */
    public val Temp: Path

    /** Directory of the currently running Java distribution. */
    public val JavaHome: Path

    public companion object {

        /**
         * Default locations on a system.
         */
        public val Default: Locations = object : Locations {

            /**
             * Working directory, that is, the directory in which this binary is located.
             */
            override val Work: Path = FileSystems.getDefault().getPath("").toAbsolutePath()

            /**
             * Home directory of the currently logged-in user.
             */
            override val Home: Path = Paths.get(System.getProperty("user.home"))

            /**
             * Directory in which temporary data can be stored.
             */
            override val Temp: Path = Paths.get(System.getProperty("java.io.tmpdir"))

            /** Directory of the currently running Java distribution. */
            override val JavaHome: Path = Paths.get(System.getProperty("java.home"))
        }
    }
}


/**
 * Returns this [Path] with a path segment added.
 *
 * The path segment is created based on [base] and [extension] and a random
 * string in between.
 *
 * The newly created [Path] is guaranteed to not already exist.
 */
public tailrec fun Path.randomPath(base: String = randomString(4), extension: String = ""): Path {
    val minLength = 6
    val length = base.length + extension.length
    val randomSuffix = randomString((minLength - length).coerceAtLeast(3))
    val randomPath = resolve("${base.takeUnlessEmpty()?.let { "$it--" } ?: ""}$randomSuffix$extension")
    return randomPath.takeUnless { it.exists() } ?: randomPath(base, extension)
}


/*
 * Random directories / files
 */

/**
 * Creates a random directory inside this [Path].
 *
 * Eventually missing directories are automatically created.
 */
public fun Path.randomDirectory(base: String = randomString(4), extension: String = "-tmp"): Path =
    randomPath(base, extension).createDirectories()

/**
 * Creates a random file inside this [Path].
 *
 * Eventually missing directories are automatically created.
 */
public fun Path.randomFile(base: String = randomString(4), extension: String = ".tmp"): Path =
    randomPath(base, extension).createParentDirectories().createFile()


/*
 * Temporary directories / files
 */

/**
 * Checks if this path is inside of one of the System's temporary directories.
 *
 * @throws IllegalArgumentException this path is not inside [Locations.Temp]
 */
public fun Path.requireTempSubPath(): Path =
    apply {
        require(fileSystem != FileSystems.getDefault() || isSubPathOf(Locations.Default.Temp)) {
            "${normalize().toAbsolutePath()} is not inside ${Locations.Default.Temp}."
        }
    }

/**
 * Creates a temporary directory inside of [Locations.Temp].
 *
 * The POSIX permissions are set to `700`.
 */
public fun tempDir(base: String = "", extension: String = ""): Path =
    Locations.Default.Temp.tempDir(base, extension)

/**
 * Creates a temporary file inside of [Locations.Temp].
 *
 * The POSIX permissions are set to `700`.
 */
public fun tempFile(base: String = "", extension: String = ""): Path =
    Locations.Default.Temp.tempFile(base, extension)

/**
 * Creates a temporary directory inside this temporary directory.
 *
 * The POSIX permissions are set to `700`.
 *
 * Attempting to create a temporary directory outside of [Locations.Temp] will
 * throw an [IllegalArgumentException].
 */
public fun Path.tempDir(base: String = "", extension: String = ""): Path =
    requireTempSubPath().randomDirectory(base, extension).apply {
        setPosixFilePermissions(setOf(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE))
    }

/**
 * Creates a temporary file inside this temporary directory.
 *
 * The POSIX permissions are set to `700`.
 *
 * Attempting to create a temporary directory outside of [Locations.Temp] will
 * throw an [IllegalArgumentException].
 */
public fun Path.tempFile(base: String = "", extension: String = ""): Path =
    requireTempSubPath().randomFile(base, extension).apply {
        setPosixFilePermissions(setOf(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE))
    }

/**
 * Runs the given [block] with a temporary directory that
 * is automatically deleted on completion.
 */
public fun <T> runWithTempDir(base: String = "", extension: String = "", block: Path.() -> T): T =
    tempDir(base, extension).run {
        val returnValue: T = block()
        deleteRecursively()
        returnValue
    }


/*
 * Misc
 */
private val cleanUpLock = ReentrantLock()

/**
 * Cleans up this directory by
 * deleting files older than the specified [keepAge] and stopping when [keepCount] files
 * are left.
 *
 * Because this process affects a potentially huge number of files,
 * this directory is required to be located somewhere inside of [Locations.Temp]
 * if not explicitly specified otherwise.
 */
public fun Path.cleanUp(keepAge: Duration, keepCount: Int, enforceTempContainment: Boolean = true): Path {
    if (enforceTempContainment) requireTempSubPath()

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
