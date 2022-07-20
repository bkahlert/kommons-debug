package com.bkahlert.kommons

import kotlin.math.roundToInt

/** A unit of which texts consists of. */
public typealias TextUnit = CharSequence

/** The list of possible text length measurement units, in which a text length can be expressed. */
public enum class TextLengthUnit : Comparable<TextLengthUnit> {

    /** Text length unit representing one [Char]. */
    CHARS {
        override fun length(text: String, startIndex: Int, endIndex: Int): Int =
            if (endIndex < startIndex || startIndex < 0 || endIndex > text.length) {
                throw IndexOutOfBoundsException("begin $startIndex, end $endIndex, length ${text.length}")
            } else {
                endIndex - startIndex
            }

        override fun split(text: String): List<TextUnit> =
            text.indices.map { index -> DelegatingCharSequence(text, index..index) }
    },

    /** Time unit representing one [CodePoint]. */
    CODEPOINTS {
        override fun length(text: String, startIndex: Int, endIndex: Int): Int = text.codePointCount(startIndex, endIndex)
        override fun split(text: String): List<TextUnit> =
            text.toCodePointList().map { DelegatingCharSequence(it.string) }
    },

    /** Time unit representing one [Grapheme]. */
    GRAPHEMES {
        override fun length(text: String, startIndex: Int, endIndex: Int): Int = text.graphemeCount(startIndex, endIndex)
        override fun split(text: String): List<TextUnit> =
            text.toGraphemeList().map { DelegatingCharSequence(it.string) }
    };

    /** Returns the number of units the specified [text] range (default: complete text) consists of. */
    public abstract fun length(text: String, startIndex: Int = 0, endIndex: Int = text.length): Int

    /** Returns the units the specified [text] consists of. */
    public abstract fun split(text: String): List<TextUnit>

    /** Returns the specified [units] joined to a string. */
    public fun join(units: List<TextUnit>): String =
        buildString { units.forEach { append(it) } }

    /**
     * Returns a new string by applying the specified [block] to a mutable list of [TextUnit] instances
     * the specified [text] consists of.
     *
     * The new string consists of the units the provided list contained after block was applied.
     */
    public inline fun transform(text: String, block: MutableList<CharSequence>.() -> Unit): String =
        join(split(text).toMutableList().apply(block))
}

/** Represents the length of text in a given [TextLengthUnit]. */
public data class TextLength(
    /** The length of text measured in [unit]. */
    public val value: Int,
    /** The unit in which the text length is measured. */
    public val unit: TextLengthUnit,
) : Comparable<TextLength> {

    public companion object {
        /** Returns a [TextLength] equal to this [Int] number of chars. */
        public inline val Int.chars: TextLength get() = toTextLength(TextLengthUnit.CHARS)

        /** Returns a [TextLength] equal to this [Int] number of code points. */
        public inline val Int.codePoints: TextLength get() = toTextLength(TextLengthUnit.CODEPOINTS)

        /** Returns a [TextLength] equal to this [Int] number of graphemes. */
        public inline val Int.graphemes: TextLength get() = toTextLength(TextLengthUnit.GRAPHEMES)
    }

    override fun toString(): String {
        val string = "$value ${unit.name.lowercase()}"
        return if (value == 1) string.removeSuffix("s")
        else string
    }

    public override fun compareTo(other: TextLength): Int {
        unit.compareTo(other.unit).let {
            if (it != 0) return it
        }

        return value.compareTo(other.value)
    }

    // arithmetic operators

    /** Returns the negative of this value. */
    public operator fun unaryMinus(): TextLength = TextLength(-value, unit)

    /** Returns a text length whose value is the sum of this text length value and the [other] value. */
    public operator fun plus(other: Int): TextLength = TextLength(value + other, unit)

    /** Returns a text length whose value is the sum of this and the [other] text length value. */
    public operator fun plus(other: TextLength): TextLength {
        require(unit == other.unit) { "Cannot sum text lengths with different units." }
        return this + other.value
    }

    /** Returns a text length whose value is the difference between this text length value and the [other] value. */
    public operator fun minus(other: Int): TextLength = this + (-other)

    /** Returns a text length whose value is the difference between this and the [other] text length. */
    public operator fun minus(other: TextLength): TextLength {
        require(unit == other.unit) { "Cannot subtract text lengths with different units." }
        return this - other.value
    }

    /** Returns a text length whose value is this text length value multiplied by the given [scale] number. */
    public operator fun times(scale: Int): TextLength = TextLength(value * scale, unit)

    /**
     * Returns a text length whose value is this text length value multiplied by the given [scale] number.
     *
     * The operation may involve rounding when the result can't be represented exactly with a [Double] number.
     */
    public operator fun times(scale: Double): TextLength {
        val intScale = scale.roundToInt()
        if (intScale.toDouble() == scale) {
            return times(intScale)
        }

        val result = value.toDouble() * scale
        return result.toInt().toTextLength(unit)
    }

    /**
     * Returns a text length whose value is this text length value divided by the given [scale] number.
     *
     * @throws IllegalArgumentException if the operation results in an undefined value for the given arguments,
     * for example, when dividing text length by zero.
     */
    public operator fun div(scale: Int): TextLength {
        if (scale == 0) throw IllegalArgumentException("Dividing text length by zero yields an undefined result.")
        return TextLength(value / scale, unit)
    }

    /**
     * Returns a text length whose value is this text length value divided by the given [scale] number.
     *
     * @throws IllegalArgumentException if the operation results in an undefined value for the given arguments,
     * for example, when dividing an infinite text length by infinity or text length by zero.
     */
    public operator fun div(scale: Double): TextLength {
        val intScale = scale.roundToInt()
        if (intScale.toDouble() == scale && intScale != 0) {
            return div(intScale)
        }

        val result = value.toDouble() / scale
        return result.toInt().toTextLength(unit)
    }

    /** Returns a number that is the ratio of this and the [other] text length value. */
    public operator fun div(other: TextLength): Double {
        require(unit == other.unit) { "Cannot divide text lengths with different units." }
        return value.toDouble() / other.value.toDouble()
    }

    /** Returns true, if the text length value is less than zero. */
    public fun isNegative(): Boolean = value < 0

    /** Returns true, if the text length value is greater than zero. */
    public fun isPositive(): Boolean = value > 0

    /** Returns the absolute value of this value. The returned value is always non-negative. */
    public val absoluteValue: TextLength get() = if (isNegative()) -this else this
}

/** Returns a [TextLength] equal to this [Int] number of the specified [unit]. */
public fun Int.toTextLength(unit: TextLengthUnit): TextLength =
    TextLength(this, unit)

/** Returns the [TextLength] of the specified range. */
public fun String.length(unit: TextLengthUnit, startIndex: Int = 0, endIndex: Int = length): TextLength =
    TextLength(unit.length(this, startIndex, endIndex), unit)
