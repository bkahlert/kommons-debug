package com.bkahlert.kommons

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class BytesTest {

    @Test fun to_hexadecimal_string() = tests {
        ubyteArray should { array ->
            array.map { it.toHexadecimalString() } shouldContainExactly listOf("00", "7f", "80", "ff")
            array.toHexadecimalString() shouldBe "007f80ff"
        }
        byteArray should { array ->
            array.map { it.toHexadecimalString() } shouldContainExactly listOf("00", "7f", "80", "ff")
            array.toHexadecimalString() shouldBe "007f80ff"
        }

        @Suppress("SpellCheckingInspection")
        largeUbyteArray.toHexadecimalString() shouldBe "ffffffffffffffffffffffffffffffff"
        @Suppress("SpellCheckingInspection")
        largeByteArrayOf.toHexadecimalString() shouldBe "ffffffffffffffffffffffffffffffff"

        veryLargeUbyteArrayOf.toHexadecimalString() shouldBe "0100000000000000000000000000000000"
        veryLargeByteArray.toHexadecimalString() shouldBe "0100000000000000000000000000000000"
    }

    @Test fun to_decimal_string() = tests {
        ubyteArray should { array ->
            array.map { it.toDecimalString() } shouldContainExactly listOf("0", "127", "128", "255")
        }
        byteArray should { array ->
            array.map { it.toDecimalString() } shouldContainExactly listOf("0", "127", "128", "255")
        }
    }

    @Test fun to_octal_string() = tests {
        ubyteArray should { array ->
            array.map { it.toOctalString() } shouldContainExactly listOf("000", "177", "200", "377")
            array.toOctalString() shouldBe "000177200377"
        }
        byteArray should { array ->
            array.map { it.toOctalString() } shouldContainExactly listOf("000", "177", "200", "377")
            array.toOctalString() shouldBe "000177200377"
        }

        largeUbyteArray.toOctalString() shouldBe "377377377377377377377377377377377377377377377377"
        largeByteArrayOf.toOctalString() shouldBe "377377377377377377377377377377377377377377377377"

        veryLargeUbyteArrayOf.toOctalString() shouldBe "001000000000000000000000000000000000000000000000000"
        veryLargeByteArray.toOctalString() shouldBe "001000000000000000000000000000000000000000000000000"
    }

    @Suppress("LongLine")
    @Test fun to_binary_string() = tests {
        ubyteArray should { array ->
            array.map { it.toBinaryString() } shouldContainExactly listOf("00000000", "01111111", "10000000", "11111111")
            array.toBinaryString() shouldBe "00000000011111111000000011111111"
        }
        byteArray should { array ->
            array.map { it.toBinaryString() } shouldContainExactly listOf("00000000", "01111111", "10000000", "11111111")
            array.toBinaryString() shouldBe "00000000011111111000000011111111"
        }

        largeUbyteArray.toBinaryString() shouldBe "11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111"
        largeByteArrayOf.toBinaryString() shouldBe "11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111"

        veryLargeUbyteArrayOf.toBinaryString() shouldBe "0000000100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"
        veryLargeByteArray.toBinaryString() shouldBe "0000000100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"
    }
}

internal val ubyteArray = ubyteArrayOf(0x00u, 0x7fu, 0x80u, 0xffu)
internal val byteArray = byteArrayOf(0x00, 0x7f, -0x80, -0x01)
internal val largeUbyteArray = ubyteArrayOf(0xffu, 0xffu, 0xffu, 0xffu, 0xffu, 0xffu, 0xffu, 0xffu, 0xffu, 0xffu, 0xffu, 0xffu, 0xffu, 0xffu, 0xffu, 0xffu)
internal val largeByteArrayOf = byteArrayOf(-0x01, -0x01, -0x01, -0x01, -0x01, -0x01, -0x01, -0x01, -0x01, -0x01, -0x01, -0x01, -0x01, -0x01, -0x01, -0x01)
internal val veryLargeUbyteArrayOf = ubyteArrayOf(0x1u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u, 0x0u)
internal val veryLargeByteArray = byteArrayOf(0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)
