package com.bkahlert.kommons

import io.kotest.assertions.asClue
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
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
import java.nio.file.DirectoryNotEmptyException
import java.nio.file.Files
import java.nio.file.LinkOption.NOFOLLOW_LINKS
import java.nio.file.NoSuchFileException
import java.nio.file.NotDirectoryException
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.Year
import java.time.YearMonth
import java.time.temporal.Temporal
import kotlin.io.path.createDirectories
import kotlin.io.path.createDirectory
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.pathString
import kotlin.io.path.writeBytes
import kotlin.io.path.writeText
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class PathsKtTest {

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

    @Suppress("SpellCheckingInspection")
    @Test
    fun `should compute checksums`(@TempDir tempDir: Path) = tests {
        val file = tempDir.resolve("file").apply { writeText("content") }
        file.computeHash(MessageDigests.MD5()) shouldBe byteArrayOf(
            -102, 3, 100, -71, -23, -101, -76, -128, -35, 37, -31, -16, 40, 76, -123, 85
        )
        file.computeChecksum(MessageDigests.MD5()) shouldBe "9a0364b9e99bb480dd25e1f0284c8555"
        file.computeMd5Checksum() shouldBe "9a0364b9e99bb480dd25e1f0284c8555"

        file.computeHash(MessageDigests.`SHA-1`()) shouldBe byteArrayOf(
            4, 15, 6, -3, 119, 64, -110, 71, -115, 69, 7, 116, -11, -70, 48, -59, -38, 120, -84, -56
        )
        file.computeChecksum(MessageDigests.`SHA-1`()) shouldBe "040f06fd774092478d450774f5ba30c5da78acc8"
        file.computeSha1Checksum() shouldBe "040f06fd774092478d450774f5ba30c5da78acc8"

        file.computeHash(MessageDigests.`SHA-256`()) shouldBe byteArrayOf(
            -19, 112, 2, -76, 57, -23, -84, -124, 95, 34, 53, 125, -126, 43, -84, 20, 68, 115, 15, -67, -74, 1, 109, 62, -55, 67, 34, -105, -71, -20, -97, 115
        )
        file.computeChecksum(MessageDigests.`SHA-256`()) shouldBe "ed7002b439e9ac845f22357d822bac1444730fbdb6016d3ec9432297b9ec9f73"
        file.computeSha256Checksum() shouldBe "ed7002b439e9ac845f22357d822bac1444730fbdb6016d3ec9432297b9ec9f73"
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

/**
 * Returns this file time with the specified [duration] subtracted.
 */
private operator fun FileTime.minus(duration: Duration): FileTime =
    FileTime.from(toInstant().minus(duration))

/**
 * Returns a [FileTime] representing the same point of time value
 * on the time-line as this instant.
 */
private fun Instant.toFileTime(): FileTime = FileTime.from(this)


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
