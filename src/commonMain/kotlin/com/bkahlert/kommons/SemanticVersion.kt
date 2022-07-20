package com.bkahlert.kommons

import com.bkahlert.kommons.Parser.Companion.parser

/**
 * Semantic Version of the format [major].[minor].[patch].
 * @see <a href="https://semver.org">Semantic Versioning 2.0.0</a>
 */
public data class SemanticVersion(
    /** The major version which increases when making incompatible API changes. */
    val major: Int,
    /** The minor version which increases when adding features in a backward compatible manner. */
    val minor: Int,
    /** The patch version which increases when making backwards compatible bug fixes. */
    val patch: Int,
    /** Optional pre-release tag, not including the dash `-`, marking a version a release candidate. */
    val preRelease: String? = null,
    /** Optional build number, not including the plus `+`, identifying a specific build. */
    val build: String? = null,
) {

    /** Returns the formatted semantic version. */
    override fun toString(): String =
        buildString {
            append(major)
            append('.')
            append(minor)
            append('.')
            append(patch)
            preRelease?.also {
                append('-')
                append(it)
            }
            build?.also {
                append('+')
                append(it)
            }
        }

    public companion object : Parser<SemanticVersion> by (parser { string ->
        regex.matchEntire(string)?.run {
            SemanticVersion(
                major = groupValues[1].toInt(),
                minor = groupValues[2].toInt(),
                patch = groupValues[3].toInt(),
                preRelease = groupValues.drop(4).firstOrNull { it.startsWith("-") }?.drop(1),
                build = groupValues.drop(4).firstOrNull { it.startsWith("+") }?.drop(1),
            )
        }
    })
}

private val regex = Regex(
    "(?<major>\\d+)\\.(?<minor>\\d+)\\.(?<patch>\\d+)" +
        "(?<preRelease>-(?:\\w+\\.)*\\w+)?" +
        "(?<build>\\+(?:\\w+\\.)*\\w+)?"
)
