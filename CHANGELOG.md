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

- reduce chance of recursion due to `render` invoking `toString`

## [0.5.0] - 2022-06-13

### Added

#### Any.asString

##### Examples

```kotlin
foo().asString()                             // { bar: "baz" }
foo(null).asString(excludeNullValues = false)  // { }
```

## [0.4.0] - 2022-06-09

### Added

#### Case Manipulations

Capitalize / de-capitalize strings using `capitalize`/`decapitalize` or
manipulate the case style using `toCasesString` or any of its specializations.

##### Examples

```kotlin
"fooBar".capitalize()    // "FooBar"
"FooBar".decapitalize()  // "fooBar"

"FooBar".toCamelCasedString()           // "fooBar"
"FooBar".toPascalCasedString()          // "FooBar"
"FooBar".toScreamingSnakeCasedString()  // "FOO_BAR"
"FooBar".toKebabCasedString()           // "foo-bar"
"FooBar".toTitleCasedString()           // "Foo Bar"

enum class FooBar { FooBaz }

FooBar::class.simpleCamelCasedName           // "fooBar"
FooBar::class.simplePascalCasedName          // "FooBar"
FooBar::class.simpleScreamingSnakeCasedName  // "FOO_BAR"
FooBar::class.simpleKebabCasedName           // "foo-bar"
FooBar::class.simpleTitleCasedName           // "Foo Bar"

FooBar.FooBaz.camelCasedName           // "fooBaz"
FooBar.FooBaz.pascalCasedName          // "FooBaz"
FooBar.FooBaz.screamingSnakeCasedName  // "FOO_BAZ"
FooBar.FooBaz.kebabCasedName           // "foo-baz"
FooBar.FooBaz.titleCasedName           // "Foo Baz"
```

#### Time Operations for JS

### Changed

Move parameter of `inspect` parameter of tracing functions to receiver, so that

```kotlin
data class Foo(val bar: String = "baz") {
    private val baz = 42.0
}

Foo().trace("details") { it.bar.reversed() }
// output: (sample.kt:5) details ⟨ Foo(bar=baz) ⟩ { "zab" }

Foo().inspect("details") { it.bar.reversed() }
// output: (sample.kt:8) details ⟨ !Foo { baz: !Double 42.0, bar: !String "baz" } ⟩ { !String "zab" }
```

becomes

```kotlin
data class Foo(val bar: String = "baz") {
    private val baz = 42.0
}

Foo().trace("details") { bar.reversed() }
// output: (sample.kt:33) details ⟨ Foo(bar=baz) ⟩ { "zab" }

Foo().inspect("details") { bar.reversed() }
// output: (sample.kt:36) details ⟨ !Foo { baz: !Double 42.0, bar: !String "baz" } ⟩ { !String "zab" }
```

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

[unreleased]: https://github.com/bkahlert/kommons/compare/v0.5.0...HEAD

[0.5.0]: https://github.com/bkahlert/kommons/compare/v0.4.0...v0.5.0

[0.4.0]: https://github.com/bkahlert/kommons/compare/v0.3.1...v0.4.0

[0.3.1]: https://github.com/bkahlert/kommons/compare/v0.3.0...v0.3.1

[0.3.0]: https://github.com/bkahlert/kommons/compare/v0.2.0...v0.3.0

[0.2.0]: https://github.com/bkahlert/kommons/compare/v0.1.0...v0.2.0

[0.1.0]: https://github.com/bkahlert/kommons/releases/tag/v0.1.0
