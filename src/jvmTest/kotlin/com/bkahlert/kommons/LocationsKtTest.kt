package com.bkahlert.kommons

import com.bkahlert.kommons.test.junit.testEach
import com.bkahlert.kommons.test.test
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.paths.shouldBeADirectory
import io.kotest.matchers.paths.shouldBeAbsolute
import io.kotest.matchers.paths.shouldExist
import io.kotest.matchers.paths.shouldNotExist
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.pathString
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class LocationsKtTest {

    @Nested
    inner class DefaultLocations {

        @TestFactory fun paths() = testEach(
            Locations.Default.Work,
            Locations.Default.Home,
            Locations.Default.Temp,
            Locations.Default.JavaHome,
        ) {
            it.shouldBeAbsolute()
            it.shouldExist()
            it.shouldBeADirectory()
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
        fun `should delete if empty`(@TempDir tempDir: Path) = test {
            tempDir.cleanUp(Duration.ZERO, 0).shouldNotExist()
        }

        @Test
        fun `should keep at most specified number of files`(@TempDir tempDir: Path) = test {
            (1..10).forEach { _ -> tempDir.tempFile() }
            tempDir.listDirectoryEntriesRecursively() shouldHaveSize 10
            tempDir.cleanUp(Duration.ZERO, 5).listDirectoryEntriesRecursively() shouldHaveSize 5
        }

        @Test
        fun `should not delete if less files than maximum`(@TempDir tempDir: Path) = test {
            (1..10).forEach { _ -> tempDir.tempFile() }
            tempDir.listDirectoryEntriesRecursively() shouldHaveSize 10
            tempDir.cleanUp(Duration.ZERO, 100).listDirectoryEntriesRecursively() shouldHaveSize 10
        }

        @Test
        fun `should not delete files younger than specified age`(@TempDir tempDir: Path) = test {
            val a = tempDir.tempFile("a").apply { age = 30.minutes }
            val b = tempDir.tempFile("b").apply { age = 1.5.hours }
            tempDir.tempFile("c").apply { age = 1.days }
            tempDir.cleanUp(2.hours, 0).listDirectoryEntriesRecursively().map { it.pathString }.shouldContainExactlyInAnyOrder(
                a.pathString,
                b.pathString
            )
        }

        @Test
        fun `should delete empty directories`(@TempDir tempDir: Path) = test {
            val emptyDir = tempDir.tempDir("empty")
            val file = tempDir.tempFile()
            tempDir.cleanUp(2.hours, 0).listDirectoryEntriesRecursively().map { it.pathString } should {
                it shouldNotContain emptyDir.pathString
                it shouldContain file.pathString
            }
        }
    }
}
