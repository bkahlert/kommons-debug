package com.bkahlert.kommons.debug

/** Constant `0x00` */
public inline val Byte.Companion.ZERO: Byte get() = 0x0

/** Constant `0x00` */
public inline val Byte.Companion.OO: Byte get() = ZERO

/** Constant `0xFF` */
public inline val Byte.Companion.FF: Byte get() = -1

/** Constant `0x00u` */
public inline val UByte.Companion.ZERO: UByte get() = 0x0u

/** Constant `0x00u` */
public inline val UByte.Companion.OO: UByte get() = ZERO

/** Constant `0xFFu` */
public inline val UByte.Companion.FF: UByte get() = MAX_VALUE

/** [ClosedRange] of all valid [UByte] values. */
public inline val UByte.Companion.VALUE_RANGE: UIntRange get() = UByte.Companion.MIN_VALUE..UByte.Companion.MAX_VALUE

/** [ClosedRange] of all valid [Byte] values. */
public inline val Byte.Companion.VALUE_RANGE: IntRange get() = Byte.Companion.MIN_VALUE..Byte.Companion.MAX_VALUE

/** Returns a hexadecimal string representation of this [UByte] value. */
public fun UByte.toHexadecimalString(): String = toString(16).padStart(2, '0')

/** Returns a hexadecimal string representation of this [Byte] value. */
public fun Byte.toHexadecimalString(): String = (toInt() and 0xFF).toString(16).padStart(2, '0')

/** Returns a hexadecimal string representation of this [UByteArray] value. */
public fun UByteArray.toHexadecimalString(): String = joinToString("") { it.toHexadecimalString() }

/** Returns a hexadecimal string representation of this [ByteArray] value. */
public fun ByteArray.toHexadecimalString(): String = joinToString("") { it.toHexadecimalString() }


/** Returns a decimal string representation of this [UByte] value. */
public fun UByte.toDecimalString(): String = toString(10)

/** Returns a decimal string representation of this [Byte] value. */
public fun Byte.toDecimalString(): String = (toInt() and 0xFF).toString(10)


/** Returns an octal string representation of this [UByte] value. */
public fun UByte.toOctalString(): String = toString(8).padStart(3, '0')

/** Returns an octal string representation of this [Byte] value. */
public fun Byte.toOctalString(): String = (toInt() and 0xFF).toString(8).padStart(3, '0')

/** Returns an octal string representation of this [UByteArray] value. */
public fun UByteArray.toOctalString(): String = joinToString("") { it.toOctalString() }

/** Returns an octal string representation of this [ByteArray] value. */
public fun ByteArray.toOctalString(): String = joinToString("") { it.toOctalString() }


/** Returns a binary string representation of this [UByte] value. */
public fun UByte.toBinaryString(): String = toString(2).padStart(8, '0')

/** Returns a binary string representation of this [Byte] value. */
public fun Byte.toBinaryString(): String = (toInt() and 0xFF).toString(2).padStart(8, '0')

/** Returns a binary string representation of this [UByteArray] value. */
public fun UByteArray.toBinaryString(): String = joinToString("") { it.toBinaryString() }

/** Returns a binary string representation of this [ByteArray] value. */
public fun ByteArray.toBinaryString(): String = joinToString("") { it.toBinaryString() }
