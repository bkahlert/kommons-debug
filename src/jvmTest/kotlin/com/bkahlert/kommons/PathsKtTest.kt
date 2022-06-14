package com.bkahlert.kommons

import com.bkahlert.kommons.DeleteOnExecTestHelper.Variant
import com.bkahlert.kommons.debug.trace
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
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.IOException
import java.net.URI
import java.net.URL
import java.nio.file.DirectoryNotEmptyException
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.LinkOption.NOFOLLOW_LINKS
import java.nio.file.NoSuchFileException
import java.nio.file.NotDirectoryException
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.ProviderNotFoundException
import java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
import java.nio.file.attribute.FileTime
import java.util.jar.JarOutputStream
import kotlin.io.path.createDirectories
import kotlin.io.path.createDirectory
import kotlin.io.path.createFile
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.outputStream
import kotlin.io.path.pathString
import kotlin.io.path.readBytes
import kotlin.io.path.readText
import kotlin.io.path.writeBytes
import kotlin.system.exitProcess
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class PathsKtTest {

    @Test fun is_directory_normalized(@TempDir tempDir: Path) = tests {
        tempDir.isDirectoryNormalized() shouldBe true
        (tempDir / "foo" / "..").isDirectoryNormalized() shouldBe true
        (tempDir / "foo").isDirectoryNormalized() shouldBe false
    }

    @Test fun require_directory_normalized(@TempDir tempDir: Path) = tests {
        tempDir should { it.requireDirectoryNormalized() shouldBe it }
        tempDir / "foo" / ".." should { it.requireDirectoryNormalized() shouldBe it }
        shouldThrow<IllegalArgumentException> { (tempDir / "foo").requireDirectoryNormalized() }
    }

    @Test fun check_directory_normalized(@TempDir tempDir: Path) = tests {
        tempDir should { it.checkDirectoryNormalized() shouldBe it }
        tempDir / "foo" / ".." should { it.checkDirectoryNormalized() shouldBe it }
        shouldThrow<IllegalStateException> { (tempDir / "foo").checkDirectoryNormalized() }
    }

    @Test fun require_no_directory_normalized(@TempDir tempDir: Path) = tests {
        shouldThrow<IllegalArgumentException> { tempDir.requireNoDirectoryNormalized() }
        shouldThrow<IllegalArgumentException> { (tempDir / "foo" / "..").requireNoDirectoryNormalized() }
        tempDir / "foo" should { it.requireNoDirectoryNormalized() shouldBe it }
    }

    @Test fun check_no_directory_normalized(@TempDir tempDir: Path) = tests {
        shouldThrow<IllegalStateException> { tempDir.checkNoDirectoryNormalized() }
        shouldThrow<IllegalStateException> { (tempDir / "foo" / "..").checkNoDirectoryNormalized() }
        tempDir / "foo" should { it.checkNoDirectoryNormalized() shouldBe it }
    }

    @Nested
    inner class IsSubPathOf {

        @Test
        fun `should return true if child`(@TempDir tempDir: Path) = tests {
            (tempDir / "child").isInside(tempDir) shouldBe true
            (tempDir / "child").isSubPathOf(tempDir) shouldBe true
        }

        @Test
        fun `should return true if descendent`(@TempDir tempDir: Path) = tests {
            (tempDir / "child1" / "child2").isInside(tempDir) shouldBe true
            (tempDir / "child1" / "child2").isSubPathOf(tempDir) shouldBe true
        }

        @Test
        fun `should return true if path is obscure`(@TempDir tempDir: Path) = tests {
            (tempDir / "child1" / ".." / "child2").isInside(tempDir) shouldBe true
            (tempDir / "child1" / ".." / "child2").isSubPathOf(tempDir) shouldBe true
        }

        @Test
        fun `should return true if same`(@TempDir tempDir: Path) = tests {
            tempDir.isInside(tempDir) shouldBe true
            tempDir.isSubPathOf(tempDir) shouldBe true
        }

        @Test
        fun `should return false if not inside`(@TempDir tempDir: Path) = tests {
            tempDir.isInside(tempDir / "child") shouldBe false
            tempDir.isSubPathOf(tempDir / "child") shouldBe false
        }
    }

    @Nested
    inner class CreateParentDirectories {

        @Test
        fun `should create missing directories`(@TempDir tempDir: Path) = tests {
            val file = tempDir.resolve("some/dir/some/file")
            file.createParentDirectories().parent should {
                it.shouldExist()
                it.shouldBeADirectory()
                it.shouldBeEmptyDirectory()
            }
        }
    }


    @Test
    fun age(@TempDir tempDir: Path) = tests {
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
        fun `should read created`(@TempDir tempDir: Path) = tests {
            tempDir.randomFile().created.toInstant() should {
                it shouldBeLessThan (Now + 1.minutes)
                it shouldBeGreaterThan (Now - 1.minutes)
            }
        }

        @Test
        fun `should write created`(@TempDir tempDir: Path) = tests {
            tempDir.randomFile().apply {
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
        fun `should read last accessed`(@TempDir tempDir: Path) = tests {
            tempDir.randomFile().lastAccessed.toInstant() should {
                it shouldBeLessThan (Now + 1.minutes)
                it shouldBeGreaterThan (Now - 1.minutes)
            }
        }

        @Test
        fun `should write last accessed`(@TempDir tempDir: Path) = tests {
            tempDir.randomFile().apply {
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
        fun `should read last modified`(@TempDir tempDir: Path) = tests {
            tempDir.randomFile().lastModified.toInstant() should {
                it shouldBeLessThan (Now + 1.minutes)
                it shouldBeGreaterThan (Now - 1.minutes)
            }
        }

        @Test
        fun `should write last modified`(@TempDir tempDir: Path) = tests {
            tempDir.randomFile().apply {
                lastModified = FileTime.from(Now - 20.minutes)
            }.lastModified.toInstant() should {
                it shouldBeLessThan (Now + 21.minutes)
                it shouldBeGreaterThan (Now - 21.minutes)
            }
        }
    }

    @Nested
    inner class ComputeHash {

        private fun byteArrayOf(vararg bytes: Int) =
            bytes.map { it.toByte() }.toByteArray()

        private val bytes = byteArrayOf(Byte.MIN_VALUE, -1, 0, 1, Byte.MAX_VALUE)

        @Test
        fun `should compute hash`(@TempDir tempDir: Path) = tests {
            val file = (tempDir / "md5").apply { writeBytes(bytes) }
            file.computeHash(MessageDigestProvider.MD5) shouldBe byteArrayOf(
                0x0A, 0x12, 0xF1, 0xE7, 0xD3, 0x46, 0xDE, 0x6D,
                0x51, 0xE7, 0x78, 0x8F, 0xE8, 0x7E, 0xCC, 0xD3,
            )
            file.computeHash(MessageDigestProvider.`SHA-1`) shouldBe byteArrayOf(
                0x44, 0x5D, 0x11, 0xBD, 0xF8, 0x71, 0x3F, 0x3D, 0x7F, 0xA1,
                0x33, 0x5B, 0x14, 0x1A, 0x77, 0x98, 0x27, 0x2C, 0xF7, 0x81,
            )
            file.computeHash(MessageDigestProvider.`SHA-256`) shouldBe byteArrayOf(
                0xFE, 0xDA, 0xBE, 0x10, 0xE6, 0x1B, 0x00, 0xD9, 0x13, 0x00, 0x50, 0x16, 0x9D, 0x67, 0x96, 0xDD,
                0x86, 0xFC, 0x72, 0xAE, 0xB4, 0xE8, 0x95, 0xCC, 0x0F, 0x8E, 0xF1, 0x90, 0x1B, 0xED, 0x58, 0x27,
            )
        }

        @Test
        fun `should throw on missing file`(@TempDir tempDir: Path) {
            shouldThrow<NoSuchFileException> { tempDir.resolve("i-dont-exist").computeHash() }
        }

        @Test
        fun `should throw on directory`(@TempDir tempDir: Path) {
            shouldThrow<IOException> { tempDir.computeHash() }
        }
    }

    @Nested
    inner class ComputeChecksum {

        private val bytes = byteArrayOf(Byte.MIN_VALUE, -1, 0, 1, Byte.MAX_VALUE)

        @Suppress("SpellCheckingInspection")
        @Test
        fun `should compute checksum`(@TempDir tempDir: Path) = tests {
            val file = (tempDir / "md5").apply { writeBytes(bytes) }
            file.computeChecksum(MessageDigestProvider.MD5) shouldBe "0a12f1e7d346de6d51e7788fe87eccd3"
            file.computeChecksum(MessageDigestProvider.`SHA-1`) shouldBe "445d11bdf8713f3d7fa1335b141a7798272cf781"
            file.computeChecksum(MessageDigestProvider.`SHA-256`) shouldBe "fedabe10e61b00d9130050169d6796dd86fc72aeb4e895cc0f8ef1901bed5827"

            file.computeMd5Checksum() shouldBe "0a12f1e7d346de6d51e7788fe87eccd3"
            file.computeSha1Checksum() shouldBe "445d11bdf8713f3d7fa1335b141a7798272cf781"
            file.computeSha256Checksum() shouldBe "fedabe10e61b00d9130050169d6796dd86fc72aeb4e895cc0f8ef1901bed5827"
        }

        @Test
        fun `should throw on missing file`(@TempDir tempDir: Path) {
            shouldThrow<NoSuchFileException> { tempDir.resolve("i-dont-exist").computeChecksum() }
        }

        @Test
        fun `should throw on directory`(@TempDir tempDir: Path) {
            shouldThrow<IOException> { tempDir.computeChecksum() }
        }
    }


    @Test fun resolve_between_file_systems(@TempDir tempDir: Path) {
        // same filesystem
        tempDir.tempJarFileSystem().use { jarFileSystem ->
            val receiverJarPath: Path = jarFileSystem.rootDirectories.first().randomDirectory().randomDirectory()
            val relativeJarPath: Path = receiverJarPath.parent.relativize(receiverJarPath)
            receiverJarPath.resolveBetweenFileSystems(relativeJarPath)
                .shouldBe(receiverJarPath.resolve(receiverJarPath.last()))
        }
        // same filesystem
        with(tempDir.randomDirectory()) {
            val receiverFilePath = randomDirectory()
            val relativeFilePath: Path = receiverFilePath.parent.relativize(receiverFilePath)
            receiverFilePath.resolveBetweenFileSystems(relativeFilePath)
                .shouldBe(receiverFilePath.resolve(receiverFilePath.last()))
        }

        // absolute other path
        tempDir.tempJarFileSystem().use { jarFileSystem ->
            val receiverJarPath: Path = jarFileSystem.rootDirectories.first().randomDirectory().randomFile()
            val absoluteJarPath: Path = jarFileSystem.rootDirectories.first()
            receiverJarPath.resolveBetweenFileSystems(absoluteJarPath)
                .shouldBe(absoluteJarPath)
        }
// absolute other path
        tempDir.tempJarFileSystem().use { jarFileSystem ->
            val receiverFilePath_: Path = tempDir.randomDirectory().randomFile()
            val absoluteJarPath: Path = jarFileSystem.rootDirectories.first()
            receiverFilePath_.resolveBetweenFileSystems(absoluteJarPath)
                .shouldBe(absoluteJarPath)
        }
// absolute other path
        tempDir.tempJarFileSystem().use { jarFileSystem ->
            val receiverJarPath: Path = jarFileSystem.rootDirectories.first().randomDirectory().randomFile()
            val otherFileAbsPath: Path = tempDir.randomDirectory()
            receiverJarPath.resolveBetweenFileSystems(otherFileAbsPath)
                .shouldBe(otherFileAbsPath)
        }
// absolute other path
        with(tempDir) {
            val receiverFilePath = randomDirectory().randomFile()
            val otherFileAbsPath: Path = randomDirectory()
            receiverFilePath.resolveBetweenFileSystems(otherFileAbsPath)
                .shouldBe(otherFileAbsPath)
        }

// relative other path
        with(tempDir) {
            val receiverFilePath: Path = randomDirectory().randomFile()
            tempJarFileSystem().use { jarFileSystem ->
                val relativeJarPath: Path = jarFileSystem.rootDirectories.first().randomDirectory().randomFile()
                    .let { absPath -> absPath.parent.relativize(absPath) }
                receiverFilePath.resolveBetweenFileSystems(relativeJarPath)
                    .shouldBe(receiverFilePath.resolve(relativeJarPath.first().toString()))
            }
        }
// relative other path
        with(tempDir) {
            val relativeFilePath: Path = randomDirectory().randomFile()
                .let { absPath -> absPath.parent.relativize(absPath) }
            tempJarFileSystem().use { jarFileSystem ->
                val receiverJarPath: Path = jarFileSystem.rootDirectories.first().randomDirectory().randomFile()
                receiverJarPath.resolveBetweenFileSystems(relativeFilePath)
                    .shouldBe(receiverJarPath.resolve(relativeFilePath.first().toString()))
            }
        }
    }

    @Test
    fun resolve_file(@TempDir tempDir: Path) = tests {
        tempDir.resolveFile { Path.of("dir", "file") } shouldBe tempDir / "dir" / "file"
        tempDir.resolveFile(Path.of("dir", "file")) shouldBe tempDir / "dir" / "file"
        tempDir.resolveFile("dir/file") shouldBe tempDir / "dir" / "file"
        shouldThrow<IllegalStateException> { tempDir.resolveFile { Path.of("dir", "..") } }
        shouldThrow<IllegalStateException> { tempDir.resolveFile(Path.of("dir", "..")) }
        shouldThrow<IllegalStateException> { tempDir.resolveFile("dir/..") }
    }

    @Test
    fun list_directory_entries_recursively(@TempDir tempDir: Path) = tests {
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

        shouldThrow<NotDirectoryException> { tempDir.randomFile().listDirectoryEntriesRecursively() }
    }

    @Nested
    inner class ListDirectoryEntriesRecursivelyOperation {

        @Test
        fun `should delete directory contents`(@TempDir tempDir: Path) = tests {
            val dir = tempDir.resolve("dir")
            dir.directoryWithTwoFiles()

            dir.deleteDirectoryEntriesRecursively().shouldExist()
            dir.shouldBeEmptyDirectory()
        }

        @Test
        fun `should delete filtered directory contents`(@TempDir tempDir: Path) = tests {
            val dir = tempDir.resolve("dir")
            val exception = dir.directoryWithTwoFiles().listDirectoryEntriesRecursively().first()

            dir.deleteDirectoryEntriesRecursively { it != exception && !it.isDirectory() }.shouldExist()
            dir.listDirectoryEntries().map { it.pathString }.shouldNotContain(exception.pathString)
        }

        @Test
        fun `should throw on non-directory`(@TempDir tempDir: Path) = tests {
            val file = tempDir.resolve("file").createFile()
            shouldThrow<NotDirectoryException> { file.deleteDirectoryEntriesRecursively() }
        }
    }

    @Test
    fun use_directory_entries_recursively(@TempDir tempDir: Path) = tests {
        val dir = tempDir.directoryWithTwoFiles()

        dir.useDirectoryEntriesRecursively { seq -> seq.map { it.fileName.pathString }.sorted().joinToString() }
            .shouldBe("config.txt, example.html, sub-dir")

        dir.useDirectoryEntriesRecursively("**/*.*") { seq -> seq.map { it.fileName.pathString }.sorted().joinToString() }
            .shouldBe("config.txt, example.html")

        shouldThrow<NotDirectoryException> { tempDir.randomFile().useDirectoryEntriesRecursively { } }
    }

    @Test
    fun for_each_directory_entries_recursively(@TempDir tempDir: Path) = tests {
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

        shouldThrow<NotDirectoryException> { tempDir.randomFile().forEachDirectoryEntryRecursively { } }
    }


    @Nested
    inner class Delete {

        @Test
        fun `should delete file`(@TempDir tempDir: Path) = tests {
            val file = tempDir.singleFile()
            file.delete().shouldNotExist()
            tempDir.shouldBeEmptyDirectory()
        }

        @Test
        fun `should delete empty directory`(@TempDir tempDir: Path) = tests {
            val dir = tempDir.resolve("dir").createDirectory()
            dir.delete().shouldNotExist()
            tempDir.shouldBeEmptyDirectory()
        }

        @Test
        fun `should throw on non-empty directory`(@TempDir tempDir: Path) = tests {
            val dir = tempDir.resolve("dir").createDirectory().apply { singleFile() }
            shouldThrow<DirectoryNotEmptyException> { dir.delete() }
        }

        @Test
        fun `should delete non-existing file`(@TempDir tempDir: Path) = tests {
            val file = tempDir.resolve("file")
            file.delete().asClue { it.exists() shouldBe false }
            tempDir.shouldBeEmptyDirectory()
        }

        @Nested
        inner class WithNoFollowLinks {

            @Test
            fun `should delete symbolic link itself`(@TempDir tempDir: Path) = tests {
                val symbolicLink = tempDir.symbolicLink()
                symbolicLink.delete()

                symbolicLink.delete(NOFOLLOW_LINKS).asClue { it.exists(NOFOLLOW_LINKS) shouldBe false }
                tempDir.shouldBeEmptyDirectory()
            }
        }

        @Nested
        inner class WithoutNoFollowLinks {

            @Test
            fun `should not delete symbolic link itself`(@TempDir tempDir: Path) = tests {
                val symbolicLink = tempDir.symbolicLink()

                symbolicLink.shouldBeSymbolicLink()
                tempDir.shouldNotBeEmptyDirectory()
            }
        }
    }

    @Nested
    inner class DeleteRecursively {

        @Test
        fun `should delete file`(@TempDir tempDir: Path) = tests {
            tempDir.singleFile().deleteRecursively().shouldNotExist()
            tempDir.shouldBeEmptyDirectory()
        }

        @Test
        fun `should delete empty directory`(@TempDir tempDir: Path) = tests {
            tempDir.resolve("dir").createDirectory().deleteRecursively().shouldNotExist()
            tempDir.shouldBeEmptyDirectory()
        }

        @Test
        fun `should delete non-empty directory`(@TempDir tempDir: Path) = tests {
            tempDir.resolve("dir").createDirectory().apply { singleFile() }.deleteRecursively().shouldNotExist()
            tempDir.shouldBeEmptyDirectory()
        }

        @Test
        fun `should delete non-existing file`(@TempDir tempDir: Path) = tests {
            tempDir.resolve("file").deleteRecursively().shouldNotExist()
            tempDir.shouldBeEmptyDirectory()
        }

        @Test
        fun `should delete complex file tree`(@TempDir tempDir: Path) = tests {
            val dir = tempDir.resolve("dir")
            dir.directoryWithTwoFiles().symbolicLink()

            dir.deleteRecursively().shouldNotExist()
            tempDir.shouldBeEmptyDirectory()
        }

        @Test
        fun `should delete filtered files`(@TempDir tempDir: Path) = tests {
            val dir = tempDir.resolve("dir")
            val exception = dir.directoryWithTwoFiles().listDirectoryEntriesRecursively().first()

            dir.deleteRecursively { it != exception && !it.isDirectory() }.shouldExist()
            dir.listDirectoryEntries().map { it.pathString }.shouldNotContain(exception.pathString)
        }
    }

    @Test fun delete_on_exit(@TempDir tempDir: Path) = tests {
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

    @Test fun use_path(@TempDir tempDir: Path) = tests {
        val regularFile = tempDir.singleFile(content = "foo")
        regularFile.toUri().usePath { it.readText() } shouldBe "foo"
        regularFile.toUri().toURL().usePath { it.readText() } shouldBe "foo"

        val standardLibraryClassPath = checkNotNull(Regex::class.java.getResource("Regex.class"))
        standardLibraryClassPath.usePath { it.readText() }.trace
        standardLibraryClassPath.toURI().usePath { it.readText() }.shouldContain("Matcher").shouldContain(Unicode.START_OF_HEADING.toString())
        standardLibraryClassPath.usePath { it.readText() }.shouldContain("Matcher").shouldContain(Unicode.START_OF_HEADING.toString())

        val bytes = ubyteArrayOf(
            0x61u, 0xC2u, 0x85u, 0xF0u, 0x9Du, 0x95u, 0x93u, 0x0Du, 0x0Au,
            0xE2u, 0x98u, 0xB0u, 0x0Au, 0xF0u, 0x9Fu, 0x91u, 0x8Bu, 0x0Au
        ).toByteArray()
        val ownClassPath = checkNotNull(Platform.contextClassLoader.getResource("61C285F09D95930D0AE298B00AF09F918B0A.txt"))
        ownClassPath.toURI().usePath { it.readBytes() }.contentEquals(bytes)
        ownClassPath.usePath { it.readBytes() }.contentEquals(bytes)

        shouldThrow<ProviderNotFoundException> { URI("https://example.com").usePath { } }
        shouldThrow<ProviderNotFoundException> { URL("https://example.com").usePath { } }
    }

    @Test fun use_input_stream(@TempDir tempDir: Path) = tests {
        tempDir.singleFile(content = "abc").useInputStream { it.readBytes().decodeToString() } shouldBe "abc"
        shouldThrow<NoSuchFileException> { tempDir.randomPath().useInputStream {} }
    }

    @Test fun use_buffered_input_stream(@TempDir tempDir: Path) = tests {
        tempDir.singleFile(content = "abc").useBufferedInputStream { it.readBytes().decodeToString() } shouldBe "abc"
        shouldThrow<NoSuchFileException> { tempDir.randomPath().useBufferedInputStream {} }
    }

    @Test fun use_reader(@TempDir tempDir: Path) = tests {
        tempDir.singleFile(content = "abc").useReader { it.readText() } shouldBe "abc"
        shouldThrow<NoSuchFileException> { tempDir.randomPath().useReader {} }
    }

    @Test fun use_buffered_reader(@TempDir tempDir: Path) = tests {
        tempDir.singleFile(content = "abc").useBufferedReader { it.readText() } shouldBe "abc"
        shouldThrow<NoSuchFileException> { tempDir.randomPath().useBufferedReader {} }
    }

    @Test fun use_output_stream(@TempDir tempDir: Path) = tests {
        tempDir.resolve("file").useOutputStream { it.write("abc".encodeToByteArray()) }.readText() shouldBe "abc"
        shouldThrow<NoSuchFileException> { tempDir.randomPath().useOutputStream(TRUNCATE_EXISTING) {} }
    }

    @Test fun use_buffered_output_stream(@TempDir tempDir: Path) = tests {
        tempDir.resolve("file").useBufferedOutputStream { it.write("abc".encodeToByteArray()) }.readText() shouldBe "abc"
        shouldThrow<NoSuchFileException> { tempDir.randomPath().useBufferedOutputStream(TRUNCATE_EXISTING) {} }
    }

    @Test fun use_writer(@TempDir tempDir: Path) = tests {
        tempDir.resolve("file").useWriter { it.write("abc") }.readText() shouldBe "abc"
        shouldThrow<NoSuchFileException> { tempDir.randomPath().useWriter(TRUNCATE_EXISTING) {} }
    }

    @Test fun use_buffered_writer(@TempDir tempDir: Path) = tests {
        tempDir.resolve("file").useBufferedWriter { it.write("abc") }.readText() shouldBe "abc"
        shouldThrow<NoSuchFileException> { tempDir.randomPath().useBufferedWriter(TRUNCATE_EXISTING) {} }
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
                require(file.isSubPathOf(Locations.Default.Temp))
                operation(file)
            }.onFailure { exitProcess(1) }
        }
    }
}


internal fun Path.symbolicLink(): Path = randomPath().apply {
    Files.createSymbolicLink(this, randomPath())
    check(exists(NOFOLLOW_LINKS)) { "Failed to create symbolic link $this." }
}

internal fun Path.singleFile(
    name: String = "example.html",
    content: String = "content $name",
): Path = resolve(name).apply {
    writeBytes(content.encodeToByteArray())
    check(exists()) { "Failed to provide archive with single file." }
}

internal fun Path.directoryWithTwoFiles(
    base: String = randomString(4),
): Path = randomDirectory(base).apply {
    singleFile()
    resolve("sub-dir").createDirectories().singleFile("config.txt")
    check(listDirectoryEntries().size == 2) { "Failed to provide directory with two files." }
}

/**
 * Creates an empty `jar` file in the system's temp directory.
 *
 * @see asNewJarFileSystem
 */
public fun Path.tempJar(base: String = "", extension: String = ".jar"): Path =
    tempFile(base, extension).apply {
        JarOutputStream(outputStream().buffered()).use { }
    }

/**
 * Attempts to create a [FileSystem] from an existing `jar` file.
 *
 * @see tempJar
 */
public fun Path.asNewJarFileSystem(vararg env: Pair<String, Any?>): FileSystem =
    FileSystems.newFileSystem(URI.create("jar:${toUri()}"), env.toMap())

/**
 * Creates an empty `jar` file system the system's temp directory.
 *
 * @see tempJar
 * @see asNewJarFileSystem
 */
public fun Path.tempJarFileSystem(base: String = "", extension: String = ".jar"): FileSystem =
    tempJar(base, extension).asNewJarFileSystem()
