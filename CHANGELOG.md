# Changelog

## [Unreleased]

### Added

*none*

### Changed

*none*

### Deprecated

*none*

### Removed

*none*

### Fixed

*none*

## [0.3.1] - 2022-06-01

### Fixed

- properly indent formatted stack trace
- properly display call-site

## [0.3.0] - 2022-05-31

### Added

#### Time Operations

```kotlin
Now + 2.seconds // 2 seconds in the future
Now - 3.days    // 3 days in the past
Instant.parse("1910-06-22T13:00:00Z") + 5.minutes // 1910-06-22T12:05:00Z
Instant.parse("1910-06-22T13:00:00Z") - 2.hours   // 1910-06-22T10:00:00Z
```

## [0.2.0] - 2022-05-29

### Added

#### trace

Print tracing information

#### Any.renderType()

Renders any object's type

#### Platform.Current

Reflects the platform the program runs on, e.g. `Platform.JVM`

#### Platform.isIntelliJ

Tries to find out if the program is currently run inside IDEA Intellij

#### Platform.isDebugging

Tries to find out if the program is currently in debug mode

#### Stack Trace

Access the current stack trace by a simple call to `StackTrace.get()`
or locate a specific caller using `StackTrace.findLastKnownCallOrNull`

#### Byte, UByte, ByteArray, UByteArray Conversions

All Byte, UByte, ByteArray, UByteArray instances support

- toHexadecimalString
- toOctalString
- toBinaryString

#### Checksums

Compute MD5, SHA-1, SHA-256 checksums for arbitrary files

#### Default system locations

Locations.Default.Work / .Home / .Temp

#### Unicode

Decode any string to a sequence or list of code points using `String.asCodePointSequence` or `String.toCodePointList`.

Decode any string to a sequence or list of graphemes using `String.asGraphemeSequence` or `String.toGraphemeList`.

#### String Handling

Create an identifier from any string using `CharSequence?.toIdentifier`
or easily create a random string using `randomString`.

Less verbose edge-case handling using `requireNotEmpty`, `requireNotBlank`, `checkNotEmpty`, `checkNotBlank`,
`takeIfNotEmpty`, `takeIfNotBlank`, `takeUnlessEmpty` and `takeUnlessBlank`.


## [0.1.0] - 2022-05-24

### Added

#### Any?.render()

Renders any object depending on whether its `toString()` is overridden:

- If there is a custom `toString()` it is simply used.
- if there is *no custom* `toString()` the object is serialized in the form `<TYPE>(key0=value0, key1=value=1, ..., keyN=valueN)`

#### Any.properties

Contains a map of the object's properties with each entry representing
the name and value of a property.

#### CharSequence.quoted

Contains this string wrapped in double quotes
and backslashes, line feeds, carriage returns, tabs and double quotes escaped.

### Changed

*none*

### Deprecated

*none*

### Removed

*none*

### Fixed

*none*

[unreleased]: https://github.com/bkahlert/kommons/compare/v0.3.1...HEAD

[0.3.1]: https://github.com/bkahlert/kommons/compare/v0.3.0...v0.3.1

[0.3.0]: https://github.com/bkahlert/kommons/compare/v0.2.0...v0.3.0

[0.2.0]: https://github.com/bkahlert/kommons/compare/v0.1.0...v0.2.0

[0.1.0]: https://github.com/bkahlert/kommons/releases/tag/v0.1.0
