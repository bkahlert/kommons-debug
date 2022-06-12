package com.bkahlert.kommons

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import kotlin.test.Test

class CollectionsKtTest {

    @Test fun require_not_empty() = tests {
        collection.requireNotEmpty() shouldBe collection
        collection.requireNotEmpty { "error" } shouldBe collection
        array.requireNotEmpty() shouldBe array
        array.requireNotEmpty { "error" } shouldBe array
        shouldThrow<IllegalArgumentException> { emptyCollection.requireNotEmpty() }
        shouldThrow<IllegalArgumentException> { emptyCollection.requireNotEmpty { "error" } } shouldHaveMessage "error"
        shouldThrow<IllegalArgumentException> { emptyArray.requireNotEmpty() }
        shouldThrow<IllegalArgumentException> { emptyArray.requireNotEmpty { "error" } } shouldHaveMessage "error"
    }

    @Test fun check_not_empty() = tests {
        collection.checkNotEmpty() shouldBe collection
        collection.checkNotEmpty { "error" } shouldBe collection
        array.checkNotEmpty() shouldBe array
        array.checkNotEmpty { "error" } shouldBe array
        shouldThrow<IllegalStateException> { emptyCollection.checkNotEmpty() }
        shouldThrow<IllegalStateException> { emptyCollection.checkNotEmpty { "error" } } shouldHaveMessage "error"
        shouldThrow<IllegalStateException> { emptyArray.checkNotEmpty() }
        shouldThrow<IllegalStateException> { emptyArray.checkNotEmpty { "error" } } shouldHaveMessage "error"
    }

    @Test fun take_if_not_empty() = tests {
        collection.takeIfNotEmpty() shouldBe collection
        array.takeIfNotEmpty() shouldBe array
        emptyCollection.takeIfNotEmpty() shouldBe null
        emptyArray.takeIfNotEmpty() shouldBe null
    }

    @Test fun take_unless_empty() = tests {
        collection.takeUnlessEmpty() shouldBe collection
        array.takeUnlessEmpty() shouldBe array
        emptyCollection.takeUnlessEmpty() shouldBe null
        emptyArray.takeUnlessEmpty() shouldBe null
    }
}

internal val collection: Collection<String> = listOf("array")
internal val emptyCollection: Collection<String> = emptyList()

internal val array: Array<String> = arrayOf("array")
internal val emptyArray: Array<String> = emptyArray()
