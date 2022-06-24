package com.bkahlert.kommons

import com.bkahlert.kommons.DeleteOnExecTestHelper.Variant
import com.bkahlert.kommons.test.directoryWithTwoFiles
import com.bkahlert.kommons.test.singleFile
import com.bkahlert.kommons.test.tempJarFileSystem
import com.bkahlert.kommons.test.test
import io.kotest.assertions.asClue
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import io.kotest.matchers.paths.shouldBeADirectory
import io.kotest.matchers.paths.shouldBeEmptyDirectory
import io.kotest.matchers.paths.shouldBeSymbolicLink
import io.kotest.matchers.paths.shouldExist
import io.kotest.matchers.paths.shouldNotBeEmptyDirectory
import io.kotest.matchers.paths.shouldNotExist
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldStartWith
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.net.URI
import java.net.URL
import java.nio.file.DirectoryNotEmptyException
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.LinkOption.NOFOLLOW_LINKS
import java.nio.file.NoSuchFileException
import java.nio.file.NotDirectoryException
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.ProviderNotFoundException
import java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
import java.nio.file.attribute.FileTime
import kotlin.io.path.appendText
import kotlin.io.path.createDirectory
import kotlin.io.path.createFile
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.pathString
import kotlin.io.path.readBytes
import kotlin.io.path.readText
import kotlin.system.exitProcess
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class PathsKtTest {

    @Test fun create_temp_file(@TempDir tempDir: Path) = test {
        tempDir.createTempFile() should {
            it.shouldExist()
            it.isRegularFile()
            it.parent.pathString shouldBe kotlin.io.path.createTempFile(tempDir).parent.pathString
        }
    }

    @Test fun create_temp_directory(@TempDir tempDir: Path) = test {
        tempDir.createTempDirectory() should {
            it.shouldExist()
            it.isDirectory()
            it.parent.pathString shouldBe kotlin.io.path.createTempDirectory(tempDir).parent.pathString
        }
    }

    @Test fun create_temp_text_file(@TempDir tempDir: Path) = test {
        createTempTextFile("text") should {
            it.shouldExist()
            it.readText() shouldBe "text"
        }
        tempDir.createTempTextFile("text") should {
            it.shouldExist()
            it.readText() shouldBe "text"
        }
    }

    @Test fun create_temp_binary_file(@TempDir tempDir: Path) = test {
        createTempBinaryFile(bytes) should {
            it.shouldExist()
            it.readBytes() shouldBe bytes
        }
        tempDir.createTempBinaryFile(bytes) should {
            it.shouldExist()
            it.readBytes() shouldBe bytes
        }
    }

    @Test fun create_text_file(@TempDir tempDir: Path) = test {
        tempDir.resolve("file.txt").createTextFile("text") should {
            it.shouldExist()
            it.readText() shouldBe "text"
        }
    }

    @Test fun create_binary_file(@TempDir tempDir: Path) = test {
        tempDir.resolve("file").createBinaryFile(bytes) should {
            it.shouldExist()
            it.readBytes() shouldBe bytes
        }
    }

    @Test fun is_normalized_directory(@TempDir tempDir: Path) = test {
        tempDir.isNormalizedDirectory() shouldBe true
        (tempDir / "foo" / "..").isNormalizedDirectory() shouldBe true
        (tempDir / "foo").isNormalizedDirectory() shouldBe false
    }

    @Test fun require_normalized_directory(@TempDir tempDir: Path) = test {
        tempDir should { requireNormalizedDirectory(it) shouldBe it }
        tempDir / "foo" / ".." should { requireNormalizedDirectory(it) shouldBe it }
        shouldThrow<IllegalArgumentException> { requireNormalizedDirectory(tempDir / "foo") }
    }

    @Test fun check_normalized_directory(@TempDir tempDir: Path) = test {
        tempDir should { checkNormalizedDirectory(it) shouldBe it }
        tempDir / "foo" / ".." should { checkNormalizedDirectory(it) shouldBe it }
        shouldThrow<IllegalStateException> { checkNormalizedDirectory(tempDir / "foo") }
    }

    @Test fun require_normalized_no_directory(@TempDir tempDir: Path) = test {
        shouldThrow<IllegalArgumentException> { requireNoDirectoryNormalized(tempDir) }
        shouldThrow<IllegalArgumentException> { requireNoDirectoryNormalized(tempDir / "foo" / "..") }
        tempDir / "foo" should { requireNoDirectoryNormalized(it) shouldBe it }
    }

    @Test fun check_normalized_no_directory(@TempDir tempDir: Path) = test {
        shouldThrow<IllegalStateException> { checkNoDirectoryNormalized(tempDir) }
        shouldThrow<IllegalStateException> { checkNoDirectoryNormalized(tempDir / "foo" / "..") }
        tempDir / "foo" should { checkNoDirectoryNormalized(it) shouldBe it }
    }

    @Nested
    inner class IsSubPathOf {

        @Test
        fun `should return true if child`(@TempDir tempDir: Path) = test {
            (tempDir / "child").isInside(tempDir) shouldBe true
            (tempDir / "child").isSubPathOf(tempDir) shouldBe true
        }

        @Test
        fun `should return true if descendent`(@TempDir tempDir: Path) = test {
            (tempDir / "child1" / "child2").isInside(tempDir) shouldBe true
            (tempDir / "child1" / "child2").isSubPathOf(tempDir) shouldBe true
        }

        @Test
        fun `should return true if path is obscure`(@TempDir tempDir: Path) = test {
            (tempDir / "child1" / ".." / "child2").isInside(tempDir) shouldBe true
            (tempDir / "child1" / ".." / "child2").isSubPathOf(tempDir) shouldBe true
        }

        @Test
        fun `should return true if same`(@TempDir tempDir: Path) = test {
            tempDir.isInside(tempDir) shouldBe true
            tempDir.isSubPathOf(tempDir) shouldBe true
        }

        @Test
        fun `should return false if not inside`(@TempDir tempDir: Path) = test {
            tempDir.isInside(tempDir / "child") shouldBe false
            tempDir.isSubPathOf(tempDir / "child") shouldBe false
        }
    }

    @Test fun create_parent_directories(@TempDir tempDir: Path) = test {
        val file = tempDir.resolve("some/dir/some/file")
        file.createParentDirectories().parent should {
            it.shouldExist()
            it.shouldBeADirectory()
            it.shouldBeEmptyDirectory()
        }
    }

    @Test
    fun age(@TempDir tempDir: Path) = test {
        with(tempDir.resolve("existing").createFile().apply { lastModified -= 1.days }) {
            age should {
                it shouldBeGreaterThanOrEqualTo (1.days - 5.seconds)
                it shouldBeLessThanOrEqualTo (1.days + 5.seconds)
            }
            age = 42.days
            age should {
                it shouldBeGreaterThanOrEqualTo (42.days - 5.seconds)
                it shouldBeLessThanOrEqualTo (42.days + 5.seconds)
            }
        }

        shouldThrow<NoSuchFileException> { tempDir.resolve("missing").age }
        shouldThrow<NoSuchFileException> { tempDir.resolve("missing").age = 42.days; @Suppress("RedundantUnitExpression") Unit }
    }


    @Nested
    inner class CreatedKtTest {

        @Test
        fun `should read created`(@TempDir tempDir: Path) = test {
            tempDir.createTempFile().created.toInstant() should {
                it shouldBeLessThan (Now + 1.minutes)
                it shouldBeGreaterThan (Now - 1.minutes)
            }
        }

        @Test
        fun `should write created`(@TempDir tempDir: Path) = test {
            tempDir.createTempFile().apply {
                created = (Now - 20.minutes).toFileTime()
            }.created should {
                it shouldBeLessThan (Now + 21.minutes).toFileTime()
                it shouldBeGreaterThan (Now - 21.minutes).toFileTime()
            }
        }
    }

    @Nested
    inner class LastAccessedKtTest {

        @Test
        fun `should read last accessed`(@TempDir tempDir: Path) = test {
            tempDir.createTempFile().lastAccessed.toInstant() should {
                it shouldBeLessThan (Now + 1.minutes)
                it shouldBeGreaterThan (Now - 1.minutes)
            }
        }

        @Test
        fun `should write last accessed`(@TempDir tempDir: Path) = test {
            tempDir.createTempFile().apply {
                lastAccessed = FileTime.from(Now - 20.minutes)
            }.lastAccessed.toInstant() should {
                it shouldBeLessThan (Now + 21.minutes)
                it shouldBeGreaterThan (Now - 21.minutes)
            }
        }
    }

    @Nested
    inner class LastModifiedKtTest {

        @Test
        fun `should read last modified`(@TempDir tempDir: Path) = test {
            tempDir.createTempFile().lastModified.toInstant() should {
                it shouldBeLessThan (Now + 1.minutes)
                it shouldBeGreaterThan (Now - 1.minutes)
            }
        }

        @Test
        fun `should write last modified`(@TempDir tempDir: Path) = test {
            tempDir.createTempFile().apply {
                lastModified = FileTime.from(Now - 20.minutes)
            }.lastModified.toInstant() should {
                it shouldBeLessThan (Now + 21.minutes)
                it shouldBeGreaterThan (Now - 21.minutes)
            }
        }
    }

    @Test fun resolve_between_file_systems(@TempDir tempDir: Path) {
        // same filesystem
        tempDir.tempJarFileSystem().use { jarFileSystem ->
            val receiverJarPath: Path = jarFileSystem.rootDirectories.first().createTempDirectory().createTempDirectory()
            val relativeJarPath: Path = receiverJarPath.parent.relativize(receiverJarPath)
            receiverJarPath.resolveBetweenFileSystems(relativeJarPath)
                .shouldBe(receiverJarPath.resolve(receiverJarPath.last()))
        }
        // same filesystem
        with(tempDir.createTempDirectory()) {
            val receiverFilePath = createTempDirectory()
            val relativeFilePath: Path = receiverFilePath.parent.relativize(receiverFilePath)
            receiverFilePath.resolveBetweenFileSystems(relativeFilePath)
                .shouldBe(receiverFilePath.resolve(receiverFilePath.last()))
        }

        // absolute other path
        tempDir.tempJarFileSystem().use { jarFileSystem ->
            val receiverJarPath: Path = jarFileSystem.rootDirectories.first().createTempDirectory().createTempFile()
            val absoluteJarPath: Path = jarFileSystem.rootDirectories.first()
            receiverJarPath.resolveBetweenFileSystems(absoluteJarPath)
                .shouldBe(absoluteJarPath)
        }
        // absolute other path
        tempDir.tempJarFileSystem().use { jarFileSystem ->
            val receiverFilePath_: Path = tempDir.createTempDirectory().createTempFile()
            val absoluteJarPath: Path = jarFileSystem.rootDirectories.first()
            receiverFilePath_.resolveBetweenFileSystems(absoluteJarPath)
                .shouldBe(absoluteJarPath)
        }
        // absolute other path
        tempDir.tempJarFileSystem().use { jarFileSystem ->
            val receiverJarPath: Path = jarFileSystem.rootDirectories.first().createTempDirectory().createTempFile()
            val otherFileAbsPath: Path = tempDir.createTempDirectory()
            receiverJarPath.resolveBetweenFileSystems(otherFileAbsPath)
                .shouldBe(otherFileAbsPath)
        }
        // absolute other path
        with(tempDir) {
            val receiverFilePath = createTempDirectory().createTempFile()
            val otherFileAbsPath: Path = createTempDirectory()
            receiverFilePath.resolveBetweenFileSystems(otherFileAbsPath)
                .shouldBe(otherFileAbsPath)
        }

        // relative other path
        with(tempDir) {
            val receiverFilePath: Path = createTempDirectory().createTempFile()
            tempJarFileSystem().use { jarFileSystem ->
                val relativeJarPath: Path = jarFileSystem.rootDirectories.first().createTempDirectory().createTempFile()
                    .let { absPath -> absPath.parent.relativize(absPath) }
                receiverFilePath.resolveBetweenFileSystems(relativeJarPath)
                    .shouldBe(receiverFilePath.resolve(relativeJarPath.first().toString()))
            }
        }
        // relative other path
        with(tempDir) {
            val relativeFilePath: Path = createTempDirectory().createTempFile()
                .let { absPath -> absPath.parent.relativize(absPath) }
            tempJarFileSystem().use { jarFileSystem ->
                val receiverJarPath: Path = jarFileSystem.rootDirectories.first().createTempDirectory().createTempFile()
                receiverJarPath.resolveBetweenFileSystems(relativeFilePath)
                    .shouldBe(receiverJarPath.resolve(relativeFilePath.first().toString()))
            }
        }
    }

    @Test
    fun resolve_random(@TempDir tempDir: Path) = test {
        tempDir.resolveRandom() should {
            it.shouldNotExist()
        }
        tempDir.resolveRandom("prefix", "suffix") should {
            it.shouldNotExist()
            it.fileName.pathString shouldStartWith "prefix"
            it.fileName.pathString shouldEndWith "suffix"
        }
    }

    @Test
    fun resolve_file(@TempDir tempDir: Path) = test {
        tempDir.resolveFile { Paths.get("dir", "file") } shouldBe tempDir / "dir" / "file"
        tempDir.resolveFile(Paths.get("dir", "file")) shouldBe tempDir / "dir" / "file"
        tempDir.resolveFile("dir/file") shouldBe tempDir / "dir" / "file"
        shouldThrow<IllegalStateException> { tempDir.resolveFile { Paths.get("dir", "..") } }
        shouldThrow<IllegalStateException> { tempDir.resolveFile(Paths.get("dir", "..")) }
        shouldThrow<IllegalStateException> { tempDir.resolveFile("dir/..") }
    }

    @Test
    fun list_directory_entries_recursively(@TempDir tempDir: Path) = test {
        val dir = tempDir.directoryWithTwoFiles()

        dir.listDirectoryEntriesRecursively()
            .map { it.pathString } shouldContainExactlyInAnyOrder listOf(
            dir.resolve("example.html").pathString,
            dir.resolve("sub-dir").pathString,
            dir.resolve("sub-dir/config.txt").pathString,
        )

        dir.listDirectoryEntriesRecursively("**/*.*")
            .map { it.pathString } shouldContainExactlyInAnyOrder listOf(
            dir.resolve("example.html").pathString,
            dir.resolve("sub-dir/config.txt").pathString,
        )

        shouldThrow<NotDirectoryException> { tempDir.createTempFile().listDirectoryEntriesRecursively() }
    }

    @Nested
    inner class ListDirectoryEntriesRecursivelyOperation {

        @Test
        fun `should delete directory contents`(@TempDir tempDir: Path) = test {
            val dir = tempDir.resolve("dir")
            dir.directoryWithTwoFiles()

            dir.deleteDirectoryEntriesRecursively().shouldExist()
            dir.shouldBeEmptyDirectory()
        }

        @Test
        fun `should delete filtered directory contents`(@TempDir tempDir: Path) = test {
            val dir = tempDir.resolve("dir")
            val exception = dir.directoryWithTwoFiles().listDirectoryEntriesRecursively().first()

            dir.deleteDirectoryEntriesRecursively { it != exception && !it.isDirectory() }.shouldExist()
            dir.listDirectoryEntries().map { it.pathString }.shouldNotContain(exception.pathString)
        }

        @Test
        fun `should throw on non-directory`(@TempDir tempDir: Path) = test {
            val file = tempDir.resolve("file").createFile()
            shouldThrow<NotDirectoryException> { file.deleteDirectoryEntriesRecursively() }
        }
    }

    @Test
    fun use_directory_entries_recursively(@TempDir tempDir: Path) = test {
        val dir = tempDir.directoryWithTwoFiles()

        dir.useDirectoryEntriesRecursively { seq -> seq.map { it.fileName.pathString }.sorted().joinToString() }
            .shouldBe("config.txt, example.html, sub-dir")

        dir.useDirectoryEntriesRecursively("**/*.*") { seq -> seq.map { it.fileName.pathString }.sorted().joinToString() }
            .shouldBe("config.txt, example.html")

        shouldThrow<NotDirectoryException> { tempDir.createTempFile().useDirectoryEntriesRecursively { } }
    }

    @Test
    fun for_each_directory_entries_recursively(@TempDir tempDir: Path) = test {
        val dir = tempDir.directoryWithTwoFiles()

        buildList { dir.forEachDirectoryEntryRecursively { add(it) } }
            .map { it.pathString } shouldContainExactlyInAnyOrder listOf(
            dir.resolve("example.html").pathString,
            dir.resolve("sub-dir").pathString,
            dir.resolve("sub-dir/config.txt").pathString,
        )

        buildList { dir.forEachDirectoryEntryRecursively("**/*.*") { add(it) } }
            .map { it.pathString } shouldContainExactlyInAnyOrder listOf(
            dir.resolve("example.html").pathString,
            dir.resolve("sub-dir/config.txt").pathString,
        )

        shouldThrow<NotDirectoryException> { tempDir.createTempFile().forEachDirectoryEntryRecursively { } }
    }

    @Test fun copy_to_directory(@TempDir tempDir: Path) = test {
        val file = tempDir.singleFile("file")
        val dir = tempDir.resolve("dir")

        shouldThrow<NoSuchFileException> { file.copyToDirectory(dir) }

        file.copyToDirectory(dir, createDirectories = true) should {
            it.parent.fileName.pathString shouldBe "dir"
            it.fileName.pathString shouldBe "file"
            it.readText() shouldBe "content file"
        }

        file.appendText("-overwritten")
        shouldThrow<FileAlreadyExistsException> { file.copyToDirectory(dir) }

        file.copyToDirectory(dir, overwrite = true) should {
            it.parent.fileName.pathString shouldBe "dir"
            it.fileName.pathString shouldBe "file"
            it.readText() shouldBe "content file-overwritten"
        }
    }


    @Nested
    inner class Delete {

        @Test
        fun `should delete file`(@TempDir tempDir: Path) = test {
            val file = tempDir.singleFile()
            file.delete().shouldNotExist()
            tempDir.shouldBeEmptyDirectory()
        }

        @Test
        fun `should delete empty directory`(@TempDir tempDir: Path) = test {
            val dir = tempDir.resolve("dir").createDirectory()
            dir.delete().shouldNotExist()
            tempDir.shouldBeEmptyDirectory()
        }

        @Test
        fun `should throw on non-empty directory`(@TempDir tempDir: Path) = test {
            val dir = tempDir.resolve("dir").createDirectory().apply { singleFile() }
            shouldThrow<DirectoryNotEmptyException> { dir.delete() }
        }

        @Test
        fun `should delete non-existing file`(@TempDir tempDir: Path) = test {
            val file = tempDir.resolve("file")
            file.delete().asClue { it.exists() shouldBe false }
            tempDir.shouldBeEmptyDirectory()
        }

        @Nested
        inner class WithNoFollowLinks {

            @Test
            fun `should delete symbolic link itself`(@TempDir tempDir: Path) = test {
                val symbolicLink = tempDir.symbolicLink()
                symbolicLink.delete()

                symbolicLink.delete(NOFOLLOW_LINKS).asClue { it.exists(NOFOLLOW_LINKS) shouldBe false }
                tempDir.shouldBeEmptyDirectory()
            }
        }

        @Nested
        inner class WithoutNoFollowLinks {

            @Test
            fun `should not delete symbolic link itself`(@TempDir tempDir: Path) = test {
                val symbolicLink = tempDir.symbolicLink()

                symbolicLink.shouldBeSymbolicLink()
                tempDir.shouldNotBeEmptyDirectory()
            }
        }
    }

    @Nested
    inner class DeleteRecursively {

        @Test
        fun `should delete file`(@TempDir tempDir: Path) = test {
            tempDir.singleFile().deleteRecursively().shouldNotExist()
            tempDir.shouldBeEmptyDirectory()
        }

        @Test
        fun `should delete empty directory`(@TempDir tempDir: Path) = test {
            tempDir.resolve("dir").createDirectory().deleteRecursively().shouldNotExist()
            tempDir.shouldBeEmptyDirectory()
        }

        @Test
        fun `should delete non-empty directory`(@TempDir tempDir: Path) = test {
            tempDir.resolve("dir").createDirectory().apply { singleFile() }.deleteRecursively().shouldNotExist()
            tempDir.shouldBeEmptyDirectory()
        }

        @Test
        fun `should delete non-existing file`(@TempDir tempDir: Path) = test {
            tempDir.resolve("file").deleteRecursively().shouldNotExist()
            tempDir.shouldBeEmptyDirectory()
        }

        @Test
        fun `should delete complex file tree`(@TempDir tempDir: Path) = test {
            val dir = tempDir.resolve("dir")
            dir.directoryWithTwoFiles().symbolicLink()

            dir.deleteRecursively().shouldNotExist()
            tempDir.shouldBeEmptyDirectory()
        }

        @Test
        fun `should delete filtered files`(@TempDir tempDir: Path) = test {
            val dir = tempDir.resolve("dir")
            val exception = dir.directoryWithTwoFiles().listDirectoryEntriesRecursively().first()

            dir.deleteRecursively { it != exception && !it.isDirectory() }.shouldExist()
            dir.listDirectoryEntries().map { it.pathString }.shouldNotContain(exception.pathString)
        }
    }

    @Test fun delete_on_exit(@TempDir tempDir: Path) = test {
        tempDir.singleFile("file-delete-default").asClue {
            IsolatedProcess.exec(DeleteOnExecTestHelper::class, Variant.Default.name, it.pathString) shouldBe 0
            it.shouldNotExist()
        }
        tempDir.singleFile("file-delete-recursively").asClue {
            IsolatedProcess.exec(DeleteOnExecTestHelper::class, Variant.Recursively.name, it.pathString) shouldBe 0
            it.shouldNotExist()
        }
        tempDir.singleFile("file-delete-non-recursively").asClue {
            IsolatedProcess.exec(DeleteOnExecTestHelper::class, Variant.NonRecursively.name, it.pathString) shouldBe 0
            it.shouldNotExist()
        }

        tempDir.directoryWithTwoFiles("dir-delete-default").asClue {
            IsolatedProcess.exec(DeleteOnExecTestHelper::class, Variant.Default.name, it.pathString) shouldBe 0
            it.shouldNotExist()
        }
        tempDir.directoryWithTwoFiles("dir-delete-recursively").asClue {
            IsolatedProcess.exec(DeleteOnExecTestHelper::class, Variant.Recursively.name, it.pathString) shouldBe 0
            it.shouldNotExist()
        }
        tempDir.directoryWithTwoFiles("dir-delete-non-recursively").asClue {
            IsolatedProcess.exec(DeleteOnExecTestHelper::class, Variant.NonRecursively.name, it.pathString) shouldBe 0
            it.shouldExist()
        }

        tempDir.resolve("missing").asClue {
            IsolatedProcess.exec(DeleteOnExecTestHelper::class, Variant.Default.name, it.pathString) shouldBe 0
            it.shouldNotExist()
        }
    }

    @Test fun use_path(@TempDir tempDir: Path) = test {
        val regularFile = tempDir.singleFile(content = "foo")
        regularFile.toUri().usePath { it.readText() } shouldBe "foo"
        regularFile.toUri().toURL().usePath { it.readText() } shouldBe "foo"

        standardLibraryClassPathClass.toURI().usePath { it.readBytes() } shouldBe standardLibraryClassPathClassBytes
        standardLibraryClassPathClass.usePath { it.readBytes() } shouldBe standardLibraryClassPathClassBytes

        val classPathTextPath = checkNotNull(Platform.contextClassLoader.getResource(classPathTextFile))
        classPathTextPath.toURI().usePath { it.readBytes() } shouldBe classPathTextFileBytes
        classPathTextPath.usePath { it.readBytes() } shouldBe classPathTextFileBytes

        shouldThrow<ProviderNotFoundException> { URI("https://example.com").usePath { } }
        shouldThrow<ProviderNotFoundException> { URL("https://example.com").usePath { } }
    }

    @Test fun use_input_stream(@TempDir tempDir: Path) = test {
        tempDir.singleFile(content = "abc").useInputStream { it.readBytes().decodeToString() } shouldBe "abc"
        shouldThrow<NoSuchFileException> { tempDir.resolveRandom().useInputStream {} }
    }

    @Test fun use_buffered_input_stream(@TempDir tempDir: Path) = test {
        tempDir.singleFile(content = "abc").useBufferedInputStream { it.readBytes().decodeToString() } shouldBe "abc"
        shouldThrow<NoSuchFileException> { tempDir.resolveRandom().useBufferedInputStream {} }
    }

    @Test fun use_reader(@TempDir tempDir: Path) = test {
        tempDir.singleFile(content = "abc").useReader { it.readText() } shouldBe "abc"
        shouldThrow<NoSuchFileException> { tempDir.resolveRandom().useReader {} }
    }

    @Test fun use_buffered_reader(@TempDir tempDir: Path) = test {
        tempDir.singleFile(content = "abc").useBufferedReader { it.readText() } shouldBe "abc"
        shouldThrow<NoSuchFileException> { tempDir.resolveRandom().useBufferedReader {} }
    }

    @Test fun use_output_stream(@TempDir tempDir: Path) = test {
        tempDir.resolve("file").useOutputStream { it.write("abc".encodeToByteArray()) }.readText() shouldBe "abc"
        shouldThrow<NoSuchFileException> { tempDir.resolveRandom().useOutputStream(TRUNCATE_EXISTING) {} }
    }

    @Test fun use_buffered_output_stream(@TempDir tempDir: Path) = test {
        tempDir.resolve("file").useBufferedOutputStream { it.write("abc".encodeToByteArray()) }.readText() shouldBe "abc"
        shouldThrow<NoSuchFileException> { tempDir.resolveRandom().useBufferedOutputStream(TRUNCATE_EXISTING) {} }
    }

    @Test fun use_writer(@TempDir tempDir: Path) = test {
        tempDir.resolve("file").useWriter { it.write("abc") }.readText() shouldBe "abc"
        shouldThrow<NoSuchFileException> { tempDir.resolveRandom().useWriter(TRUNCATE_EXISTING) {} }
    }

    @Test fun use_buffered_writer(@TempDir tempDir: Path) = test {
        tempDir.resolve("file").useBufferedWriter { it.write("abc") }.readText() shouldBe "abc"
        shouldThrow<NoSuchFileException> { tempDir.resolveRandom().useBufferedWriter(TRUNCATE_EXISTING) {} }
    }
}

class DeleteOnExecTestHelper {
    enum class Variant {
        Default {
            override fun deleteOnExit(path: Path) {
                path.deleteOnExit()
            }
        },
        Recursively {
            override fun deleteOnExit(path: Path) {
                path.deleteOnExit(recursively = true)
            }
        },
        NonRecursively {
            override fun deleteOnExit(path: Path) {
                path.deleteOnExit(recursively = false)
            }
        };

        abstract fun deleteOnExit(path: Path)
    }

    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            kotlin.runCatching {
                val operation = Variant.valueOf(args.first())::deleteOnExit
                val file = Paths.get(args.last())
                require(file.isSubPathOf(SystemLocations.Temp))
                operation(file)
            }.onFailure { exitProcess(1) }
        }
    }
}

public fun Path.symbolicLink(): Path = resolveRandom().apply {
    Files.createSymbolicLink(this, resolveRandom())
    check(exists(NOFOLLOW_LINKS)) { "Failed to create symbolic link $this." }
}
