# cssyes

**Hi-fidelity printing for CSS in terminal environments.**  
<sub>Works with Clojure, ClojureScript(Node), Babashka, and Jolt</sub>

<br>


**[Setup]** &nbsp;•&nbsp; **[Usage]** &nbsp;•&nbsp; **[Themes]** &nbsp;•&nbsp; **[Developing]**

[Setup]:       #setup
[Usage]:       #usage
[Themes]:      #themes
[Developing]:  #developing

![cssyes in a Babashka REPL](doc/graphics/screens/bbrepl-white-2.webp#gh-light-mode-only)


## Development Status

| Period | Stage | Release |
|---|---|---|
| 2026 Mar-Nov | Experimental | - |
| 2026 Nov-present  | Alpha | 0.1.0 |


<br />

## Requirements

If using with Babashka, requires `v1.13.222` or higher

If using with Jolt, requires `v0.8.24` or higher

<br />

## Setup

### Add as a dependency to your project:

<br>

Deps:
```clojure
design.kushi/cssyes {:mvn/version "0.1.0"}
```
<br>

Leiningen:
```clojure
[design.kushi/cssyes "0.1.0"]
```
<br>

Require it:
```clojure
(require '[cssyes.core :refer [beautify print-css]])
```
<br>

Or import into your namespace:
```clojure
(ns myns.core
  (:require
    [cssyes.core :refer [beautify print-css]]))
```
<br>

### Consuming via git

Using a tool without Maven support? See
[Consuming cssyes as a git dependency](doc/git-deps.md).

<br>
<br>

## Usage

`cssyes.core/beautify` expects a string of valid CSS and returns a formatted string, tagged with ANSI SGR codes. The SGR codes will activate syntax highlighting in terminal/console environments.

```clojure
(beautify ".btn{color:red;background-color:blue;&:hover{color:gold;}}")
```

Output (without color):
```
.btn {
  color:            red;
  background-color: blue;
  &:hover {
    color: gold;
  }
}
```

`print-css` prints the formatted string and returns `nil`.

```clojure
(print-css ".foo{color:red;}")
(print-css ".foo{color:red;}" {:theme cssyes.themes/neutral})
```

### Options

| Key               | Default                   | Description                                                                              |
| :---              | :---                      | :---                                                                                     |
| `:theme`          | `cssyes.themes/alabaster` | Map of token to color                                                                    |
| `:color-variant`  | `nil`                     | Use one variant for every base color in the theme. See [Color variants](#color-variants) |
| `:indentation`    | `2`                       | Spaces per nesting level                                                                 |
| `:align-values`   | `true`                    | Align (left-justify) values within a block                                               |
| `:supports-color` | `nil`                   | A supports-color `:context` map, for mocking the terminal in tests                       |

<br>

## Themes

Presets in `cssyes.themes`:

- `monokai`
- `alabaster`, the default.
- `zenburn` 
- `neutral`, black / white / gray.

cssyes colors text with [sugar](https://github.com/paintparty/sugar), so each base color adapts to the terminal's background. `:red` is a lighter red on a dark background, a darker red on a light background, and a medium red when the background is not detected. Colors are downsampled to the color level the terminal supports, and output has ANSI SGR codes when the terminal supports none.

### Theme tokens

| Token             | Colors                                         |
| :---              | :---                                           |
| `:selectors`      | Selectors and at-rule text: `.foo`, `@media`   |
| `:ampersands`     | `&`                                            |
| `:pseudo-colons`  | `:` and `::` in selectors and at-rules         |
| `:curly-brackets` | `{` `}`                                        |
| `:round-brackets` | `(` `)`                                        |
| `:properties`     | Property names: `color`                        |
| `:colons`         | `:` after a property                           |
| `:values`         | Plain values and hex colors: `silver`, `#fff`  |
| `:strings`        | Quoted strings: `"cssyes"`                     |
| `:numbers`        | Numbers and units: `10px`, `1.5`, `50%`        |
| `:vars`           | Custom properties: `--gap`                     |
| `:cssfn`          | Function names: `calc`, `color-mix`            |
| `:commas`         | `,`                                            |
| `:semi-colons`    | `;`                                            |
| `:comments`       | `/* ... */`                                    |

<br>

### Theme colors

A token's color should be one of the following base colors, which adapt to the terminal's background:

`:red`<br>`:orange`<br>`:yellow`<br>`:olive`<br>`:green`<br>`:cyan`<br>`:blue`<br>`:purple`<br>`:magenta`<br>`:gray`

`:black`, `:white`, fixed variants such as `:dark-blue` or `:soft-red`, hex strings such as `"#ff5f00"`, rgb vectors such as `[255 95 0]`, and 8-bit ids such as `202` do not adapt.

`:neutral`, or a missing token, prints uncolored.

View [sugar's color docs](https://github.com/paintparty/sugar/blob/main/doc/colors.md), which list every variant.

<br>

### Color variants

`:color-variant` puts the same variant on every base color in the theme, in place of the one sugar picks for the terminal's background. With `:extra-dark`, `:blue` becomes `:extra-dark-blue`.

```clojure
(print-css ".foo{color:red;}" {:theme         cssyes.themes/monokai
                               :color-variant :extra-dark})
```

It takes one of `:shadow`, `:extra-dark`, `:dark`, `:medium`, `:light`, `:extra-light` or `:highlight`. Any other value leaves the theme unchanged. `:black`, `:white`, fixed variants, hex, rgb and 8-bit colors are not changed.

On a terminal with 256 colors, neighboring variants such as `:extra-dark` and `:shadow` can round to the same color.

<br>

### Custom theme example

```clojure
(def my-theme
  {:selectors      :blue
   :ampersands     :blue
   :pseudo-colons  :blue
   :properties     :green
   :strings        :green
   :curly-brackets :gray
   :round-brackets :gray
   :colons         :gray
   :commas         :gray
   :semi-colons    :gray
   :comments       :gray})

(print-css ".foo{color:red;}" {:theme my-theme})
```

<br>

## Developing

Available dev tasks via `bb.edn`:

```sh
bb test            # Clojure tests
bb test:bb         # Babashka tests
bb test:jolt       # Jolt tests
bb test:cljs       # ClojureScript tests on Node (run npm install first)
bb test:all        # all of the above
bb test:visual     # print sample CSS in each preset theme and color variant
bb ci              # run the tests and build the jar
bb install         # install the jar to ~/.m2
bb docs:readme     # after tagging a release: update the version numbers in this README
bb docs:git-deps   # after tagging a release: regenerate doc/git-deps.md
```

### Updating docs after a release

After tagging a release (for example `git tag v0.2.0`), run both docs tasks:

- `bb docs:readme` updates the version numbers in the Setup section of this README to match the tag.
- `bb docs:git-deps` regenerates [doc/git-deps.md](doc/git-deps.md) from [bb/tasks/git-deps.template.md](bb/tasks/git-deps.template.md) with the tag and its commit SHA.

Commit both files and push.

<br>

## Contributing

Issues for bugs, improvements, or features are very welcome. Please file an issue for discussion before starting or issuing a PR.

<br>

## License

Copyright © 2026 Jeremiah Coyle

Distributed under the [Eclipse Public License 2.0](https://www.eclipse.org/legal/epl-2.0)
