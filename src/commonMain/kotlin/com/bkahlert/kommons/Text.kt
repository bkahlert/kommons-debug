package com.bkahlert.kommons

import com.bkahlert.kommons.Text.Char
import com.bkahlert.kommons.Text.ChunkedText
import com.bkahlert.kommons.TextUnit.Chars
import com.bkahlert.kommons.TextUnit.CodePoints
import com.bkahlert.kommons.TextUnit.Graphemes
import kotlin.jvm.JvmInline
import kotlin.math.roundToInt
import kotlin.reflect.KClass

/** An [Iterator] that iterates text positions between boundaries. */
public typealias PositionIterator = Iterator<IntRange>

/** An [Iterator] that iterates text chunks between boundaries. */
public interface ChunkIterator<T : CharSequence> : Iterator<T>

/** An [Iterator] that iterates text chunks returned by the specified [iterator] and materialized using the specified [materialize]. */
public class ChunkingIterator<T : CharSequence>(
    private val iterator: PositionIterator,
    private val materialize: (IntRange) -> T,
) : ChunkIterator<T> {
    override fun hasNext(): Boolean = iterator.hasNext()
    override fun next(): T = materialize(iterator.next())
}

/** An [Iterator] that iterates [Char] positions. */
public class CharPositionIterator(private val text: CharSequence) : PositionIterator {
    private var index = 0
    override fun hasNext(): Boolean = index < text.length
    override fun next(): IntRange = index++..index
}

/** An [Iterator] that iterates [Char] instances. */
public class CharIterator(private val text: CharSequence) : ChunkIterator<Char> by ChunkingIterator(CharPositionIterator(text), { Char(text, it) })


public interface Text<out T : CharSequence> {

    /**
     * Returns the length of this character sequence.
     */
    public val length: Int

    /**
     * Returns the character at the specified [index] in this character sequence.
     *
     * @throws [IndexOutOfBoundsException] if the [index] is out of bounds of this character sequence.
     *
     * Note that the [String] implementation of this interface in Kotlin/JS has unspecified behavior
     * if the [index] is out of its bounds.
     */
    public operator fun get(index: Int): T

    /**
     * Returns a new character sequence that is a subsequence of this character sequence,
     * starting at the specified [startIndex] and ending right before the specified [endIndex].
     *
     * @param startIndex the start index (inclusive).
     * @param endIndex the end index (exclusive).
     */
    public fun subSequence(startIndex: Int = 0, endIndex: Int = length): CharSequence

    public fun subText(startIndex: Int = 0, endIndex: Int = length): Text<T>

    // TODO test
    public fun asList(): List<T>

    public companion object {

        public fun from(text: CharSequence): Text<Char> = from(text) { it.map(::Char) }

        public fun <T : CharSequence> from(
            text: CharSequence,
            transform: (CharSequence) -> List<T>
        ): Text<T> = ChunkedText(transform(text))

        /** Returns an empty text. */
        public fun <T : CharSequence> emptyText(): Text<T> = EmptyText
        public fun <T : CharSequence> restrictedText(text: CharSequence): Text<T> = RestrictedText(text)
        public fun <T : CharSequence> CharSequence.toText(unit: TextUnit<T>): Text<T> = unit.transform(this)
        public inline fun <reified T : CharSequence> CharSequence.toText(): Text<T> = toText(TextUnit.of())
    }

    @JvmInline
    public value class Char(private val char: kotlin.Char) : CharSequence {
        public constructor(text: CharSequence, index: Int) : this(text[index])
        public constructor(text: CharSequence, range: IntRange) : this(text, range.single())

        override val length: Int get() = 1
        override fun get(index: Int): kotlin.Char = char.also { checkBoundsIndex(indices, index) }
        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            checkBoundsIndexes(length, startIndex, endIndex)
            if (startIndex == endIndex) return String.EMPTY
            return this
        }

        override fun toString(): String = "$char"
    }

    @JvmInline
    public value class ChunkedText<T : CharSequence>(private val chunks: List<T>) : Text<T> {
        override val length: Int get() = chunks.size
        override fun get(index: Int): T {
            checkBoundsIndex(chunks.indices, index)
            return chunks[index]
        }

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            checkBoundsIndexes(chunks.size, startIndex, endIndex)
            if (startIndex == endIndex) return String.EMPTY
            return ComposingCharSequence(chunks.subList(startIndex, endIndex))
        }

        override fun subText(startIndex: Int, endIndex: Int): Text<T> {
            checkBoundsIndexes(chunks.size, startIndex, endIndex)
            if (startIndex == endIndex) return EmptyText
            return ChunkedText(chunks.subList(startIndex, endIndex))
        }

        override fun asList(): List<T> = chunks

        override fun toString(): String = ComposingCharSequence(chunks).toString()
    }

    private object EmptyText : Text<Nothing> {
        override val length: Int get() = 0
        override fun get(index: Int): Nothing = throw IndexOutOfBoundsException("index out of range: $index")
        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = String.EMPTY.also { checkBoundsIndexes(0, startIndex, endIndex) }
        override fun subText(startIndex: Int, endIndex: Int): Text<Nothing> = EmptyText.also { checkBoundsIndexes(0, startIndex, endIndex) }
        override fun asList(): List<Nothing> = emptyList()
        override fun toString(): String = String.EMPTY
    }

    @JvmInline
    private value class RestrictedText private constructor(private val text: String) : Text<Nothing> {
        constructor(text: CharSequence) : this(text.takeIf {
            it.none(kotlin.Char::isSurrogate)
        }?.toString() ?: throw IllegalArgumentException("text must not contain surrogates"))

        override val length: Int get() = text.length
        override fun get(index: Int): Nothing = throw UnsupportedOperationException()
        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            require(startIndex == 0) { "startIndex must be 0" }
            require(endIndex == text.length) { "endIndex must be ${text.length}" }
            return text
        }

        override fun subText(startIndex: Int, endIndex: Int): Text<Nothing> {
            require(startIndex == 0) { "startIndex must be 0" }
            require(endIndex == text.length) { "endIndex must be ${text.length}" }
            return this
        }

        override fun asList(): List<Nothing> = throw UnsupportedOperationException()
        override fun toString(): String = text
    }
}

public operator fun <T : CharSequence> Text<T>.plus(other: Text<T>): Text<T> = ChunkedText(this.asList() + other.asList())

public interface TextUnit<T : CharSequence> {
    public val type: KClass<T>
    public fun transform(text: CharSequence): Text<T>

    public object Chars : TextUnit<Char> by (build { Text.from(it) })
    public object CodePoints : TextUnit<CodePoint> by (build { Text.from(it, CharSequence::toCodePointList) })
    public object Graphemes : TextUnit<Grapheme> by (build { Text.from(it, CharSequence::toGraphemeList) })
    public object Nothings : TextUnit<Nothing> by (build(Nothing::class) { Text.restrictedText(it) })

    public companion object {

        public fun <T : CharSequence> build(
            type: KClass<T>,
            transform: (CharSequence) -> Text<T>
        ): TextUnit<T> = object : TextUnit<T> {
            override val type: KClass<T> = type
            override fun transform(text: CharSequence): Text<T> = transform(text)
        }

        public inline fun <reified T : CharSequence> build(
            noinline transform: (CharSequence) -> Text<T>
        ): TextUnit<T> = build(T::class, transform)

        @Suppress("UNCHECKED_CAST")
        public fun <T : CharSequence> of(unitClass: KClass<T>): TextUnit<T> = when (unitClass) {
            Char::class -> Chars
            CodePoint::class -> CodePoints
            Grapheme::class -> Graphemes
            else -> Nothings
        } as TextUnit<T>

        public inline fun <reified T : CharSequence> of(): TextUnit<T> = of(T::class)
    }
}

/** Represents the length of text in a given [TextUnit]. */
public class TextLength<T : CharSequence>(
    /** The length of text measured in [unit]. */
    public val value: Int,
    public val unit: KClass<T>,
) : Comparable<TextLength<T>> {

    private val name = checkNotNull(unit.simpleName?.lowercase()) { "missing name" }

    public companion object {

        /** Returns a [Chars] equal to this [Int] number of chars. */
        public inline val Int.chars: TextLength<Char> get() = TextLength(this, Char::class)

        /** Returns a [CodePoints] equal to this [Int] number of code points. */
        public inline val Int.codePoints: TextLength<CodePoint> get() = TextLength(this, CodePoint::class)

        /** Returns a [Graphemes] equal to this [Int] number of graphemes. */
        public inline val Int.graphemes: TextLength<Grapheme> get() = TextLength(this, Grapheme::class)

        /** Returns a [TextLength] equal to this [Int] number of the reified [TextUnit]. */
        public inline fun <reified T : CharSequence> Int.toTextLength(): TextLength<T> = TextLength(this, T::class)
    }

    public override fun compareTo(other: TextLength<T>): Int = value.compareTo(other.value)
    override fun toString(): String = when (value) {
        1 -> "$value $name"
        else -> "$value ${name}s"
    }


    // arithmetic operators

    /** Returns the negative of this value. */
    public operator fun unaryMinus(): TextLength<T> = TextLength(-value, unit)

    /** Returns a text length whose value is the sum of this text length value and the [other] value. */
    public operator fun plus(other: Int): TextLength<T> = TextLength(value + other, unit)

    /** Returns a text length whose value is the sum of this and the [other] text length value. */
    public operator fun plus(other: TextLength<T>): TextLength<T> = this + other.value

    /** Returns a text length whose value is the difference between this text length value and the [other] value. */
    public operator fun minus(other: Int): TextLength<T> = this + (-other)

    /** Returns a text length whose value is the difference between this and the [other] text length. */
    public operator fun minus(other: TextLength<T>): TextLength<T> = this - other.value

    /** Returns a text length whose value is this text length value multiplied by the given [scale] number. */
    public operator fun times(scale: Int): TextLength<T> = TextLength(value * scale, unit)

    /**
     * Returns a text length whose value is this text length value multiplied by the given [scale] number.
     *
     * The operation may involve rounding when the result can't be represented exactly with a [Double] number.
     */
    public operator fun times(scale: Double): TextLength<T> {
        val intScale = scale.roundToInt()
        if (intScale.toDouble() == scale) {
            return times(intScale)
        }

        val result = value.toDouble() * scale
        return TextLength(result.toInt(), unit)
    }

    /**
     * Returns a text length whose value is this text length value divided by the given [scale] number.
     *
     * @throws IllegalArgumentException if the operation results in an undefined value for the given arguments,
     * for example, when dividing text length by zero.
     */
    public operator fun div(scale: Int): TextLength<T> {
        if (scale == 0) throw IllegalArgumentException("Dividing text length by zero yields an undefined result.")
        return TextLength(value / scale, unit)
    }

    /**
     * Returns a text length whose value is this text length value divided by the given [scale] number.
     *
     * @throws IllegalArgumentException if the operation results in an undefined value for the given arguments,
     * for example, when dividing an infinite text length by infinity or text length by zero.
     */
    public operator fun div(scale: Double): TextLength<T> {
        val intScale = scale.roundToInt()
        if (intScale.toDouble() == scale && intScale != 0) {
            return div(intScale)
        }

        val result = value.toDouble() / scale
        return TextLength(result.toInt(), unit)
    }

    /** Returns a number that is the ratio of this and the [other] text length value. */
    public operator fun div(other: TextLength<T>): Double = value.toDouble() / other.value.toDouble()

    /** Returns true, if the text length value is less than zero. */
    public fun isNegative(): Boolean = value < 0

    /** Returns true, if the text length value is greater than zero. */
    public fun isPositive(): Boolean = value > 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as TextLength<*>

        if (value != other.value) return false
        if (unit != other.unit) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value
        result = 31 * result + unit.hashCode()
        return result
    }

    /** Returns the absolute value of this value. The returned value is always non-negative. */
    public val absoluteValue: TextLength<T> get() = if (isNegative()) -this else this
}


/** Throws an [IllegalArgumentException] with the result of calling [lazyMessage] if the specified [n] is negative. */
public fun requireNotNegative(
    n: Int,
    lazyMessage: () -> Any = { "Requested text unit count $n is less than zero." }
): Int = n.also { require(n >= 0, lazyMessage) }


/**
 * Returns a subsequence of this character sequence containing the first [n] units of the reified [TextUnit] from this character sequence,
 * or the entire character sequence if this character sequence is shorter.
 *
 * @throws IllegalArgumentException if [n] is negative.
 */
public fun <T : CharSequence> Text<T>.take(n: Int): Text<T> {
    requireNotNegative(n)
    return subText(endIndex = n.coerceAtMost(length))
}

/**
 * Returns a subsequence of this character sequence containing the last [n] units of the reified [TextUnit] from this character sequence,
 * or the entire character sequence if this character sequence is shorter.
 *
 * @throws IllegalArgumentException if [n] is negative.
 */
public fun <T : CharSequence> Text<T>.takeLast(n: Int): Text<T> {
    requireNotNegative(n)
    val length = length
    return subText(startIndex = length - n.coerceAtMost(length))
}


private fun targetLengthFor(length: Int, markerText: Text<*>): Int {
    requireNotNegative(length)
    require(length >= markerText.length) {
        "The specified length ($length) must be greater or equal than the length of the marker ${markerText.quoted} (${markerText.length})."
    }
    return length - markerText.length
}

/**
 * Returns this string truncated from the center to the specified [length] (default: 15)
 * including the [marker] (default: " … ").
 */
public fun <T : CharSequence> Text<T>.truncate(
    length: Int,
    marker: Text<T> = Text.restrictedText(Unicode.ELLIPSIS.spaced)
): Text<T> {
    requireNotNegative(length)
    if (this.length <= length) return this
    val targetLength = targetLengthFor(length, marker)
    val left = truncateEnd(-(-targetLength).floorDiv(2), marker = Text.emptyText())
    val right = truncateStart(targetLength.floorDiv(2), marker = Text.emptyText())
    return left + marker + right
}

/**
 * Returns this string truncated from the start to the specified [length] (default: 15)
 * including the [marker] (default: "… ").
 */
public fun <T : CharSequence> Text<T>.truncateStart(
    length: Int = 15,
    marker: Text<T> = Text.restrictedText(Unicode.ELLIPSIS.endSpaced),
): Text<T> {
    requireNotNegative(length)
    if (this.length <= length) return this
    val targetLength = targetLengthFor(length, marker)
    return marker + takeLast(targetLength)
}

/**
 * Returns this string truncated from the end to the specified [length] (default: 15)
 * including the [marker] (default: " …").
 */
public fun <T : CharSequence> Text<T>.truncateEnd(
    length: Int = 15,
    marker: Text<T> = Text.restrictedText(Unicode.ELLIPSIS.startSpaced),
): Text<T> {
    requireNotNegative(length)
    if (this.length <= length) return this
    val targetLength = targetLengthFor(length, marker)
    return take(targetLength) + marker
}
