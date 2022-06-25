package com.bkahlert.kommons

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


private val base64Characters = ("" +
    "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
    "abcdefghijklmnopqrstuvwxyz" +
    "0123456789+/"
    ).toCharArray()

private val base64UrlSafeReplacements = listOf(
    "+" to "-",
    "/" to "_",
    "=" to "%3d"
)

private const val base64PaddingCharacters = '='

/** Encodes this byte array into a string using [Base64](https://en.wikipedia.org/wiki/Base64). */
public fun ByteArray.encodeToBase64(urlSafe: Boolean = false, chunked: Boolean = true): String {
    val sb = StringBuilder()
    var blocks = 0
    val byteCount = size
    val n = byteCount + byteCount % 3
    for (i in 0 until n step 3) {
        val c0 = if (byteCount > i) (this[i].toInt() and 0xff) else break
        val c1 = if (byteCount > i + 1) (this[i + 1].toInt() and 0xff) else -1
        val c2 = if (byteCount > i + 2) (this[i + 2].toInt() and 0xff) else -1
        val block = (c0 shl 16) or (c1.coerceAtLeast(0) shl 8) or (c2.coerceAtLeast(0))
        sb.append(base64Characters[block shr 18 and 63])
        sb.append(base64Characters[block shr 12 and 63])
        sb.append(if (c1 == -1) base64PaddingCharacters else base64Characters[block shr 6 and 63])
        sb.append(if (c2 == -1) base64PaddingCharacters else base64Characters[block and 63])
        if (++blocks == 19) {
            blocks = 0
            if (chunked) sb.append(LineSeparators.CRLF)
        }
    }
    if (blocks > 0 && chunked) sb.append(LineSeparators.CRLF)
    return if (urlSafe) base64UrlSafeReplacements
        .fold(sb.toString()) { acc, (from, to) -> acc.replace(from, to) }
    else sb.toString()
}

/** Decodes this [Base64](https://en.wikipedia.org/wiki/Base64) encoded string into a newly-allocated byte array. */
public fun String.decodeFromBase64(): ByteArray {
    if (isEmpty()) return ByteArray(0)
    val cleaned = base64UrlSafeReplacements
        .fold(this) { acc, (to, from) -> acc.replace(from, to) }
        .filter { it in base64Characters || it == base64PaddingCharacters }
    val bytes = ByteArray(cleaned.length * 3 / 4)
    val strLength = cleaned.length
    val pad = cleaned.takeLastWhile { it == base64PaddingCharacters }
    val padded = buildString {
        append(cleaned, 0, strLength - pad.length)
        repeat(pad.length) { append(base64Characters[0]) }
    }
    var i = 0
    var j = 0
    while (i < padded.length) {
        val part1 = base64Characters.indexOf(padded[i])
        val part2 = base64Characters.indexOf(padded[i + 1])
        val part3 = base64Characters.indexOf(padded[i + 2])
        val part4 = base64Characters.indexOf(padded[i + 3])
        val byte1 = part1 shl 2 or (part2 shr 4) and 255
        val byte2 = part2 shl 4 or (part3 shr 2) and 255
        val byte3 = part3 shl 6 or part4 and 255
        bytes[j] = byte1.toByte()
        bytes[j + 1] = byte2.toByte()
        bytes[j + 2] = byte3.toByte()
        i += 4
        j += 3
    }
    return bytes.copyOfRange(0, bytes.size - pad.length)
}
