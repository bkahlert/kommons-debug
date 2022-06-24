package com.bkahlert.kommons

import com.bkahlert.kommons.MessageDigestProviders.MD5
import com.bkahlert.kommons.MessageDigestProviders.`SHA-1`
import com.bkahlert.kommons.MessageDigestProviders.`SHA-256`
import com.bkahlert.kommons.debug.FunctionTypes.provider
import com.bkahlert.kommons.test.junit.testEach
import com.bkahlert.kommons.test.test
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.div
import kotlin.io.path.writeBytes

class MessageDigestsKtTest {

    @TestFactory fun hash(@TempDir tempDir: Path) = hashBytes.testEach { (provider, expectedBytes) ->
        bytes.inputStream().hash(provider) shouldBe expectedBytes
        bytes.hash(provider) shouldBe expectedBytes
        (tempDir / provider.name).apply { writeBytes(bytes) }.hash(provider) shouldBe expectedBytes
    }

    @TestFactory fun checksum(@TempDir tempDir: Path) = checksums.testEach { (provider, expectedChecksum) ->
        bytes.inputStream().checksum(provider) shouldBe expectedChecksum
        bytes.checksum(provider) shouldBe expectedChecksum
        (tempDir / provider.name).apply { writeBytes(bytes) }.checksum(provider) shouldBe expectedChecksum
    }

    @Test fun md5_checksum(@TempDir tempDir: Path) = test {
        bytes.inputStream().md5Checksum() shouldBe checksums[MD5]
        bytes.md5Checksum() shouldBe checksums[MD5]
        (tempDir / provider.name).apply { writeBytes(bytes) }.md5Checksum() shouldBe checksums[MD5]
    }

    @Test fun sha1_checksum(@TempDir tempDir: Path) = test {
        bytes.inputStream().sha1Checksum() shouldBe checksums[`SHA-1`]
        bytes.sha1Checksum() shouldBe checksums[`SHA-1`]
        (tempDir / provider.name).apply { writeBytes(bytes) }.sha1Checksum() shouldBe checksums[`SHA-1`]
    }

    @Test fun sha256_checksum(@TempDir tempDir: Path) = test {
        bytes.inputStream().sha256Checksum() shouldBe checksums[`SHA-256`]
        bytes.sha256Checksum() shouldBe checksums[`SHA-256`]
        (tempDir / provider.name).apply { writeBytes(bytes) }.sha256Checksum() shouldBe checksums[`SHA-256`]
    }
}


internal fun byteArrayOf(vararg bytes: Int) =
    bytes.map { it.toByte() }.toByteArray()

internal val bytes = byteArrayOf(Byte.MIN_VALUE, -1, 0, 1, Byte.MAX_VALUE)
internal val hashBytes = mapOf(
    MD5 to byteArrayOf(
        0x0A, 0x12, 0xF1, 0xE7, 0xD3, 0x46, 0xDE, 0x6D,
        0x51, 0xE7, 0x78, 0x8F, 0xE8, 0x7E, 0xCC, 0xD3,
    ),
    `SHA-1` to byteArrayOf(
        0x44, 0x5D, 0x11, 0xBD, 0xF8, 0x71, 0x3F, 0x3D, 0x7F, 0xA1,
        0x33, 0x5B, 0x14, 0x1A, 0x77, 0x98, 0x27, 0x2C, 0xF7, 0x81,
    ),
    `SHA-256` to byteArrayOf(
        0xFE, 0xDA, 0xBE, 0x10, 0xE6, 0x1B, 0x00, 0xD9, 0x13, 0x00, 0x50, 0x16, 0x9D, 0x67, 0x96, 0xDD,
        0x86, 0xFC, 0x72, 0xAE, 0xB4, 0xE8, 0x95, 0xCC, 0x0F, 0x8E, 0xF1, 0x90, 0x1B, 0xED, 0x58, 0x27,
    ),
)

@Suppress("SpellCheckingInspection")
internal val checksums = mapOf(
    MD5 to "0a12f1e7d346de6d51e7788fe87eccd3",
    `SHA-1` to "445d11bdf8713f3d7fa1335b141a7798272cf781",
    `SHA-256` to "fedabe10e61b00d9130050169d6796dd86fc72aeb4e895cc0f8ef1901bed5827",
)
