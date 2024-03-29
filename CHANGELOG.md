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

## [0.14.0] - 2022-08-08

### Changed

- `ClassPath` delegates to a newly introduced `ClassPathFileSystemProvider` which supports the `classpath` URI scheme.

### Removed

- `usePath` as its intended use cases are now mostly covered by `ClassPathFileSystemProvider`

### Fixed

- fix Windows-related bugs

## [0.13.0] - 2022-08-07

### Added

- Open any `URL`, `URI`, `Path` or `File` using `open` or `locate`.

### Changed

- Render small integers in decimal and hexadecimal form (e.g. "42／0x2a").

## [0.12.0] - 2022-07-11

### Added

- transform, e.g. `"© А-З ÄÖÜäöüẞß".transform("de_DE", "de_DE-ASCII")  // "(C) A-Z AEOEUeaeoeueSSss"`
- `json(Map)`
- Quadruple
- Quintuple
- toMomentString
- factory builders `creator`, `converter`, and `parser`

## [0.11.0] - 2022-07-06

### Added

#### Bytes

- Int.toHexadecimalString() / .toOctalString() / .toBinaryString() / .toByteArray()
- UInt.toHexadecimalString() / .toOctalString() / .toBinaryString() / .toUByteArray()
- Long.toHexadecimalString() / .toOctalString() / .toBinaryString() / .toByteArray()
- ULong.toHexadecimalString() / .toOctalString() / .toBinaryString() / .toUByteArray()

#### Strings

- splitMap
- splitToSequence (with keeping delimiters)
- [LineSeparators](src/commonMain/kotlin/com/bkahlert/kommons/text/LineSeparators.kt): Plethora extension function to work with everyday and exotic Unicode line
  breaks
- graphemeCount, improved emoji support when using asGraphemeSequence

#### Regular Expressions

Match multiline strings with simple glob patterns:

```kotlin
// matching within lines with wildcard
"foo.bar()".matchesGlob("foo.*")  // ✅

// matching across lines with multiline wildcard
"""
foo
  .bar()
  .baz()
""".matchesGlob(
    """
    foo
      .**()
    """.trimIndent()              // ✅
)

"""
foo
  .bar()
  .baz()
""".matchesGlob(
    """
    foo
      .*()
    """.trimIndent()              // ❌ (* doesn't match across lines)
)
```

#### Miscellaneous

- Scaling

## [0.10.1] - 2022-06-30

### Added

- `CodePoint.name` \[only JVM\]
- `firstEnumValueOfOrNull`
- `firstEnumValueOf`

## [0.10.0] - 2022-06-29

### Added

- Either type
- Regular expressions `Regex.UrlRegex` and `Regex.UriRegex`
- Regular expressions operations like `group`, `getGroupValue`, `findAllValues`

## [0.9.0] - 2022-06-28

### Added

- `spaced`/`startSpaced`/`endSpaced`: Adds a space before and/or after a string if there isn't already one.
- `truncate`/`truncateStart`/`truncateEnd`: Truncates a string to a given length.

### Changed

- `renderType(simplified=true)` contains the enclosing class if available

## [0.8.0] - 2022-06-27

### Added

- ANSI support detection
- Any?.asEmoji
- Platform.onExit

## [0.7.2] - 2022-06-25

### Changed

upgrade to kommons-test:0.3.0

## [0.7.1] - 2022-06-24

### Added

NodeJS support

## [0.7.0] - 2022-06-24

### Added

Create files with contents in one call using

- `createTextFile`
- `createBinaryFile`
- `createTempTextFile`
- `createTempBinaryFile`

## [0.6.1] - 2022-06-24

### Added

kommons-test 0.2.0

## [0.6.0] - 2022-06-16

### Added

#### Collections and Ranges

Require or check emptiness of collections and arrays using `requireNotEmpty`
and `checkNotEmpty`.

Iterate any type of closed ranges using `asIterable`.

##### Examples

```kotlin
(-4.2..42.0)
    .asIterable { it + 9 }
    .map { it.toInt() } // [-4, 4, 13, 22, 31, 40]
```

#### File Handling

Read files with
`useInputStream`, `useBufferedInputStream`, `useReader`, and `useBufferedReader`,
and write files with
`useOutputStream`, `useBufferedOutputStream`, `useWriter`, and `useBufferedWriter`.

Map URIs and URLs to a Path with `usePath`, which also works for class path resource.

#### Example

```kotlin
standardLibraryClassPath.usePath {
    it.pathString
} // ����   2� kotlin/text/Regex  java/lang/Object ...
```

Find the class directory, the source directory or the source file itself of a class.

#### Example

```kotlin
Foo::class.findClassesDirectoryOrNull()  // /home/john/dev/project/build/classes/kotlin/jvm/test
Foo::class.findSourceDirectoryOrNull()   // /home/john/dev/project/src/jvmTest/kotlin
Foo::class.findSourceFileOrNull()        // /home/john/dev/project/src/jvmTest/kotlin/packages/source.kt
```

Access a class path resource like any other NIO 2 path using `ClassPath`.

#### Example

```kotlin
ClassPath("dir/to/resource").readText()
ClassPath("dir/to/resource").readBytes()
ClassPath("dir/to/resource").copyToDirectory(SystemLocations.Temp)
ClassPath("dir/to/resource").useBufferedReader { it.readLine() }
```

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
foo().asString()                               // { bar: "baz" }
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
Now + 2.seconds     // 2 seconds in the future
Today - 3.days      // 3 days in the past
Yesterday - 2.days  // 3 days in the past
Tomorrow + 1.days   // the day after tomorrow
Instant.parse("1910-06-22T13:00:00Z") + 5.minutes // 1910-06-22T12:05:00Z
LocalDate.parse("1910-06-22") - 2.days            // 1910-06-20
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

Tries to find out if the program runs inside [IDEA IntelliJ](https://www.jetbrains.com/lp/intellij-frameworks/).

#### Platform.isDebugging

Tries to find out if the program runs in debug mode.

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

SystemLocations.Work / .Home / .Temp

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

- If there is a custom `toString()` it's simply used.
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

[unreleased]: https://github.com/bkahlert/kommons-debug/compare/v0.14.0...HEAD

[0.14.0]: https://github.com/bkahlert/kommons-debug/compare/v0.13.0...v0.14.0

[0.13.0]: https://github.com/bkahlert/kommons-debug/compare/v0.12.0...v0.13.0

[0.12.0]: https://github.com/bkahlert/kommons-debug/compare/v0.11.0...v0.12.0

[0.11.0]: https://github.com/bkahlert/kommons-debug/compare/v0.10.1...v0.11.0

[0.10.1]: https://github.com/bkahlert/kommons-debug/compare/v0.10.0...v0.10.1

[0.10.0]: https://github.com/bkahlert/kommons-debug/compare/v0.9.0...v0.10.0

[0.9.0]: https://github.com/bkahlert/kommons-debug/compare/v0.8.0...v0.9.0

[0.8.0]: https://github.com/bkahlert/kommons-debug/compare/v0.7.2...v0.8.0

[0.7.2]: https://github.com/bkahlert/kommons-debug/compare/v0.7.1...v0.7.2

[0.7.1]: https://github.com/bkahlert/kommons-debug/compare/v0.7.0...v0.7.1

[0.7.0]: https://github.com/bkahlert/kommons-debug/compare/v0.6.1...v0.7.0

[0.6.1]: https://github.com/bkahlert/kommons-debug/compare/v0.6.0...v0.6.1

[0.6.0]: https://github.com/bkahlert/kommons-debug/compare/v0.5.0...v0.6.0

[0.5.0]: https://github.com/bkahlert/kommons-debug/compare/v0.4.0...v0.5.0

[0.4.0]: https://github.com/bkahlert/kommons-debug/compare/v0.3.1...v0.4.0

[0.3.1]: https://github.com/bkahlert/kommons-debug/compare/v0.3.0...v0.3.1

[0.3.0]: https://github.com/bkahlert/kommons-debug/compare/v0.2.0...v0.3.0

[0.2.0]: https://github.com/bkahlert/kommons-debug/compare/v0.1.0...v0.2.0

[0.1.0]: https://github.com/bkahlert/kommons-debug/releases/tag/v0.1.0
