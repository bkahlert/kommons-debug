package com.bkahlert.kommons

import com.bkahlert.kommons.Platform.JVM
import com.bkahlert.kommons.ShutdownHookTestHelper.Companion.marker
import com.bkahlert.kommons.test.test
import io.kotest.assertions.asClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.paths.shouldExist
import io.kotest.matchers.paths.shouldNotExist
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption.CREATE_NEW
import kotlin.concurrent.thread
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.pathString
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.test.Test

class JvmPlatformTest {

    @Test fun current() = test {
        Platform.Current shouldBe JVM
    }

    @Test fun context_class_loader() = test {
        JVM.contextClassLoader shouldNotBe null
    }

    @Test fun load_class_or_null() = test {
        JVM.contextClassLoader.loadClassOrNull(randomString()) shouldBe null
        JVM.contextClassLoader.loadClassOrNull("java.lang.String") shouldBe String::class.java
    }

    @Test fun is_debugging() = test {
        Platform.Current.isDebugging shouldBe false
    }

    @Test fun ansi_support() = test {
        if (JVM.isIntelliJ) Platform.Current.ansiSupport shouldNotBe AnsiSupport.NONE
        else Platform.Current.ansiSupport shouldNotBe AnsiSupport.ANSI24
    }

    @Test fun on_exit(@TempDir tempDir: Path) = test {
        tempDir.resolve("on-exit.txt").asClue { file ->
            IsolatedProcess.exec(OnExitTestHelper::class, file.pathString, "did complete") shouldBe 0
            file should {
                it.shouldExist()
                it.readText() shouldBe "did complete"
            }
        }

        tempDir.resolve("on-exit-failure.txt").asClue { file ->
            IsolatedProcess.exec(OnExitTestHelper::class, file.pathString, "too long".repeat(10)) {
                it.environment()["com.bkahlert.kommons.testing-shutdown"] = "true"
            } shouldBe 0
            file.shouldNotExist()
            SystemLocations.Temp
                .listDirectoryEntries("kommons.*.onexit.log")
                .minByOrNull { it.age }
                .shouldNotBeNull()
                .deleteOnExit() should {
                it.shouldExist()
                it.readText()
                    .shouldContain("An exception occurred during shutdown.")
                    .shouldContain("at com.bkahlert.kommons.OnExitTestHelper\$Companion\$main\$1.invoke(JvmPlatformTest.kt:105)")
                    .shouldContain("IllegalArgumentException: too much content")
            }
        }
    }

    @Test fun shutdown_hook() = test {
        marker.delete()
        IsolatedProcess.exec(ShutdownHookTestHelper::class, "create") shouldBe 0
        marker.shouldExist()

        marker.delete()
        IsolatedProcess.exec(ShutdownHookTestHelper::class) shouldBe 0
        marker.shouldNotExist()
    }
}


class OnExitTestHelper {

    companion object {
        /**
         * Writes the last argument to the path specified by the first argument.
         *
         * The file must not exist and located in the temp directory.
         *
         * The content must not have more than 20 characters length.
         */
        @JvmStatic
        fun main(vararg args: String) {
            JVM.onExit {
                val file = Paths.get(args.first()).also { require(!it.exists()) { "file $it already exists" } }
                val content = args.last().also { require(it.length <= 20) { "too much content" } }
                require(file.isSubPathOf(SystemLocations.Temp))
                file.writeText(content, options = arrayOf(CREATE_NEW))
            }
        }
    }
}


class ShutdownHookTestHelper {

    companion object {
        val marker = SystemLocations.Temp.resolve("kommons-shutdown-hook-marker")

        /**
         * Creates the [marker] file if any argument is provided.
         *
         * Otherwise, the creating hook is unregistered.
         */
        @JvmStatic
        fun main(vararg args: String) {
            val hook = thread(start = false) { marker.createFile() }
            JVM.addShutDownHook(hook)

            if (args.isEmpty()) JVM.removeShutdownHook(hook)
        }
    }
}
