package com.bkahlert.kommons.debug

import com.bkahlert.kommons.debug.Platform.JVM
import com.bkahlert.kommons.randomString
import com.bkahlert.kommons.tests
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

class JvmPlatformTest {

    @Test fun current() = tests {
        Platform.Current shouldBe JVM
    }

    @Test fun context_class_loader() = tests {
        Platform.contextClassLoader shouldNotBe null
    }

    @Test fun load_class_or_null() = tests {
        Platform.contextClassLoader.loadClassOrNull(randomString()) shouldBe null
        Platform.contextClassLoader.loadClassOrNull("java.lang.String") shouldBe String::class.java
    }
}
