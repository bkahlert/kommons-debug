package com.bkahlert.kommons

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.Writer
import java.net.URI
import java.net.URL
import java.nio.charset.Charset
import java.nio.file.DirectoryNotEmptyException
import java.nio.file.FileSystem
import java.nio.file.FileSystemNotFoundException
import java.nio.file.FileSystems
import java.nio.file.FileVisitOption.FOLLOW_LINKS
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.NotDirectoryException
import java.nio.file.OpenOption
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.nio.file.Paths
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.StandardOpenOption.READ
import java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
import java.nio.file.StandardOpenOption.WRITE
import java.nio.file.attribute.BasicFileAttributeView
import java.nio.file.attribute.FileTime
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.concurrent.locks.ReentrantLock
import java.util.stream.Stream
import kotlin.concurrent.thread
import kotlin.concurrent.withLock
import kotlin.io.path.bufferedReader
import kotlin.io.path.bufferedWriter
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.forEachDirectoryEntry
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.inputStream
import kotlin.io.path.isDirectory
import kotlin.io.path.outputStream
import kotlin.io.path.pathString
import kotlin.io.path.reader
import kotlin.io.path.setLastModifiedTime
import kotlin.io.path.writer
import kotlin.streams.asSequence
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Checks if the file located by the **normalized** path is a directory.
 *
 * By default, symbolic links in the path are followed.
 *
 * @param options options to control how symbolic links are handled.
 *
 * @see Files.isDirectory
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun Path.isDirectoryNormalized(vararg options: LinkOption): Boolean =
    Files.isDirectory(normalize(), *options)

/**
 * Checks if the file located by the **normalized** path is a directory,
 * or throws an [IllegalArgumentException] otherwise.
 */
public fun Path.requireDirectoryNormalized(vararg options: LinkOption): Path =
    apply { require(isDirectoryNormalized(*options)) { "${normalize()} is no directory" } }

/**
 * Checks if the file located by the **normalized** path is a directory,
 * or throws an [IllegalStateException] otherwise.
 */
public fun Path.checkDirectoryNormalized(vararg options: LinkOption): Path =
    apply { check(isDirectoryNormalized(*options)) { "${normalize()} is no directory" } }

/**
 * Checks if the file located by the **normalized** path is **no** directory,
 * or throws an [IllegalArgumentException] otherwise.
 */
public fun Path.requireNoDirectoryNormalized(vararg options: LinkOption): Path =
    apply { require(!isDirectoryNormalized(*options)) { "${normalize()} is a directory" } }

/**
 * Checks if the file located by the **normalized** path is **no** directory,
 * or throws an [IllegalStateException] otherwise.
 */
public fun Path.checkNoDirectoryNormalized(vararg options: LinkOption): Path =
    apply { check(!isDirectoryNormalized(*options)) { "${normalize()} is a directory" } }

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

/** Provider for [MessageDigest] implementations safe to use on all Java platforms. */
public enum class MessageDigestProvider : () -> MessageDigest {
    MD5, @Suppress("EnumEntryName") `SHA-1`, @Suppress("EnumEntryName") `SHA-256`;

    public override operator fun invoke(): MessageDigest =
        checkNotNull(MessageDigest.getInstance(name)) { "Failed to instantiate $name message digest" }
}

/** Computes the hash of this file using the specified [messageDigestProvider]. */
public fun Path.computeHash(messageDigestProvider: () -> MessageDigest = MessageDigestProvider.`SHA-256`): ByteArray =
    DigestInputStream(inputStream(), messageDigestProvider()).use {
        while (it.read() != -1) {
            // clear data
        }
        it.messageDigest.digest()
    }

/**
 * Computes the hash of this file using the specified [messageDigestProvider]
 * and returns it formatted as a checksum.
 */
public fun Path.computeChecksum(messageDigestProvider: () -> MessageDigest = MessageDigestProvider.`SHA-256`): String =
    computeHash(messageDigestProvider).toHexadecimalString()

/**
 * Computes the [MessageDigestProvider.MD5] hash of this file and returns it formatted as a checksum.
 */
public fun Path.computeMd5Checksum(): String = computeChecksum(MessageDigestProvider.MD5)

/**
 * Computes the [MessageDigests.SHA-1] hash of this file and returns it formatted as a checksum.
 */
public fun Path.computeSha1Checksum(): String = computeChecksum(MessageDigestProvider.`SHA-1`)

/**
 * Computes the [MessageDigests.SHA-256] hash of this file and returns it formatted as a checksum.
 */
public fun Path.computeSha256Checksum(): String = computeChecksum(MessageDigestProvider.`SHA-256`)

/**
 * Resolves the specified [path] against this path
 * whereas [path] may be of a different [FileSystem] than
 * the one of this path.
 *
 * If the [FileSystem] is the same, [Path.resolve] is used.
 * Otherwise, this [FileSystem] is used—unless [path] is [absolute][Path.isAbsolute].
 *
 * In other words: [path] is resolved against this path's file system.
 * So the resolved path will reside in this path's file system, too.
 * The only exception is if [path] is absolute. Since
 * an absolute path is already "fully-qualified" it is
 * the resolved result *(and its file system the resulting file system)*.
 */
public fun Path.resolveBetweenFileSystems(path: Path): Path =
    when {
        fileSystem == path.fileSystem -> resolve(path)
        path.isAbsolute -> path
        else -> path.fold(this) { acc, segment -> acc.resolve("$segment") }
    }

/**
 * Returns a path based on the following rules:
 * - If this path **is no directory** it is returned.
 * - If this path **is a directory** the file name returned by the specified [computeFileName] relative to this directory is returned.
 *
 * Use [options] to control how symbolic links are handled.
 */
public fun Path.resolveFile(vararg options: LinkOption, computeFileName: () -> Path): Path =
    if (isDirectoryNormalized(*options)) resolve(computeFileName()).checkNoDirectoryNormalized(*options) else this

/**
 * Returns a path based on the following rules:
 * - If this path **is no directory** it is returned.
 * - If this path **is a directory** the specified [fileName] relative to this directory is returned.
 *
 * Use [options] to control how symbolic links are handled.
 */
public fun Path.resolveFile(fileName: Path, vararg options: LinkOption): Path =
    if (isDirectoryNormalized(*options)) resolve(fileName).checkNoDirectoryNormalized(*options) else this

/**
 * Returns a path based on the following rules:
 * - If this path **is no directory** it is returned.
 * - If this path **is a directory** the specified [fileName] relative to this directory is returned.
 *
 * Use [options] to control how symbolic links are handled.
 */
public fun Path.resolveFile(fileName: String, vararg options: LinkOption): Path =
    if (isDirectoryNormalized(*options)) resolve(fileName).checkNoDirectoryNormalized(*options) else this


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
    streamContentsRecursively(glob, *options).asSequence().toList()

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


/** Lock used to synchronize the creation of file systems by [usePath]. */
private val usePathLock = ReentrantLock()

/**
 * Calls the specified [block] callback
 * giving it the [Path] this [URI] points to
 * and returns the result.
 *
 * In contrast to [Paths.get] this function does not
 * only check the default file system but also loads the needed one if necessary
 * (and closes it afterwards).
 *
 * @see FileSystems.getDefault
 * @see FileSystems.newFileSystem
 */
public fun <R> URI.usePath(block: (Path) -> R): R =
    usePathLock.withLock {
        runCatching {
            block(Paths.get(this))
        }.recoverCatching { ex ->
            when (ex) {
                is FileSystemNotFoundException -> {
                    val fileSystem = FileSystems.newFileSystem(this, emptyMap<String, Any>())
                    fileSystem.use { block(it.provider().getPath(this)) }
                }
                else -> throw ex
            }
        }.getOrThrow()
    }

/**
 * Calls the specified [block] callback
 * giving it the [Path] this [URI] points to
 * and returns the result.
 *
 * In contrast to [Paths.get] this function does not
 * only check the default file system but also loads the needed one if necessary
 * (and closes it afterwards).
 *
 * @see FileSystems.getDefault
 * @see FileSystems.newFileSystem
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun <R> URL.usePath(noinline block: (Path) -> R): R =
    toURI().usePath(block)


/**
 * Calls the specified [block] callback
 * giving it a new [InputStream] of this file
 * and returns the result.
 *
 * The [InputStream] is closed down correctly whether an exception is thrown or not.
 *
 * If no [options] are present then it is equivalent to opening the file with
 * the [READ]
 * options.
 */
public inline fun <R> Path.useInputStream(vararg options: OpenOption, block: (InputStream) -> R): R =
    inputStream(*options).use(block)

/**
 * Calls the specified [block] callback
 * giving it a new [BufferedInputStream] of this file
 * and returns the result.
 *
 * The [BufferedInputStream] is closed down correctly whether an exception is thrown or not.
 *
 * If no [options] are present then it is equivalent to opening the file with
 * the [READ]
 * options.
 */
public inline fun <R> Path.useBufferedInputStream(
    vararg options: OpenOption,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    block: (BufferedInputStream) -> R
): R = inputStream(*options).buffered(bufferSize).use(block)

/**
 * Calls the specified [block] callback
 * giving it a new [InputStreamReader] of this file
 * and returns the result.
 *
 * The [InputStreamReader] is closed down correctly whether an exception is thrown or not.
 *
 * If no [options] are present then it is equivalent to opening the file with
 * the [READ]
 * options.
 */
public inline fun <R> Path.useReader(
    vararg options: OpenOption,
    charset: Charset = Charsets.UTF_8,
    block: (InputStreamReader) -> R
): R = reader(charset, *options).use(block)

/**
 * Calls the specified [block] callback
 * giving it a new [BufferedReader] of this file
 * and returns the result.
 *
 * The [BufferedReader] is closed down correctly whether an exception is thrown or not.
 *
 * If no [options] are present then it is equivalent to opening the file with
 * the [READ]
 * options.
 */
public inline fun <R> Path.useBufferedReader(
    vararg options: OpenOption,
    charset: Charset = Charsets.UTF_8,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    block: (BufferedReader) -> R
): R = bufferedReader(charset, bufferSize, *options).use(block)

/**
 * Calls the specified [block] callback
 * giving it a new [OutputStream] of this file
 * and returns the result.
 *
 * The [OutputStream] is closed down correctly whether an exception is thrown or not.
 *
 * If no [options] are present then it is equivalent to opening the file with
 * the [CREATE], [TRUNCATE_EXISTING], and [WRITE]
 * options.
 */
public inline fun Path.useOutputStream(
    vararg options: OpenOption,
    block: (OutputStream) -> Unit
): Path = apply { outputStream(*options).use(block) }

/**
 * Calls the specified [block] callback
 * giving it a new [BufferedOutputStream] of this file
 * and returns the result.
 *
 * The [BufferedOutputStream] is closed down correctly whether an exception is thrown or not.
 *
 * If no [options] are present then it is equivalent to opening the file with
 * the [CREATE], [TRUNCATE_EXISTING], and [WRITE]
 * options.
 */
public inline fun Path.useBufferedOutputStream(
    vararg options: OpenOption,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    block: (BufferedOutputStream) -> Unit
): Path = apply { outputStream(*options).buffered(bufferSize).use(block) }

/**
 * Calls the specified [block] callback
 * giving it a new [Writer] of this file
 * and returns the result.
 *
 * The [Writer] is closed down correctly whether an exception is thrown or not.
 *
 * If no [options] are present then it is equivalent to opening the file with
 * the [CREATE], [TRUNCATE_EXISTING], and [WRITE]
 * options.
 */
public inline fun Path.useWriter(
    vararg options: OpenOption,
    charset: Charset = Charsets.UTF_8,
    block: (Writer) -> Unit
): Path = apply { writer(charset, *options).use(block) }

/**
 * Calls the specified [block] callback
 * giving it a new [BufferedWriter] of this file
 * and returns the result.
 *
 * The [BufferedWriter] is closed down correctly whether an exception is thrown or not.
 *
 * If no [options] are present then it is equivalent to opening the file with
 * the [CREATE], [TRUNCATE_EXISTING], and [WRITE]
 * options.
 */
public inline fun Path.useBufferedWriter(
    vararg options: OpenOption,
    charset: Charset = Charsets.UTF_8,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    block: (BufferedWriter) -> Unit
): Path = apply { bufferedWriter(charset, bufferSize, *options).use(block) }
