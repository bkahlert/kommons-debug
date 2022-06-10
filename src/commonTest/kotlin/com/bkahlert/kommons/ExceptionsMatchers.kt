package com.bkahlert.kommons

import io.kotest.assertions.print.print
import io.kotest.matchers.ComparableMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

infix fun Throwable.shouldHaveRootCauseMessage(rootCauseMessage: String) = this should haveRootCauseMessage(rootCauseMessage)
infix fun Throwable.shouldNotHaveRootCauseMessage(rootCauseMessage: String) = this shouldNot haveRootCauseMessage(rootCauseMessage)

fun haveRootCauseMessage(rootCauseMessage: String) = object : Matcher<Throwable> {
    override fun test(value: Throwable) = ComparableMatcherResult(
        value.rootCause.message?.trim() == rootCauseMessage.trim(),
        {
            "Throwable should have root cause message:\n${rootCauseMessage.trim().print().value}\n\nActual was:\n${
                value.rootCause.message?.trim().print().value
            }\n"
        },
        {
            "Throwable should not have rootCauseMessage:\n${rootCauseMessage.trim().print().value}"
        },
        actual = value.rootCause.message?.trim().print().value,
        expected = rootCauseMessage.trim().print().value,
    )
}

infix fun Throwable.shouldHaveRootCauseMessage(rootCauseMessage: Regex) = this should haveRootCauseMessage(rootCauseMessage)
infix fun Throwable.shouldNotHaveRootCauseMessage(rootCauseMessage: Regex) = this shouldNot haveRootCauseMessage(rootCauseMessage)

fun haveRootCauseMessage(regex: Regex) = object : Matcher<Throwable> {
    override fun test(value: Throwable) = MatcherResult(
        value.rootCause.message?.matches(regex) ?: false,
        { "Throwable should match regex: ${regex.print().value}\nActual was:\n${value.rootCause.message?.trim().print().value}\n" },
        { "Throwable should not match regex: ${regex.print().value}" })
}


fun Throwable.shouldHaveRootCause(block: (Throwable) -> Unit = {}) {
    this should haveRootCause()
    block.invoke(rootCause)
}

fun Throwable.shouldNotHaveRootCause() = this shouldNot haveRootCause()
fun haveRootCause() = object : Matcher<Throwable> {
    override fun test(value: Throwable) = resultForThrowable(value.rootCause)
}

inline fun <reified T : Throwable> Throwable.shouldHaveRootCauseInstanceOf() = this should haveRootCauseInstanceOf<T>()
inline fun <reified T : Throwable> Throwable.shouldNotHaveRootCauseInstanceOf() = this shouldNot haveRootCauseInstanceOf<T>()
inline fun <reified T : Throwable> haveRootCauseInstanceOf() = object : Matcher<Throwable> {
    override fun test(value: Throwable): MatcherResult {
        val rootCause = value.rootCause
        return MatcherResult(
            rootCause is T,
            { "Throwable root cause should be of type ${T::class.bestName()} or it's descendant, but instead got ${rootCause::class.bestName()}" },
            { "Throwable root cause should not be of type ${T::class.bestName()} or it's descendant" })
    }
}

inline fun <reified T : Throwable> Throwable.shouldHaveRootCauseOfType() = this should haveRootCauseOfType<T>()
inline fun <reified T : Throwable> Throwable.shouldNotHaveRootCauseOfType() = this shouldNot haveRootCauseOfType<T>()
inline fun <reified T : Throwable> haveRootCauseOfType() = object : Matcher<Throwable> {
    override fun test(value: Throwable): MatcherResult {
        val rootCause = value.rootCause
        return MatcherResult(
            rootCause::class == T::class,
            { "Throwable root cause should be of type ${T::class.bestName()}, but instead got ${rootCause::class.bestName()}" },
            { "Throwable root cause should not be of type ${T::class.bestName()}" })
    }
}

@PublishedApi
internal fun resultForThrowable(value: Throwable?) = MatcherResult(
    value != null,
    { "Throwable should have a root cause" },
    { "Throwable should not have a root cause" })
