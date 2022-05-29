package com.bkahlert.kommons

import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.paths.shouldBeADirectory
import io.kotest.matchers.paths.shouldBeAFile
import io.kotest.matchers.paths.shouldBeAbsolute
import io.kotest.matchers.paths.shouldExist
import io.kotest.matchers.paths.shouldNotExist
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermission
import kotlin.io.path.getPosixFilePermissions
import kotlin.io.path.pathString
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class LocationsKtTest {

    @Nested
    inner class DefaultLocations {

        @Test
        fun paths() = tests {
            listOf(
                Locations.Default.Work,
                Locations.Default.Home,
                Locations.Default.Temp,
                Locations.Default.JavaHome,
            ).shouldForAll {
                it.shouldBeAbsolute()
                it.shouldExist()
                it.shouldBeADirectory()
            }
        }
    }

    @Nested
    inner class RandomPath {

        @Test
        fun `should create inside receiver path`(@TempDir tempDir: Path) = tests {
            tempDir.randomPath().isSubPathOf(tempDir) shouldBe true
        }

        @Test
        fun `should not exist`(@TempDir tempDir: Path) = tests {
            tempDir.randomPath().shouldNotExist()
        }
    }

    @Nested
    inner class RandomDirectory {

        @Test
        fun `should create inside receiver path`(@TempDir tempDir: Path) = tests {
            tempDir.randomDirectory().isSubPathOf(tempDir) shouldBe true
        }

        @Test
        fun `should create directory`(@TempDir tempDir: Path) = tests {
            tempDir.randomDirectory().shouldBeADirectory()
        }

        @Test
        fun `should create directory inside non-existent parent`(@TempDir tempDir: Path) = tests {
            tempDir.randomPath().randomDirectory().shouldBeADirectory()
        }
    }

    @Nested
    inner class RandomFile {

        @Test
        fun `should create inside receiver path`(@TempDir tempDir: Path) = tests {
            tempDir.randomFile().isSubPathOf(tempDir) shouldBe true
        }

        @Test
        fun `should create regular file`(@TempDir tempDir: Path) = tests {
            tempDir.randomFile().shouldBeAFile()
        }

        @Test
        fun `should create regular file inside non-existent parent`(@TempDir tempDir: Path) = tests {
            tempDir.randomPath().randomFile().shouldBeAFile()
        }
    }

    @Nested
    inner class TempDirectory {

        @Test
        fun `should create inside temp directory`() {
            tempDir().deleteOnExit().isSubPathOf(Locations.Default.Temp) shouldBe true
        }

        @Test
        fun `should create directory`() {
            tempDir().deleteOnExit().shouldBeADirectory()
        }

        @Test
        fun `should have exclusive rights`() {
            tempDir().deleteOnExit().getPosixFilePermissions() shouldContainExactly setOf(
                PosixFilePermission.OWNER_READ,
                PosixFilePermission.OWNER_WRITE,
                PosixFilePermission.OWNER_EXECUTE
            )
        }

        @Test
        fun `should create directory inside receiver path`(@TempDir tempDir: Path) = tests {
            tempDir.tempDir() should {
                it.isSubPathOf(tempDir) shouldBe true
                it.shouldBeADirectory()
                it.getPosixFilePermissions() shouldContainExactly setOf(
                    PosixFilePermission.OWNER_READ,
                    PosixFilePermission.OWNER_WRITE,
                    PosixFilePermission.OWNER_EXECUTE
                )
            }
        }

        @Test
        fun `should create directory inside non-existent parent`(@TempDir tempDir: Path) = tests {
            val nonExistentParent = tempDir.randomPath()
            nonExistentParent.tempDir() should {
                it.isSubPathOf(nonExistentParent) shouldBe true
                it.shouldBeADirectory()
                it.getPosixFilePermissions() shouldContainExactly setOf(
                    PosixFilePermission.OWNER_READ,
                    PosixFilePermission.OWNER_WRITE,
                    PosixFilePermission.OWNER_EXECUTE
                )
            }
        }
    }

    @Nested
    inner class TempFile {

        @Test
        fun `should create inside temp directory`() {
            tempFile().deleteOnExit().isSubPathOf(Locations.Default.Temp) shouldBe true
        }

        @Test
        fun `should create file`() {
            tempFile().deleteOnExit().shouldBeAFile()
        }

        @Test
        fun `should have exclusive rights`() {
            tempFile().deleteOnExit().getPosixFilePermissions() shouldContainExactly setOf(
                PosixFilePermission.OWNER_READ,
                PosixFilePermission.OWNER_WRITE,
                PosixFilePermission.OWNER_EXECUTE
            )
        }

        @Test
        fun `should create file inside receiver path`(@TempDir tempDir: Path) = tests {
            tempDir.tempFile() should {
                it.isSubPathOf(tempDir) shouldBe true
                it.shouldBeAFile()
                it.getPosixFilePermissions() shouldContainExactly setOf(
                    PosixFilePermission.OWNER_READ,
                    PosixFilePermission.OWNER_WRITE,
                    PosixFilePermission.OWNER_EXECUTE
                )
            }
        }

        @Test
        fun `should create file inside non-existent parent`(@TempDir tempDir: Path) = tests {
            val nonExistentParent = tempDir.randomPath()
            nonExistentParent.tempFile() should {
                it.isSubPathOf(nonExistentParent) shouldBe true
                it.shouldBeAFile()
                it.getPosixFilePermissions() shouldContainExactly setOf(
                    PosixFilePermission.OWNER_READ,
                    PosixFilePermission.OWNER_WRITE,
                    PosixFilePermission.OWNER_EXECUTE
                )
            }
        }
    }

    @Nested
    inner class RunWithTempDir {

        @Test
        fun `should run inside temp dir`() {
            val tempDir: Path = runWithTempDir {
                this should {
                    it.isSubPathOf(Locations.Default.Temp) shouldBe true
                    it.shouldBeADirectory()
                }
                this
            }
            tempDir.shouldNotExist()
        }
    }

    @Nested
    inner class AutoCleanup {

        @Test
        fun `should delete if empty`(@TempDir tempDir: Path) = tests {
            tempDir.cleanUp(Duration.ZERO, 0).shouldNotExist()
        }

        @Test
        fun `should keep at most specified number of files`(@TempDir tempDir: Path) = tests {
            (1..10).forEach { _ -> tempDir.tempFile() }
            tempDir.listDirectoryEntriesRecursively() shouldHaveSize 10
            tempDir.cleanUp(Duration.ZERO, 5).listDirectoryEntriesRecursively() shouldHaveSize 5
        }

        @Test
        fun `should not delete if less files than maximum`(@TempDir tempDir: Path) = tests {
            (1..10).forEach { _ -> tempDir.tempFile() }
            tempDir.listDirectoryEntriesRecursively() shouldHaveSize 10
            tempDir.cleanUp(Duration.ZERO, 100).listDirectoryEntriesRecursively() shouldHaveSize 10
        }

        @Test
        fun `should not delete files younger than specified age`(@TempDir tempDir: Path) = tests {
            val a = tempDir.tempFile("a").apply { age = 30.minutes }
            val b = tempDir.tempFile("b").apply { age = 1.5.hours }
            tempDir.tempFile("c").apply { age = 1.days }
            tempDir.cleanUp(2.hours, 0).listDirectoryEntriesRecursively().map { it.pathString }.shouldContainExactlyInAnyOrder(
                a.pathString,
                b.pathString
            )
        }

        @Test
        fun `should delete empty directories`(@TempDir tempDir: Path) = tests {
            val emptyDir = tempDir.tempDir("empty")
            val file = tempDir.tempFile()
            tempDir.cleanUp(2.hours, 0).listDirectoryEntriesRecursively().map { it.pathString } should {
                it shouldNotContain emptyDir.pathString
                it shouldContain file.pathString
            }
        }
    }
}
