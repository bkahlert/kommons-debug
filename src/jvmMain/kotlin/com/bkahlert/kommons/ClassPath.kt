package com.bkahlert.kommons

import com.bkahlert.kommons.debug.inspect
import com.bkahlert.kommons.debug.trace
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
import kotlin.io.path.readBytes
import kotlin.io.path.readText
import kotlin.properties.ReadOnlyProperty

/**
 * Gets the class path resources, the specified [path] points to and applies [transform] to each.
 *
 * Also this function does its best to avoid write access by wrapping the
 * actual [FileSystem] with a write protection layer. **Write protection
 * also covers paths generated from the one provided during the [transform] call.**
 *
 * @see <a href="https://stackoverflow.com/questions/15713119/java-nio-file-path-for-a-classpath-resource"
 * >java.nio.file.Path for a classpath resource</a>
 */
public fun <T> useClassPaths(path: String, transform: (Path) -> T): List<T> {
    val normalizedPath = path.removePrefix("classpath:").removePrefix("/")
    return Platform.contextClassLoader.getResources(normalizedPath).asSequence().map { url ->
        url.usePath { classPath -> transform(classPath) }
    }.toList()
}

/**
 * Gets the class path resource, the specified [path] points to and applies [transform] to it.
 *
 * Also this function does its best to avoid write access by wrapping the
 * actual [FileSystem] with a write protection layer. **Write protection
 * also covers paths generated from the one provided during the [transform] call.**
 *
 * **This function only returns one match out of possibly many. Use [useClassPaths] to get all.**
 *
 * @see <a href="https://stackoverflow.com/questions/15713119/java-nio-file-path-for-a-classpath-resource"
 * >java.nio.file.Path for a classpath resource</a>
 * @see useClassPaths
 */
public fun <T> useClassPath(path: String, transform: (Path) -> T): T? {
    val normalizedPath = path.removePrefix("classpath:").removePrefix("/")
    return Platform.contextClassLoader.getResource(normalizedPath)?.usePath { transform(it) }
}

/**
 * Gets the class path resource, the specified [path] points to and applies [transform] to it.
 *
 * In contrast to [useClassPath] this function throws if no resource could be found.
 *
 * @see useClassPath
 */
public fun <T> useRequiredClassPath(path: String, transform: (Path) -> T): T =
    useClassPath(path, transform) ?: throw NoSuchFileException(path)

/**
 * Reads the class path resource, the specified [path] points to and returns it.
 *
 * Returns `null` if no resource could be found.
 *
 * @see requireClassPathText
 */
public fun readClassPathText(path: String): String? =
    useClassPath(path) { it.readText() }

/**
 * Reads the class path resource, the specified [path] points to and returns it.
 *
 * Throws an exception throws if no resource could be found.
 *
 * @see readClassPathText
 */
public fun requireClassPathText(path: String): String =
    useRequiredClassPath(path) { it.readText() }

/**
 * Reads the class path resource, the specified [path] points to and returns it.
 *
 * Returns `null` if no resource could be found.
 *
 * @see requireClassPathBytes
 */
public fun readClassPathBytes(path: String): ByteArray? =
    useClassPath(path) { it.readBytes() }

/**
 * Reads the class path resource, the specified [path] points to and returns it.
 *
 * Throws an exception throws if no resource could be found.
 *
 * @see readClassPathBytes
 */
public fun requireClassPathBytes(path: String): ByteArray =
    useRequiredClassPath(path) { it.readBytes() }

/**
 * Gets a proxied class path resource that get only accesses the moment
 * an operation is executed. Should a [FileSystem] need to be loaded this
 * will be done transparently as it will be closed afterwards.
 */
public fun classPath(path: String): ReadOnlyProperty<Any?, Path> = ReadOnlyProperty<Any?, Path> { _, _ -> DelegatingPath(path) }

// TODO rename deletaing path and delete
public fun ClassPath(path: String): Path {
    return DelegatingPath(path)
}

// TODO rename deletaing path and delete
public fun ClassPath(path: URL): Path {
    return DelegatingPath(path)
}

// TODO rename deletaing path and delete
public fun ClassPath(path: URI): Path {
    return DelegatingPath(path)
}

private class DelegatingFileSystemProvider(val fileSystemProvider: FileSystemProvider) : FileSystemProvider() {
    override fun getScheme(): String = fileSystemProvider.scheme

    override fun newFileSystem(uri: URI?, env: MutableMap<String, *>?): FileSystem =
        DelegatingFileSystem(fileSystemProvider.newFileSystem(uri, env))

    override fun getFileSystem(uri: URI?): FileSystem =
        fileSystemProvider.getFileSystem(uri)

    override fun getPath(uri: URI): Path =
        fileSystemProvider.getPath(uri)

    override fun newByteChannel(path: Path?, options: MutableSet<out OpenOption>?, vararg attrs: FileAttribute<*>?): SeekableByteChannel {
        return if (path is DelegatingPath) {
            path.resource.usePath { fileSystemProvider.newByteChannel(it.trace("newByteChannel"), options, *attrs) }
        } else {
            fileSystemProvider.newByteChannel(path.trace("newByteChannel"), options, *attrs)
        }
    }

    override fun newDirectoryStream(dir: Path?, filter: Filter<in Path>?): DirectoryStream<Path> =
        fileSystemProvider.newDirectoryStream(dir, filter)

    override fun createDirectory(dir: Path?, vararg attrs: FileAttribute<*>?) =
        fileSystemProvider.createDirectory(dir, *attrs)

    override fun delete(path: Path?) =
        fileSystemProvider.delete(path)

    override fun copy(source: Path?, target: Path?, vararg options: CopyOption?) =
        fileSystemProvider.copy(source, target, *options)

    override fun move(source: Path?, target: Path?, vararg options: CopyOption?) =
        fileSystemProvider.move(source, target, *options)

    override fun isSameFile(path: Path?, path2: Path?): Boolean =
        fileSystemProvider.isSameFile(path, path2)

    override fun isHidden(path: Path?): Boolean =
        fileSystemProvider.isHidden(path)

    override fun getFileStore(path: Path?): FileStore =
        fileSystemProvider.getFileStore(path)

    override fun checkAccess(path: Path?, vararg modes: AccessMode?) =
        fileSystemProvider.checkAccess(path, *modes)

    override fun <V : FileAttributeView?> getFileAttributeView(path: Path?, type: Class<V>?, vararg options: LinkOption?): V =
        fileSystemProvider.getFileAttributeView(path, type, *options)

    override fun <A : BasicFileAttributes?> readAttributes(path: Path?, type: Class<A>?, vararg options: LinkOption?): A =
        fileSystemProvider.readAttributes(path, type, *options)

    override fun readAttributes(path: Path?, attributes: String?, vararg options: LinkOption?): MutableMap<String, Any> =
        fileSystemProvider.readAttributes(path, attributes, *options)

    override fun setAttribute(path: Path?, attribute: String?, value: Any?, vararg options: LinkOption?) =
        fileSystemProvider.setAttribute(path, attribute, value, *options)
}

private class DelegatingFileSystem(val fileSystem: FileSystem) : FileSystem() {
    override fun close() = fileSystem.close()
    override fun provider(): FileSystemProvider = DelegatingFileSystemProvider(fileSystem.provider())
    override fun isOpen(): Boolean =
        fileSystem.isOpen

    override fun isReadOnly(): Boolean =
        fileSystem.isReadOnly

    override fun getSeparator(): String =
        fileSystem.separator

    override fun getRootDirectories(): MutableIterable<Path> =
        fileSystem.rootDirectories

    override fun getFileStores(): MutableIterable<FileStore> =
        fileSystem.fileStores

    override fun supportedFileAttributeViews(): MutableSet<String> =
        fileSystem.supportedFileAttributeViews()

    override fun getPath(first: String, vararg more: String?): Path =
        fileSystem.getPath(first, *more)

    override fun getPathMatcher(syntaxAndPattern: String?): PathMatcher =
        fileSystem.getPathMatcher(syntaxAndPattern)

    override fun getUserPrincipalLookupService(): UserPrincipalLookupService =
        fileSystem.getUserPrincipalLookupService()

    override fun newWatchService(): WatchService =
        fileSystem.newWatchService()
}

@JvmInline
private value class DelegatingPath(val resource: URL) : Path {
    constructor(resource: URI) : this(resource.toURL())
    constructor(resource: String) : this(kotlin.run {
        val normalized = resource.removePrefix("classpath:").removePrefix("/")
        Platform.contextClassLoader.getResource(normalized)
    } ?: throw NoSuchFileException(resource, null, "classpath:$resource could not be found"))

    fun <T> op(name: String, transform: Path.() -> T): T {
        name.toScreamingSnakeCasedString().trace
        return resource.usePath { transform(it.inspect("path") { it::class }) }.inspect("result") { it.toString() }
    }

    override fun compareTo(other: Path): Int = op("compareTo") { this.compareTo(other) }
    override fun register(watcher: WatchService, events: Array<out Kind<*>>, vararg modifiers: Modifier?): WatchKey =
        throw UnsupportedOperationException("Watching files is not supported.")

    override fun getFileSystem(): FileSystem = op("getFileSystem") { DelegatingFileSystem(fileSystem) }
    override fun isAbsolute(): Boolean = op("isAbsolute") { isAbsolute }
    override fun getRoot(): Path = op("getRoot") { root }
    override fun getFileName(): Path = op("getFileName") { fileName }
    override fun getParent(): Path = op("getParent") { parent }
    override fun getNameCount(): Int = op("getNameCount") { nameCount }
    override fun getName(index: Int): Path = op("getName") { getName(index) }
    override fun subpath(beginIndex: Int, endIndex: Int): Path = op("subpath") { subpath(beginIndex, endIndex) }
    override fun startsWith(other: Path): Boolean = op("startsWith") { startsWith(other) }
    override fun endsWith(other: Path): Boolean = op("endsWith") { endsWith(other) }
    override fun normalize(): Path = op("normalize") { normalize() }
    override fun resolve(other: Path): Path = op("resolve") { resolveBetweenFileSystems(other) }
    override fun relativize(other: Path): Path = op("relativize") { relativize(other) }
    override fun toUri(): URI = op("toUri") { toUri() }
    override fun toAbsolutePath(): Path = op("toAbsolutePath") { toAbsolutePath() }
    override fun toRealPath(vararg options: LinkOption?): Path = op("toRealPath") { toRealPath() }
}
