# Changelog
cssyes: CSS formatting and syntax highlighting for the terminal, in Clojure and Babashka.


## Unreleased

### Changed
- Rename `cssyes.themes/monokai-universal` to `cssyes.themes/monokai`.
- Make `cssyes.themes/monokai` the default theme, in place of `cssyes.themes/neutral`.

### Fixed
- Color comments with the theme's `:comments` color. Only a comment at the very start of the input was colored. Others were printed as part of the selector or declaration around them.


## 0.1.0

### Added
- Initial commit.
