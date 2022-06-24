package com.bkahlert.kommons

import java.io.InputStream
import java.nio.file.Path
import java.security.DigestInputStream
import java.security.MessageDigest
import kotlin.io.path.inputStream

/** Provider for [MessageDigest] implementations. */
public typealias MessageDigestProvider = () -> MessageDigest

/** [MessageDigestProvider] implementations safe to use on all Java platforms. */
public enum class MessageDigestProviders : MessageDigestProvider {
    /** [MD5](https://en.wikipedia.org/wiki/MD5) */
    MD5,

    /** [SHA-1](https://en.wikipedia.org/wiki/SHA-1) */
    @Suppress("EnumEntryName") `SHA-1`,

    /** [SHA-256](https://en.wikipedia.org/wiki/SHA-256) */
    @Suppress("EnumEntryName") `SHA-256`,
    ;

    /** Provides a new [MessageDigest] instance. */
    public override operator fun invoke(): MessageDigest =
        checkNotNull(MessageDigest.getInstance(name)) { "Failed to instantiate $name message digest" }
}

/** Computes the hash of this input stream using the specified [messageDigestProvider]. */
public fun InputStream.hash(messageDigestProvider: MessageDigestProvider = MessageDigestProviders.`SHA-256`): ByteArray =
    DigestInputStream(this, messageDigestProvider()).use {
        while (it.read() != -1) {
            // clear data
        }
        it.messageDigest.digest()
    }

/** Computes the hash of this byte array using the specified [messageDigestProvider]. */
public fun ByteArray.hash(messageDigestProvider: MessageDigestProvider = MessageDigestProviders.`SHA-256`): ByteArray =
    inputStream().hash(messageDigestProvider)

/** Computes the hash of this file using the specified [messageDigestProvider]. */
public fun Path.hash(messageDigestProvider: MessageDigestProvider = MessageDigestProviders.`SHA-256`): ByteArray =
    inputStream().hash(messageDigestProvider)


/** Computes the hash of this input stream using the specified [messageDigestProvider] and returns it formatted as a hexadecimal checksum. */
public fun InputStream.checksum(messageDigestProvider: MessageDigestProvider = MessageDigestProviders.`SHA-256`): String =
    hash(messageDigestProvider).toHexadecimalString()

/** Computes the hash of this byte array using the specified [messageDigestProvider] and returns it formatted as a hexadecimal checksum. */
public fun ByteArray.checksum(messageDigestProvider: MessageDigestProvider = MessageDigestProviders.`SHA-256`): String =
    hash(messageDigestProvider).toHexadecimalString()

/** Computes the hash of this file using the specified [messageDigestProvider] and returns it formatted as a hexadecimal checksum. */
public fun Path.checksum(messageDigestProvider: MessageDigestProvider = MessageDigestProviders.`SHA-256`): String =
    hash(messageDigestProvider).toHexadecimalString()


/** Computes the [MessageDigestProviders.MD5] hash of this input stream and returns it formatted as a hexadecimal checksum. */
public fun InputStream.md5Checksum(): String = checksum(MessageDigestProviders.MD5)

/** Computes the [MessageDigestProviders.MD5] hash of this byte array and returns it formatted as a hexadecimal checksum. */
public fun ByteArray.md5Checksum(): String = checksum(MessageDigestProviders.MD5)

/** Computes the [MessageDigestProviders.MD5] hash of this file and returns it formatted as a hexadecimal checksum. */
public fun Path.md5Checksum(): String = checksum(MessageDigestProviders.MD5)


/** Computes the [MessageDigestProviders.SHA-1] hash of this input stream and returns it formatted as a hexadecimal checksum. */
public fun InputStream.sha1Checksum(): String = checksum(MessageDigestProviders.`SHA-1`)

/** Computes the [MessageDigestProviders.SHA-1] hash of this byte array and returns it formatted as a hexadecimal checksum. */
public fun ByteArray.sha1Checksum(): String = checksum(MessageDigestProviders.`SHA-1`)

/** Computes the [MessageDigestProviders.SHA-1] hash of this file and returns it formatted as a hexadecimal checksum. */
public fun Path.sha1Checksum(): String = checksum(MessageDigestProviders.`SHA-1`)


/** Computes the [MessageDigestProviders.SHA-256] hash of this input stream and returns it formatted as a hexadecimal checksum. */
public fun InputStream.sha256Checksum(): String = checksum(MessageDigestProviders.`SHA-256`)

/** Computes the [MessageDigestProviders.SHA-256] hash of this byte array and returns it formatted as a hexadecimal checksum. */
public fun ByteArray.sha256Checksum(): String = checksum(MessageDigestProviders.`SHA-256`)

/** Computes the [MessageDigestProviders.SHA-256] hash of this file and returns it formatted as a hexadecimal checksum. */
public fun Path.sha256Checksum(): String = checksum(MessageDigestProviders.`SHA-256`)
