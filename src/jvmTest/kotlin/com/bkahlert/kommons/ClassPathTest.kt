package com.bkahlert.kommons

import com.bkahlert.kommons.debug.trace
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.inspectors.forAll
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import kotlin.io.path.copyTo
import kotlin.io.path.extension
import kotlin.io.path.pathString
import kotlin.io.path.readBytes
import kotlin.io.path.readText

class ClassPathTest {

    @Nested
    inner class UseClassPaths {
        @Test fun `should map root with no provided path`() = tests {
            useClassPaths("") { it.listDirectoryEntriesRecursively() }
                .flatten().count { it.fileName.pathString.endsWith(".class") }.shouldBeGreaterThan(20)
        }

        @Test
        fun `should map resource on matching path`() = tests {
            useClassPaths(classPathTextFile) { it.readBytes() }
                .forAll { it.contentEquals(classPathTextFileBytes).shouldBeTrue() }
        }

        @Test
        fun `should map resource on non-matching path`() = tests {
            useClassPaths("invalid.file") { it.pathString }.shouldBeEmpty()
        }

        @Test
        fun `should support different notations`() = tests {
            listOf(
                classPathTextFile,
                "/$classPathTextFile",
                "classpath:$classPathTextFile",
                "classpath:/$classPathTextFile",
            ).forAll {
                useClassPaths(it) { it.readBytes() }
                    .forAll { it.contentEquals(classPathTextFileBytes).shouldBeTrue() }
            }
        }
    }

    @Nested
    inner class UseClassPath {
        @Test fun `should map root with no provided path`() = tests {
            useClassPath("") { it.listDirectoryEntriesRecursively() }
                .shouldNotBeNull()
                .count { it.fileName.pathString.endsWith(".class") }.shouldBeGreaterThan(20)
        }

        @Test
        fun `should map resource on matching path`() = tests {
            useClassPath(classPathTextFile) { it.readBytes() }
                .contentEquals(classPathTextFileBytes).shouldBeTrue()
        }

        @Test
        fun `should map resource on non-matching path`() = tests {
            useClassPath("invalid.file") { it.pathString }.shouldBeNull()
        }

        @Test
        fun `should support different notations`() = tests {
            listOf(
                classPathTextFile,
                "/$classPathTextFile",
                "classpath:$classPathTextFile",
                "classpath:/$classPathTextFile",
            ).forAll {
                useClassPath(it) { it.readBytes() }
                    .contentEquals(classPathTextFileBytes).shouldBeTrue()
            }
        }
    }

    @Test fun `use required class path`() = tests {
        useRequiredClassPath(classPathTextFile) { it.readBytes() }
            .contentEquals(classPathTextFileBytes).shouldBeTrue()
        shouldThrow<NoSuchFileException> { useRequiredClassPath("invalid.file") {} }
    }

    @Test fun `read class path`() = tests {
        readClassPathText(classPathTextFile) shouldBe classPathTextFileBytes.decodeToString()
        readClassPathBytes(classPathTextFile).contentEquals(classPathTextFileBytes).shouldBeTrue()
        readClassPathText("invalid.file").shouldBeNull()
        readClassPathBytes("invalid.file").shouldBeNull()
    }

    @Test fun `require class path`() = tests {
        requireClassPathText(classPathTextFile) shouldBe classPathTextFileBytes.decodeToString()
        requireClassPathBytes(classPathTextFile).contentEquals(classPathTextFileBytes).shouldBeTrue()
        shouldThrow<NoSuchFileException> { requireClassPathText("invalid.file") }
        shouldThrow<NoSuchFileException> { requireClassPathBytes("invalid.file") }
    }


    @Test fun class_path() = tests {
        ClassPath(classPathTextFile) should {
            it.readText() shouldBe classPathTextFileText
            it.readBytes() shouldBe classPathTextFileBytes
        }
        ClassPath(standardLibraryClassPathClass) should {
            it.readText() shouldBe standardLibraryClassPathClassText
            it.readBytes() shouldBe standardLibraryClassPathClassBytes
        }
        shouldThrow<NoSuchFileException> { ClassPath("invalid.file") }
    }

    @Test fun class_path_use(@TempDir tempDir: Path) = tests {
        ClassPath(classPathTextFile).useBufferedInputStream {
            tempDir.resolve("classPathTextFile-streamed-copy").useOutputStream { out -> it.copyTo(out) }
        }.readBytes() shouldBe classPathTextFileBytes

        ClassPath(standardLibraryClassPathClass).useBufferedInputStream {
            tempDir.resolve("standardLibraryClassPathClass-streamed-copy").useOutputStream { out -> it.copyTo(out) }
        }.readBytes() shouldBe standardLibraryClassPathClassBytes

        ClassPath(classPathTextFile).copyTo(tempDir.resolve("classPathTextFile-kotlin-copy"))
            .readBytes() shouldBe classPathTextFileBytes

//        ClassPath(standardLibraryClassPathClass).copyTo(tempDir.resolve("standardLibraryClassPathClass-kotlin-copy").trace).trace
//            .readBytes() shouldBe standardLibraryClassPathClassBytes
    }

    @Test fun class_pathxx() = tests {
        val p = ClassPath(classPathTextFile).trace
        p.extension.trace("file name")
        p.readText().trace("file name")
        ClassPath(checkNotNull(Regex::class.java.getResource("Regex.class"))).readText().trace("file name")
        val inputStream = Files.newInputStream(p)
        inputStream.trace
    }
}

internal val classPathTextFile = "61C285F09D95930D0AE298B00AF09F918B0A.txt"
internal val classPathTextFileBytes = ubyteArrayOf(
    0x61u, 0xC2u, 0x85u, 0xF0u, 0x9Du, 0x95u, 0x93u, 0x0Du, 0x0Au,
    0xE2u, 0x98u, 0xB0u, 0x0Au, 0xF0u, 0x9Fu, 0x91u, 0x8Bu, 0x0Au
).toByteArray()
internal val classPathTextFileText = classPathTextFileBytes.decodeToString()

internal val standardLibraryClassPathClass = checkNotNull(Regex::class.java.getResource("Regex.class"))
internal val standardLibraryClassPathClassBytes = standardLibraryClassPathClass.readBytes()
internal val standardLibraryClassPathClassText = standardLibraryClassPathClassBytes.decodeToString()
