package com.bkahlert.kommons.debug

import com.bkahlert.kommons.Locations
import com.bkahlert.kommons.isSubPathOf
import com.bkahlert.kommons.tests
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.div
import kotlin.io.path.pathString
import kotlin.io.path.readText
import kotlin.io.path.writeText

class IsolatedProcessTest {

    @Test
    fun `should execute main`(@TempDir tempDir: Path) = tests {
        val file = tempDir / "file"
        val result = IsolatedProcess.exec(TestClass::class, file.pathString)
        result shouldBe 0
        file.readText() shouldBe "foo"
    }

    @Test
    fun `should return non-zero exit code on error`(@TempDir tempDir: Path) = tests {
        val file = tempDir / "not-existing-dir" / "file"
        val result = IsolatedProcess.exec(TestClass::class, file.pathString)
        result shouldNotBe 0
    }

    @Test
    fun `should throw on missing main`() = tests {
        shouldThrow<IllegalArgumentException> { IsolatedProcess.exec(IsolatedProcessTest::class) }
    }
}

class TestClass {

    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            val file = Paths.get(args.first())
            require(file.isSubPathOf(Locations.Default.Temp))
            file.writeText("foo")
        }
    }
}
