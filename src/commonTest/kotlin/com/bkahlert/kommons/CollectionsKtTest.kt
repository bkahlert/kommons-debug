package com.bkahlert.kommons

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import kotlin.test.Test

class CollectionsKtTest {

    @Test fun require_not_empty() = tests {
        requireNotEmpty(collection) shouldBe collection
        requireNotEmpty(collection) { "error" } shouldBe collection
        requireNotEmpty(array) shouldBe array
        requireNotEmpty(array) { "error" } shouldBe array
        shouldThrow<IllegalArgumentException> { requireNotEmpty(emptyCollection) }
        shouldThrow<IllegalArgumentException> { requireNotEmpty(emptyCollection) { "error" } } shouldHaveMessage "error"
        shouldThrow<IllegalArgumentException> { requireNotEmpty(emptyArray) }
        shouldThrow<IllegalArgumentException> { requireNotEmpty(emptyArray) { "error" } } shouldHaveMessage "error"
    }

    @Test fun check_not_empty() = tests {
        checkNotEmpty(collection) shouldBe collection
        checkNotEmpty(collection) { "error" } shouldBe collection
        checkNotEmpty(array) shouldBe array
        checkNotEmpty(array) { "error" } shouldBe array
        shouldThrow<IllegalStateException> { checkNotEmpty(emptyCollection) }
        shouldThrow<IllegalStateException> { checkNotEmpty(emptyCollection) { "error" } } shouldHaveMessage "error"
        shouldThrow<IllegalStateException> { checkNotEmpty(emptyArray) }
        shouldThrow<IllegalStateException> { checkNotEmpty(emptyArray) { "error" } } shouldHaveMessage "error"
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

    @Test fun head() = tests {
        collection.head shouldBe "array"
        shouldThrow<NoSuchElementException> { emptyCollection.head }
    }

    @Test fun head_or_null() = tests {
        collection.headOrNull shouldBe "array"
        emptyCollection.headOrNull.shouldBeNull()
    }

    @Test fun tail() = tests {
        listOf("head", "tail").tail shouldBe listOf("tail")
        collection.tail shouldBe emptyList()
        emptyCollection.tail shouldBe emptyList()
    }
}

internal val collection: Collection<String> = listOf("array")
internal val emptyCollection: Collection<String> = emptyList()

internal val array: Array<String> = arrayOf("array")
internal val emptyArray: Array<String> = emptyArray()
