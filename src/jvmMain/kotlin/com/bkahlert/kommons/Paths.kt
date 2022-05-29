package com.bkahlert.kommons

import com.bkahlert.kommons.debug.toHexadecimalString
import java.io.IOException
import java.nio.file.DirectoryNotEmptyException
import java.nio.file.FileSystem
import java.nio.file.FileVisitOption.FOLLOW_LINKS
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.NotDirectoryException
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.nio.file.attribute.BasicFileAttributeView
import java.nio.file.attribute.FileTime
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.stream.Stream
import kotlin.concurrent.thread
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.forEachDirectoryEntry
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.inputStream
import kotlin.io.path.isDirectory
import kotlin.io.path.pathString
import kotlin.io.path.setLastModifiedTime
import kotlin.streams.asSequence
import kotlin.streams.toList
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Alias for [isSubPathOf].
 */
public fun Path.isInside(path: Path): Boolean = isSubPathOf(path)

/**
 * Returns whether this path is a sub path of [path].
 */
public fun Path.isSubPathOf(path: Path): Boolean =
    normalize().toAbsolutePath().startsWith(path.normalize().toAbsolutePath())

/**
 * Returns this [Path] with all parent directories created.
 *
 * Example: If directory `/some/where` existed and this method was called on `/some/where/resides/a/file`,
 * the missing directories `/some/where/resides` and `/some/where/resides/a` would be created.
 */
public fun Path.createParentDirectories(): Path = apply { parent.takeUnless { it.exists() }?.createDirectories() }

/**
 * Contains since when this file was last modified.
 */
public var Path.age: Duration
    get() :Duration = (Now.millis - getLastModifiedTime().toMillis()).milliseconds
    set(value) {
        setLastModifiedTime(FileTime.from(Now.instant.minusMillis(value.inWholeMilliseconds)))
    }

/**
 * This path's creation time.
 */
public var Path.created: FileTime
    get() = Files.getFileAttributeView(this, BasicFileAttributeView::class.java).readAttributes().creationTime()
    set(fileTime) {
        Files.setAttribute(this, "basic:creationTime", fileTime)
    }

/**
 * This path's last accessed time.
 */
public var Path.lastAccessed: FileTime
    get() = Files.getFileAttributeView(this, BasicFileAttributeView::class.java).readAttributes().lastAccessTime()
    set(fileTime) {
        Files.setAttribute(this, "basic:lastAccessTime", fileTime)
    }

/**
 * This path's last modified time.
 */
public var Path.lastModified: FileTime
    get() = Files.getLastModifiedTime(this)
    set(fileTime) {
        Files.setLastModifiedTime(this, fileTime)
    }

/** [MessageDigest] implementations safe to use on all Java platforms. */
public enum class MessageDigests {
    MD5, `SHA-1`, `SHA-256`;

    public operator fun invoke(): MessageDigest =
        checkNotNull(MessageDigest.getInstance(name)) { "Failed to instantiate $name message digest" }
}

/**
 * Computes the hash of this file using the specified [messageDigest].
 */
public fun Path.computeHash(messageDigest: MessageDigest = MessageDigests.`SHA-256`()): ByteArray =
    DigestInputStream(inputStream(), messageDigest).use {
        while (it.read() != -1) {
            // clear data
        }
        it.messageDigest.digest()
    }

/**
 * Computes the hash of this file using the specified [messageDigest]
 * and returns it formatted as a checksum.
 */
public fun Path.computeChecksum(messageDigest: MessageDigest = MessageDigests.`SHA-256`()): String =
    computeHash(messageDigest).toHexadecimalString()

/**
 * Computes the [MessageDigests.MD5] hash of this file and returns it formatted as a checksum.
 */
public fun Path.computeMd5Checksum(): String = computeChecksum(MessageDigests.MD5())

/**
 * Computes the [MessageDigests.SHA-1] hash of this file and returns it formatted as a checksum.
 */
public fun Path.computeSha1Checksum(): String = computeChecksum(MessageDigests.`SHA-1`())

/**
 * Computes the [MessageDigests.SHA-256] hash of this file and returns it formatted as a checksum.
 */
public fun Path.computeSha256Checksum(): String = computeChecksum(MessageDigests.`SHA-256`())


private fun Path.getPathMatcher(glob: String): PathMatcher? {
    // avoid creating a matcher if all entries are required.
    if (glob == "*" || glob == "**" || glob == "**/*") return null

    // create a matcher and return a filter that uses it.
    return fileSystem.getPathMatcher("glob:$glob")
}

private fun Path.streamContentsRecursively(glob: String = "*", vararg options: LinkOption): Stream<Path> {
    if (!isDirectory(*options)) throw NotDirectoryException(pathString)
    val fileVisitOptions = options.let { if (it.contains(LinkOption.NOFOLLOW_LINKS)) emptyArray() else arrayOf(FOLLOW_LINKS) }
    val walk = Files.walk(this, *fileVisitOptions).filter { it != this }
    return getPathMatcher(glob)
        ?.let { matcher -> walk.filter { path -> matcher.matches(path) } }
        ?: walk
}

/**
 * Returns a list of the entries in this directory and its subdirectories
 * optionally filtered by matching against the specified [glob] pattern.
 *
 * @param glob the globbing pattern. The syntax is specified by the [FileSystem.getPathMatcher] method.
 *
 * @throws java.util.regex.PatternSyntaxException if the glob pattern is invalid.
 * @throws NotDirectoryException If this path does not refer to a directory.
 * @throws IOException If an I/O error occurs.
 *
 * @see Files.walk
 */
public fun Path.listDirectoryEntriesRecursively(glob: String = "*", vararg options: LinkOption): List<Path> =
    streamContentsRecursively(glob, *options).toList()

/**
 * Calls the [block] callback with a sequence of all entries in this directory
 * and its subdirectories optionally filtered by matching against the specified [glob] pattern.
 *
 * @param glob the globbing pattern. The syntax is specified by the [FileSystem.getPathMatcher] method.
 *
 * @throws java.util.regex.PatternSyntaxException if the glob pattern is invalid.
 * @throws NotDirectoryException If this path does not refer to a directory.
 * @throws IOException If an I/O error occurs.
 * @return the value returned by [block].
 *
 * @see Files.walk
 */
public fun <T> Path.useDirectoryEntriesRecursively(glob: String = "*", vararg options: LinkOption, block: (Sequence<Path>) -> T): T =
    streamContentsRecursively(glob, *options).use { block(it.asSequence()) }

/**
 * Performs the given [action] on each entry in this directory and its subdirectories
 * optionally filtered by matching against the specified [glob] pattern.
 *
 * @param glob the globbing pattern. The syntax is specified by the [FileSystem.getPathMatcher] method.
 *
 * @throws java.util.regex.PatternSyntaxException if the glob pattern is invalid.
 * @throws NotDirectoryException If this path does not refer to a directory.
 * @throws IOException If an I/O error occurs.
 *
 * @see Files.walk
 */
public fun Path.forEachDirectoryEntryRecursively(glob: String = "*", vararg options: LinkOption, action: (Path) -> Unit): Unit =
    streamContentsRecursively(glob, *options).use { it.forEach(action) }


/**
 * Deletes this file or empty directory.
 *
 * Returns the deletes path.
 */
public fun Path.delete(vararg options: LinkOption): Path =
    apply { if (exists(*options)) Files.delete(this) }

/**
 * Deletes this file or directory recursively.
 *
 * Symbolic links are not followed but deleted themselves.
 *
 * Returns the deletes path.
 */
public fun Path.deleteRecursively(vararg options: LinkOption, predicate: (Path) -> Boolean = { true }): Path =
    apply {
        if (exists(*options, LinkOption.NOFOLLOW_LINKS)) {
            if (isDirectory(*options, LinkOption.NOFOLLOW_LINKS)) {
                forEachDirectoryEntry { it.deleteRecursively(*options, LinkOption.NOFOLLOW_LINKS, predicate = predicate) }
            }

            if (predicate(this)) {
                var maxAttempts = 3
                var ex: Throwable? = kotlin.runCatching { delete(*options, LinkOption.NOFOLLOW_LINKS) }.exceptionOrNull()
                while (ex != null && maxAttempts > 0) {
                    maxAttempts--
                    if (ex is DirectoryNotEmptyException) {
                        val files = listDirectoryEntriesRecursively(options = options)
                        files.forEach { it.deleteRecursively(*options, predicate = predicate) }
                    }
                    Thread.sleep(100)
                    ex = kotlin.runCatching { delete(*options, LinkOption.NOFOLLOW_LINKS) }.exceptionOrNull()
                }
                if (ex != null) throw ex
            }
        }
    }

/**
 * Deletes the contents of this directory.
 *
 * Throws if this is no directory.
 */
public fun Path.deleteDirectoryEntriesRecursively(predicate: (Path) -> Boolean = { true }): Path =
    apply { listDirectoryEntriesRecursively().forEach { it.deleteRecursively(predicate = predicate) } }

/** Deletes this file when the virtual machine shuts down. */
public fun Path.deleteOnExit(recursively: Boolean = true): Path {
    val file = toFile()
    Runtime.getRuntime().addShutdownHook(thread(start = false) {
        if (recursively) file.deleteRecursively()
        else file.delete()
    })
    return this
}
