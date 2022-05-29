package com.bkahlert.kommons

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
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.IOException
import java.nio.file.DirectoryNotEmptyException
import java.nio.file.Files
import java.nio.file.LinkOption.NOFOLLOW_LINKS
import java.nio.file.NoSuchFileException
import java.nio.file.NotDirectoryException
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import java.time.LocalDate
import java.time.Period
import java.time.Year
import java.time.YearMonth
import java.time.temporal.Temporal
import kotlin.io.path.createDirectories
import kotlin.io.path.createDirectory
import kotlin.io.path.createFile
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.pathString
import kotlin.io.path.writeBytes
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class PathsKtTest {

    @Nested
    inner class IsSubPathOf {

        @Test
        fun `should return true if child`(@TempDir tempDir: Path) = tests {
            (tempDir / "child").isSubPathOf(tempDir) shouldBe true
        }

        @Test
        fun `should return true if descendent`(@TempDir tempDir: Path) = tests {
            (tempDir / "child1" / "child2").isSubPathOf(tempDir) shouldBe true
        }

        @Test
        fun `should return true if path is obscure`(@TempDir tempDir: Path) = tests {
            (tempDir / "child1" / ".." / "child2").isSubPathOf(tempDir) shouldBe true
        }

        @Test
        fun `should return true if same`(@TempDir tempDir: Path) = tests {
            tempDir.isSubPathOf(tempDir) shouldBe true
        }

        @Test
        fun `should return false if not inside`(@TempDir tempDir: Path) = tests {
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

    @Nested
    inner class Age {

        @Test
        fun `should return age of file`(@TempDir tempDir: Path) = tests {
            tempDir.resolve("file").apply {
                createFile()
                lastModified -= 1.days
            }.age should {
                it shouldBeGreaterThanOrEqualTo (1.days - 5.seconds)
                it shouldBeLessThanOrEqualTo (1.days + 5.seconds)
            }
        }

        @Test
        fun `should return age of directory`(@TempDir tempDir: Path) = tests {
            tempDir.resolve("dir").apply {
                createDirectory()
                lastModified -= 1.days
            }.age should {
                it shouldBeGreaterThanOrEqualTo (1.days - 5.seconds)
                it shouldBeLessThanOrEqualTo (1.days + 5.seconds)
            }
        }

        @Test
        fun `should throw if missing`(@TempDir tempDir: Path) = tests {
            shouldThrow<NoSuchFileException> { tempDir.resolve("file").age }
        }
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
            file.computeHash(MessageDigests.MD5()) shouldBe byteArrayOf(
                0x0A, 0x12, 0xF1, 0xE7, 0xD3, 0x46, 0xDE, 0x6D,
                0x51, 0xE7, 0x78, 0x8F, 0xE8, 0x7E, 0xCC, 0xD3,
            )
            file.computeHash(MessageDigests.`SHA-1`()) shouldBe byteArrayOf(
                0x44, 0x5D, 0x11, 0xBD, 0xF8, 0x71, 0x3F, 0x3D, 0x7F, 0xA1,
                0x33, 0x5B, 0x14, 0x1A, 0x77, 0x98, 0x27, 0x2C, 0xF7, 0x81,
            )
            file.computeHash(MessageDigests.`SHA-256`()) shouldBe byteArrayOf(
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
            file.computeChecksum(MessageDigests.MD5()) shouldBe "0a12f1e7d346de6d51e7788fe87eccd3"
            file.computeChecksum(MessageDigests.`SHA-1`()) shouldBe "445d11bdf8713f3d7fa1335b141a7798272cf781"
            file.computeChecksum(MessageDigests.`SHA-256`()) shouldBe "fedabe10e61b00d9130050169d6796dd86fc72aeb4e895cc0f8ef1901bed5827"

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

    @Nested
    inner class ListDirectoryEntriesRecursively {

        @Test
        fun `should list all entries recursively`(@TempDir tempDir: Path) = tests {
            val dir = tempDir.directoryWithTwoFiles()
            val subject = dir.listDirectoryEntriesRecursively().map { it.pathString }
            subject shouldContainExactlyInAnyOrder listOf(
                dir.resolve("example.html").pathString,
                dir.resolve("sub-dir").pathString,
                dir.resolve("sub-dir/config.txt").pathString,
            )
        }

        @Test
        fun `should apply glob expression`(@TempDir tempDir: Path) = tests {
            val dir = tempDir.directoryWithTwoFiles()
            val subject = dir.listDirectoryEntriesRecursively("**/*.*").map { it.pathString }
            subject shouldContainExactlyInAnyOrder listOf(
                dir.resolve("example.html").pathString,
                dir.resolve("sub-dir/config.txt").pathString,
            )
        }

        @Test
        fun `should throw on listing file`(@TempDir tempDir: Path) = tests {
            shouldThrow<NotDirectoryException> { tempDir.randomFile().listDirectoryEntriesRecursively() }
        }
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

            dir.deleteDirectoryEntriesRecursively() { it != exception && !it.isDirectory() }.shouldExist()
            dir.listDirectoryEntries().map { it.pathString }.shouldNotContain(exception.pathString)
        }

        @Test
        fun `should throw on non-directory`(@TempDir tempDir: Path) = tests {
            val file = tempDir.resolve("file").createFile()
            shouldThrow<NotDirectoryException> { file.deleteDirectoryEntriesRecursively() }
        }
    }
}


/**
 * Returns an object of the same type [T] as this object with the specified [duration] subtracted.
 *
 * **Important:** This operation does not work for date types like [LocalDate], [YearMonth] or [Year]
 * as they don't differ in [Duration] but in [Period].
 */
private inline operator fun <reified T : Temporal> T.minus(duration: Duration): T {
    return duration.toComponents { days, hours, minutes, seconds, nanoseconds ->
        sequenceOf(
            java.time.Duration.ofDays(days),
            java.time.Duration.ofHours(hours.toLong()),
            java.time.Duration.ofMinutes(minutes.toLong()),
            java.time.Duration.ofSeconds(seconds.toLong()),
            java.time.Duration.ofNanos(nanoseconds.toLong()),
        ).filter { !it.isZero }.fold(this) { temporal, adjuster ->
            if (adjuster.isZero) temporal
            else temporal.minus(adjuster) as? T ?: error("broken contract of Temporal operations returning the same type")
        }
    }
}


internal fun Path.symbolicLink(): Path = randomPath()
    .also { link -> Files.createSymbolicLink(link, randomPath()) }
    .apply { check(exists(NOFOLLOW_LINKS)) { "Failed to create symbolic link $this." } }

internal fun Path.singleFile(name: String = "example.html", content: String = "content $name"): Path = resolve(name)
    .apply { writeBytes(content.encodeToByteArray()) }
    .apply { check(exists()) { "Failed to provide archive with single file." } }

internal fun Path.directoryWithTwoFiles(): Path = randomDirectory().also {
    it.singleFile()
    it.resolve("sub-dir").createDirectories().singleFile("config.txt")
}.apply { check(listDirectoryEntries().size == 2) { "Failed to provide directory with two files." } }
