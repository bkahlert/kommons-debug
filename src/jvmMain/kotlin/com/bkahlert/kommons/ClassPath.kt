package com.bkahlert.kommons

import java.net.URI
import java.net.URL
import java.nio.channels.SeekableByteChannel
import java.nio.file.AccessMode
import java.nio.file.CopyOption
import java.nio.file.DirectoryStream
import java.nio.file.DirectoryStream.Filter
import java.nio.file.FileStore
import java.nio.file.FileSystem
import java.nio.file.LinkOption
import java.nio.file.NoSuchFileException
import java.nio.file.OpenOption
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.nio.file.WatchEvent.Kind
import java.nio.file.WatchEvent.Modifier
import java.nio.file.WatchKey
import java.nio.file.WatchService
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileAttribute
import java.nio.file.attribute.FileAttributeView
import java.nio.file.attribute.UserPrincipalLookupService
import java.nio.file.spi.FileSystemProvider

/**
 * Returns a [Path] that points to the specified [resource].
 *
 * The explicit `classpath:` schema is optional.
 */
@Suppress("FunctionName")
public fun ClassPath(resource: String): Path {
    val normalized = resource.removePrefix("classpath:").removePrefix("/")
    val url = Platform.contextClassLoader.getResource(normalized) ?: throw NoSuchFileException(resource, null, "classpath:$resource could not be found")
    return ClassPath(url)
}

/** Returns a [Path] that points to the specified [resource]. */
@Suppress("FunctionName")
public fun ClassPath(resource: URL): Path =
    DelegatingPath(resource.also { require(it.openStream().use { it.available() > 0 }) { "$resource is unavailable" } })

/** Returns a [Path] that points to the specified [resource]. */
@Suppress("FunctionName")
public fun ClassPath(resource: URI): Path =
    ClassPath(resource.toURL())


/** A [Path] that delegates all invocations to the specified [actualResource]. */
internal class DelegatingPath(
    private val actualResource: URL,
    private val range: IntRange? = null,
) : Path {
    constructor(resource: URI) : this(resource.toURL())
    constructor(resource: Path) : this(resource.toUri())

    fun <R> useActual(transform: (Path) -> R): R {
        return actualResource.usePath { actual ->
            val actualSubPath = range?.let { actual.subpath(it.first, it.last + 1) } ?: actual
            transform(actualSubPath)
        }
    }

    override fun toString(): String = useActual { actual -> actual.toString() }
    override fun compareTo(other: Path): Int = useActual { actual -> other.useActual { actual.compareTo(it) } }
    override fun register(watcher: WatchService, events: Array<out Kind<*>>, vararg modifiers: Modifier?): WatchKey =
        throw UnsupportedOperationException("Watching files is not supported.")

    override fun getFileSystem(): FileSystem = useActual { actual -> DelegatingFileSystem(actual.fileSystem) }
    override fun isAbsolute(): Boolean = useActual { actual -> actual.isAbsolute }
    override fun getRoot(): Path? = useActual { actual -> actual.root?.let { DelegatingPath(it) } }
    override fun getFileName(): Path? = if (nameCount > 0) DelegatingPath(actualResource, nameCount.let { it - 1 until it }) else null
    override fun getParent(): Path? = useActual { actual -> actual.parent?.let { DelegatingPath(it) } }
    override fun getNameCount(): Int = useActual { actual -> actual.nameCount }
    override fun getName(index: Int): Path = DelegatingPath(actualResource, index until index + 1)
    override fun subpath(beginIndex: Int, endIndex: Int): Path = DelegatingPath(actualResource, beginIndex until endIndex)
    override fun startsWith(other: Path): Boolean = useActual { actual -> other.useActual { actual.startsWith(it) } }
    override fun endsWith(other: Path): Boolean = useActual { actual -> other.useActual { actual.endsWith(it) } }
    override fun normalize(): Path = useActual { actual -> DelegatingPath(actual.normalize()) }
    override fun resolve(other: Path): Path = useActual { actual -> DelegatingPath(other.useActual { actual.resolve(it) }) }
    override fun relativize(other: Path): Path =
        useActual { actual -> other.useActual { actual.relativize(it) } } // TODO return delegated with computed range

    override fun toUri(): URI = useActual { actual -> actual.toUri() }
    override fun toAbsolutePath(): Path = useActual { actual -> DelegatingPath(actual.toAbsolutePath()) }
    override fun toRealPath(vararg options: LinkOption?): Path = useActual { actual -> DelegatingPath(actual.toRealPath(*options)) }
}

private fun <R> Path.useActual(block: (Path) -> R): R = when (this) {
    is DelegatingPath -> useActual { block(it) }
    else -> block(this)
}

private fun <R> Path?.useNullableActual(block: (Path?) -> R): R = when (this) {
    is DelegatingPath -> useActual { block(it) }
    else -> block(this)
}

/** A [FileSystem] that delegates all invocations to the specified [actualFileSystem]. */
internal class DelegatingFileSystem(
    private val actualFileSystem: FileSystem,
) : FileSystem() {
    override fun close() = actualFileSystem.close()
    override fun provider(): FileSystemProvider = DelegatingFileSystemProvider(actualFileSystem.provider())
    override fun isOpen(): Boolean =
        actualFileSystem.isOpen

    override fun isReadOnly(): Boolean =
        actualFileSystem.isReadOnly

    override fun getSeparator(): String =
        actualFileSystem.separator

    override fun getRootDirectories(): MutableIterable<Path> =
        actualFileSystem.rootDirectories

    override fun getFileStores(): MutableIterable<FileStore> =
        actualFileSystem.fileStores

    override fun supportedFileAttributeViews(): MutableSet<String> =
        actualFileSystem.supportedFileAttributeViews()

    override fun getPath(first: String, vararg more: String?): Path =
        actualFileSystem.getPath(first, *more)

    override fun getPathMatcher(syntaxAndPattern: String?): PathMatcher =
        actualFileSystem.getPathMatcher(syntaxAndPattern)

    override fun getUserPrincipalLookupService(): UserPrincipalLookupService =
        actualFileSystem.userPrincipalLookupService

    override fun newWatchService(): WatchService =
        actualFileSystem.newWatchService()
}

/** A [FileSystemProvider] that delegates all invocations to the specified [actualProvider]. */
internal class DelegatingFileSystemProvider(
    private val actualProvider: FileSystemProvider,
) : FileSystemProvider() {
    override fun getScheme(): String = actualProvider.scheme

    override fun newFileSystem(uri: URI?, env: MutableMap<String, *>?): FileSystem =
        DelegatingFileSystem(actualProvider.newFileSystem(uri, env))

    override fun getFileSystem(uri: URI?): FileSystem =
        actualProvider.getFileSystem(uri)

    override fun getPath(uri: URI): Path =
        actualProvider.getPath(uri)

    override fun newByteChannel(path: Path?, options: MutableSet<out OpenOption>?, vararg attrs: FileAttribute<*>?): SeekableByteChannel =
        path.useNullableActual { actual -> actualProvider.newByteChannel(actual, options, *attrs) }

    override fun newDirectoryStream(dir: Path?, filter: Filter<in Path>?): DirectoryStream<Path> =
        dir.useNullableActual { actual -> actualProvider.newDirectoryStream(actual, filter) }

    override fun createDirectory(dir: Path?, vararg attrs: FileAttribute<*>?) =
        dir.useNullableActual { actual -> actualProvider.createDirectory(actual, *attrs) }

    override fun delete(path: Path?) =
        path.useNullableActual { actual -> actualProvider.delete(actual) }

    override fun copy(source: Path?, target: Path?, vararg options: CopyOption?) =
        source.useNullableActual { actualSource ->
            target.useNullableActual { actualTarget ->
                actualProvider.copy(actualSource, actualTarget, *options)
            }
        }

    override fun move(source: Path?, target: Path?, vararg options: CopyOption?) =
        source.useNullableActual { actualSource ->
            target.useNullableActual { actualTarget ->
                actualProvider.move(actualSource, actualTarget, *options)
            }
        }

    override fun isSameFile(path: Path?, path2: Path?): Boolean =
        path.useNullableActual { actual -> path2.useNullableActual { actual2 -> actualProvider.isSameFile(actual, actual2) } }

    override fun isHidden(path: Path?): Boolean =
        path.useNullableActual { actual -> actualProvider.isHidden(actual) }

    override fun getFileStore(path: Path?): FileStore =
        path.useNullableActual { actual -> actualProvider.getFileStore(actual) }

    override fun checkAccess(path: Path?, vararg modes: AccessMode?) =
        path.useNullableActual { actual -> actualProvider.checkAccess(actual, *modes) }

    override fun <V : FileAttributeView?> getFileAttributeView(path: Path?, type: Class<V>?, vararg options: LinkOption?): V =
        path.useNullableActual { actual -> actualProvider.getFileAttributeView(actual, type, *options) }

    override fun <A : BasicFileAttributes?> readAttributes(path: Path?, type: Class<A>?, vararg options: LinkOption?): A =
        path.useNullableActual { actual -> actualProvider.readAttributes(actual, type, *options) }

    override fun readAttributes(path: Path?, attributes: String?, vararg options: LinkOption?): MutableMap<String, Any> =
        path.useNullableActual { actual -> actualProvider.readAttributes(actual, attributes, *options) }

    override fun setAttribute(path: Path?, attribute: String?, value: Any?, vararg options: LinkOption?) =
        path.useNullableActual { actual -> actualProvider.setAttribute(actual, attribute, value, *options) }
}
