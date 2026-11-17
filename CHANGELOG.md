# Changelog
cssyes: CSS formatting and syntax highlighting for the terminal, in Clojure and Babashka.


## Unreleased


## 0.3.0
2026-11-16

### Changed
- Make `cssyes.themes/alabaster` the default theme, in place of `cssyes.themes/monokai`.
- Rename the `:align-values?` option to `:align-values`.

## 0.2.0
2026-11-16

### Changed
- Rename `cssyes.themes/monokai-universal` to `cssyes.themes/monokai`.
- Make `cssyes.themes/monokai` the default theme, in place of `cssyes.themes/neutral`.

### Fixed
- Color comments with the theme's `:comments` color. Only a comment at the very start of the input was colored. Others were printed as part of the selector or declaration around them.


## 0.1.0
2026-11-14

### Added
- Initial commit.
