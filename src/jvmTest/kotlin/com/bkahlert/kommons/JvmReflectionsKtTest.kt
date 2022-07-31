package com.bkahlert.kommons

import com.bkahlert.kommons.test.testAll
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import org.junit.jupiter.api.Test

class JvmReflectionsKtTest {

    @Test fun all_sealed_subclasses() = testAll {
        JvmReflectionsKtTest::class.allSealedSubclasses.shouldBeEmpty()
        Platform::class.allSealedSubclasses.shouldContainExactly(
            Platform.JS::class,
            Platform.JS.Browser::class,
            Platform.JS.NodeJS::class,
            Platform.JVM::class
        )
    }

    @Test fun all_sealed_object_instances() = testAll {
        JvmReflectionsKtTest::class.allSealedObjectInstances.shouldBeEmpty()
        Platform::class.allSealedObjectInstances.shouldContainExactly(
            Platform.JS.Browser,
            Platform.JS.NodeJS,
            Platform.JVM
        )
    }
}
