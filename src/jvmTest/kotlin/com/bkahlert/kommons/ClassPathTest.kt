package com.bkahlert.kommons

import com.bkahlert.kommons.test.test
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.IOException
import java.net.URI
import java.net.URL
import java.nio.file.Path
import kotlin.io.path.copyTo
import kotlin.io.path.div
import kotlin.io.path.pathString
import kotlin.io.path.readBytes
import kotlin.io.path.readText

class ClassPathTest {

    @Test fun class_path() = test {
        ClassPath(classPathTextFile) should {
            it.fileSystem.shouldBeInstanceOf<DelegatingFileSystem>()
            it.isAbsolute shouldBe true
            it.root.pathString shouldBe "/"
            it.fileName.pathString shouldBe "61C285F09D95930D0AE298B00AF09F918B0A.txt"
            it.parent.pathString shouldEndWith "jvm/test"
            it.nameCount shouldBeGreaterThan 3
            it.getName(it.nameCount - 2).pathString shouldBe "test"
            it.subpath(it.nameCount - 3, it.nameCount - 1).pathString shouldBe "jvm/test"
            withClue("starts with self") { it.startsWith(it) shouldBe true }
            withClue("not starts without root") { it.startsWith(it.subpath(0, 2)) shouldBe false }
            withClue("starts with root") { it.startsWith(it.root / it.subpath(0, 2)) shouldBe true }
            withClue("starts with root string") { it.startsWith("/") shouldBe true }
            withClue("ends with self") { it.endsWith(it) shouldBe true }
            withClue("not ends without file name") { it.endsWith(it.subpath(it.nameCount - 3, it.nameCount - 2)) shouldBe false }
            withClue("ends with file name") { it.endsWith(it.subpath(it.nameCount - 3, it.nameCount)) shouldBe true }
            withClue("ends with file name string") { it.endsWith("61C285F09D95930D0AE298B00AF09F918B0A.txt") shouldBe true }
            it.normalize().pathString shouldBe it.pathString
            it.resolve("..").normalize().pathString shouldBe it.parent.pathString
            it.relativize(it.parent).pathString shouldBe ".."
            it.toUri().toString() shouldMatch "file:.*/jvm/test/61C285F09D95930D0AE298B00AF09F918B0A\\.txt".toRegex()
            it.toAbsolutePath().pathString shouldMatch "/.*/jvm/test/61C285F09D95930D0AE298B00AF09F918B0A\\.txt".toRegex()
            it.toRealPath().pathString shouldMatch "/.*/jvm/test/61C285F09D95930D0AE298B00AF09F918B0A\\.txt".toRegex()
            it.readText() shouldBe classPathTextFileText
            it.readBytes() shouldBe classPathTextFileBytes
        }
        ClassPath(standardLibraryClassPathClass) should {
            it.fileSystem.shouldBeInstanceOf<DelegatingFileSystem>()
            it.isAbsolute shouldBe true
            it.root.pathString shouldBe "/"
            it.fileName.pathString shouldBe "Regex.class"
            it.parent.pathString shouldBe "/kotlin/text"
            it.nameCount shouldBe 3
            it.getName(it.nameCount - 2).pathString shouldBe "text"
            it.subpath(it.nameCount - 3, it.nameCount - 1).pathString shouldBe "kotlin/text"
            withClue("starts with self") { it.startsWith(it) shouldBe true }
            withClue("not starts without root") { it.startsWith(it.subpath(0, 2)) shouldBe false }
            withClue("starts with root") { it.startsWith(it.root / it.subpath(0, 2)) shouldBe true }
            withClue("starts with root string") { it.startsWith("/") shouldBe true }
            withClue("ends with self") { it.endsWith(it) shouldBe true }
            withClue("not ends without file name") { it.endsWith(it.subpath(it.nameCount - 3, it.nameCount - 2)) shouldBe false }
            withClue("ends with file name") { it.endsWith(it.subpath(it.nameCount - 3, it.nameCount)) shouldBe true }
            withClue("ends with file name string") { it.endsWith("Regex.class") shouldBe true }
            it.normalize().pathString shouldBe it.pathString
            it.resolve("..").normalize().pathString shouldBe it.parent.pathString
            it.relativize(it.parent).pathString shouldBe ".."
            it.toUri().toString() shouldMatch "jar:file:.*/kotlin/text/Regex\\.class".toRegex()
            it.toAbsolutePath().pathString shouldMatch "/kotlin/text/Regex\\.class".toRegex()
            it.toRealPath().pathString shouldMatch "/kotlin/text/Regex\\.class".toRegex()
            it.readText() shouldBe standardLibraryClassPathClassText
            it.readBytes() shouldBe standardLibraryClassPathClassBytes
        }
        shouldThrow<IOException> { ClassPath("invalid.file") }
        shouldThrow<IOException> { ClassPath(URL("jar:file:/invalid.jar!/invalid.class")) }
        shouldThrow<IOException> { ClassPath(URI("jar:file:/invalid.jar!/invalid.class")) }
    }

    @Test fun manually_copy_class_path(@TempDir tempDir: Path) = test {
        ClassPath(classPathTextFile).useBufferedInputStream {
            tempDir.resolve("classPathTextFile-streamed-copy").useOutputStream { out -> it.copyTo(out) }
        }.readBytes() shouldBe classPathTextFileBytes

        ClassPath(standardLibraryClassPathClass).useBufferedInputStream {
            tempDir.resolve("standardLibraryClassPathClass-streamed-copy").useOutputStream { out -> it.copyTo(out) }
        }.readBytes() shouldBe standardLibraryClassPathClassBytes
    }

    @Test fun kotlin_copy_class_path(@TempDir tempDir: Path) = test {
        ClassPath(classPathTextFile).copyTo(tempDir.resolve("classPathTextFile-kotlin-copy")) should {
            it.pathString shouldBe tempDir.resolve("classPathTextFile-kotlin-copy").pathString
            it.readBytes() shouldBe classPathTextFileBytes
        }

        ClassPath(standardLibraryClassPathClass).copyTo(tempDir.resolve("standardLibraryClassPathClass-kotlin-copy")) should {
            it.pathString shouldBe tempDir.resolve("standardLibraryClassPathClass-kotlin-copy").pathString
            it.readBytes() shouldBe standardLibraryClassPathClassBytes
        }
    }

    @Test fun kotlin_copy_class_path_to_directory(@TempDir tempDir: Path) = test {
        ClassPath(classPathTextFile).copyToDirectory(tempDir) should {
            it.pathString shouldBe tempDir.resolve(classPathTextFile).pathString
            it.readBytes() shouldBe classPathTextFileBytes
        }

        ClassPath(standardLibraryClassPathClass).copyToDirectory(tempDir) should {
            it.pathString shouldBe tempDir.resolve("Regex.class").pathString
            it.readBytes() shouldBe standardLibraryClassPathClassBytes
        }
    }

    @Test fun kotlin_copy_class_path_to_directory__multiple(@TempDir tempDir: Path) = test {
        for (i in 0..0) {
            ClassPath(classPathTextFile).copyTo(tempDir.resolve(classPathTextFile + i)) should {
                it.pathString shouldBe tempDir.resolve(classPathTextFile + i).pathString
                it.readBytes() shouldBe classPathTextFileBytes
            }

            ClassPath(standardLibraryClassPathClass).copyTo(tempDir.resolve("Regex.class" + i)) should {
                it.pathString shouldBe tempDir.resolve("Regex.class" + i).pathString
                it.readBytes() shouldBe standardLibraryClassPathClassBytes
            }
        }
    }
}

internal const val classPathTextFile = "61C285F09D95930D0AE298B00AF09F918B0A.txt"
internal val classPathTextFileBytes = checkNotNull(Thread.currentThread().contextClassLoader.getResource(classPathTextFile)).readBytes()
internal val classPathTextFileText = classPathTextFileBytes.decodeToString()

internal val standardLibraryClassPathClass = checkNotNull(Regex::class.java.getResource("Regex.class"))
internal val standardLibraryClassPathClassBytes = standardLibraryClassPathClass.readBytes()
internal val standardLibraryClassPathClassText = standardLibraryClassPathClassBytes.decodeToString()
