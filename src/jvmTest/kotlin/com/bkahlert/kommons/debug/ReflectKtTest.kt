package com.bkahlert.kommons.debug

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.paths.shouldBeADirectory
import io.kotest.matchers.paths.shouldBeAFile
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import kotlin.io.path.pathString
import kotlin.reflect.jvm.javaField

class ReflectKtTest {

    @Test fun accessible() {
        val instance = BaseClass()
        val privateProperty = checkNotNull(instance.kProperties0().single { it.name.startsWith("private") }.javaField)

        privateProperty.accessible shouldBe false
        shouldThrow<IllegalAccessException> { privateProperty.get(instance) }

        privateProperty.accessible = true

        privateProperty.accessible shouldBe true
        privateProperty.get(instance) shouldBe "private-base-property"
    }

    @Test fun find_classes_directory_or_null() {
        listOf(
            { this::class.findClassesDirectoryOrNull() },
            { javaClass.findClassesDirectoryOrNull() },
            { StaticClass::class.findClassesDirectoryOrNull() },
            { StaticClass::class.java.findClassesDirectoryOrNull() },
            { InnerClass::class.findClassesDirectoryOrNull() },
            { InnerClass::class.java.findClassesDirectoryOrNull() },
        ).forAll { compute ->
            compute().shouldNotBeNull() should { dir ->
                dir.shouldBeADirectory()
                dir.map { it.pathString }.takeLast(3).shouldContainExactly("kotlin", "jvm", "test")
            }
        }

        String::class.findClassesDirectoryOrNull().shouldBeNull()
    }

    @Test fun find_source_directory_or_null() {
        listOf(
            { this::class.findSourceDirectoryOrNull() },
            { javaClass.findSourceDirectoryOrNull() },
            { StaticClass::class.findSourceDirectoryOrNull() },
            { StaticClass::class.java.findSourceDirectoryOrNull() },
            { InnerClass::class.findSourceDirectoryOrNull() },
            { InnerClass::class.java.findSourceDirectoryOrNull() },
        ).forAll { compute ->
            compute().shouldNotBeNull() should { dir ->
                dir.shouldBeADirectory()
                dir.map { it.pathString }.takeLast(3).shouldContainExactly("src", "jvmTest", "kotlin")
            }
        }

        String::class.findSourceDirectoryOrNull().shouldBeNull()
    }

    @Test fun find_source_file_or_null() {
        listOf(
            { this::class.findSourceFileOrNull() },
            { javaClass.findSourceFileOrNull() },
            { StaticClass::class.findSourceFileOrNull() },
            { StaticClass::class.java.findSourceFileOrNull() },
            { InnerClass::class.findSourceFileOrNull() },
            { InnerClass::class.java.findSourceFileOrNull() },
        ).forAll { compute ->
            compute().shouldNotBeNull() should { file ->
                file.shouldBeAFile()
                file.map { it.pathString }.takeLast(8).shouldContainExactly(
                    "src", "jvmTest", "kotlin", "com", "bkahlert", "kommons", "debug", "ReflectKtTest.kt"
                )
            }
        }

        String::class.findSourceFileOrNull().shouldBeNull()
    }

    inner class StaticClass
    inner class InnerClass
}
