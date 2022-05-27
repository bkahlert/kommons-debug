package com.bkahlert.kommons.debug

internal expect fun nativeObject(): Any

@Suppress("unused")
internal open class BaseClass {
    val baseProperty: String = "base-property"
    open val openBaseProperty: Int = 42
    protected open val protectedOpenBaseProperty: String = "protected-open-base-property"
    private val privateBaseProperty: String = "private-base-property"
}

@Suppress("unused")
internal object Singleton : BaseClass() {
    val singletonProperty: String = "singleton-property"
    private val privateSingletonProperty: String = "private-singleton-property"
}

@Suppress("unused")
val AnonymousSingleton: Any = object {
    val anonymousSingletonProperty: String = "anonymous-singleton-property"
    private val privateAnonymousSingletonProperty: String = "private-anonymous-singleton-property"
}

@Suppress("unused")
internal object ListImplementingSingleton : BaseClass(), List<Any?> by listOf("foo", null) {
    val singletonProperty: String = "singleton-property"
    private val privateSingletonProperty: String = "private-singleton-property"
}

@Suppress("unused")
val ListImplementingAnonymousSingleton = object : List<Any?> by listOf("foo", null) {
    val anonymousSingletonProperty: String = "anonymous-singleton-property"
    private val privateAnonymousSingletonProperty: String = "private-anonymous-singleton-property"
}

@Suppress("unused")
internal object MapImplementingSingleton : BaseClass(), Map<String, Any?> by mapOf("foo" to "bar", "baz" to null) {
    val singletonProperty: String = "singleton-property"
    private val privateSingletonProperty: String = "private-singleton-property"
}

@Suppress("unused")
val MapImplementingAnonymousSingleton = object : Map<String, Any?> by mapOf("foo" to "bar", "baz" to null) {
    val anonymousSingletonProperty: String = "anonymous-singleton-property"
    private val privateAnonymousSingletonProperty: String = "private-anonymous-singleton-property"
}

@Suppress("unused")
internal class OrdinaryClass : BaseClass() {
    val ordinaryProperty: String = "ordinary-property"
    private val privateOrdinaryProperty: String = "private-ordinary-property"
}

internal data class DataClass(
    val dataProperty: String = "data-property",
    override val openBaseProperty: Int = 37,
) : BaseClass() {
    override val protectedOpenBaseProperty: String = "overridden-protected-open-base-property"
    @Suppress("unused") private val privateDataProperty: String = "private-data-property"
}

internal class ClassWithDefaultToString(@Suppress("unused") val foo: Any? = null) {
    val bar: String = "baz"
}

internal class ClassWithCustomToString(@Suppress("unused") val foo: Any? = null) {
    override fun toString(): String = "custom toString"
}

internal class SelfReferencingClass : BaseClass() {
    val selfProperty: SelfReferencingClass = this
}
