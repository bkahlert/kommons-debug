package com.bkahlert.kommons.debug

import com.bkahlert.kommons.debug.Platform.JS
import com.bkahlert.kommons.debug.Platform.JVM
import com.bkahlert.kommons.tests
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class PropertiesTest {

    @Test fun object_properties_extension() = tests {
        nativeObject().properties shouldBe mapOf(
            "property" to "Function-property",
        )
        BaseClass().properties shouldBe mapOf(
            "baseProperty" to "base-property",
            "openBaseProperty" to 42,
            "protectedOpenBaseProperty" to "protected-open-base-property",
            "privateBaseProperty" to "private-base-property",
        )
        Singleton.properties shouldBe buildMap {
            put("baseProperty", "base-property")
            put("openBaseProperty", 42)
            put("protectedOpenBaseProperty", "protected-open-base-property")
            if (Platform.Current == JS) put("privateBaseProperty", "private-base-property")
            put("singletonProperty", "singleton-property")
            put("privateSingletonProperty", "private-singleton-property")
        }
        AnonymousSingleton.properties shouldBe mapOf(
            "anonymousSingletonProperty" to "anonymous-singleton-property",
            "privateAnonymousSingletonProperty" to "private-anonymous-singleton-property",
        )
        ListImplementingSingleton.properties shouldBe buildMap {
            put("baseProperty", "base-property")
            put("openBaseProperty", 42)
            put("protectedOpenBaseProperty", "protected-open-base-property")
            when (Platform.Current) {
                JS -> put("privateBaseProperty", "private-base-property")
                JVM -> put("size", 2)
            }
            put("singletonProperty", "singleton-property")
            put("privateSingletonProperty", "private-singleton-property")
        }
        ListImplementingAnonymousSingleton.properties shouldBe buildMap {
            if (Platform.Current == JVM) put("size", 2)
            put("anonymousSingletonProperty", "anonymous-singleton-property")
            put("privateAnonymousSingletonProperty", "private-anonymous-singleton-property")
        }
        MapImplementingSingleton.properties shouldBe buildMap {
            put("baseProperty", "base-property")
            put("openBaseProperty", 42)
            put("protectedOpenBaseProperty", "protected-open-base-property")
            when (Platform.Current) {
                JS -> put("privateBaseProperty", "private-base-property")
                JVM -> {
                    put("size", 2)
                    put("keys", MapImplementingSingleton.keys)
                    put("values", MapImplementingSingleton.values)
                    put("entries", MapImplementingSingleton.entries)
                }
            }
            put("singletonProperty", "singleton-property")
            put("privateSingletonProperty", "private-singleton-property")
        }
        MapImplementingAnonymousSingleton.properties shouldBe buildMap {
            if (Platform.Current == JVM) {
                put("size", 2)
                put("keys", MapImplementingSingleton.keys)
                put("values", MapImplementingSingleton.values)
                put("entries", MapImplementingSingleton.entries)
            }
            put("anonymousSingletonProperty", "anonymous-singleton-property")
            put("privateAnonymousSingletonProperty", "private-anonymous-singleton-property")
        }
        OrdinaryClass().properties shouldBe buildMap {
            put("baseProperty", "base-property")
            put("openBaseProperty", 42)
            put("protectedOpenBaseProperty", "protected-open-base-property")
            if (Platform.Current == JS) put("privateBaseProperty", "private-base-property")
            put("ordinaryProperty", "ordinary-property")
            put("privateOrdinaryProperty", "private-ordinary-property")
        }
        DataClass().properties shouldBe buildMap {
            put("baseProperty", "base-property")
            put("openBaseProperty", 37)
            put("protectedOpenBaseProperty", "overridden-protected-open-base-property")
            if (Platform.Current == JS) put("privateBaseProperty", "private-base-property")
            put("dataProperty", "data-property")
            put("privateDataProperty", "private-data-property")
        }
        val customObject = ClassWithDefaultToString(null)
        customObject.properties shouldBe mapOf(
            "foo" to null,
            "bar" to "baz",
        )
        ClassWithDefaultToString(customObject).properties shouldBe mapOf(
            "foo" to customObject,
            "bar" to "baz",
        )
    }
}
