# Changelog

## [Unreleased]

### Added

#### Any.renderType()

Renders any object's type

#### Platform.Current

Reflects the platform the program runs on, e.g. `Platform.JVM`

### Changed

*none*

### Deprecated

*none*

### Removed

*none*

### Fixed

*none*

## [0.1.0] - 2022-05-24

### Added

#### Any?.render()

Renders any object depending on whether its `toString()` is overridden:

- if there is a custom `toString()` it is simply used
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

[unreleased]: https://github.com/bkahlert/kommons/compare/v0.1.0...HEAD

[0.1.0]: https://github.com/bkahlert/kommons/releases/tag/v0.1.0
