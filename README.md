# cssyes

**Makes CSS nicer to read in terminal environments.**  
<sub>Works with Clojure, ClojureScript(Node), Babashka, and Jolt</sub>

<br>



<br>

**[Setup]** &nbsp;•&nbsp; **[Usage]** &nbsp;•&nbsp; **[Themes]** &nbsp;•&nbsp; **[Developing]**

[Setup]:       #setup
[Usage]:       #usage
[Themes]:      #themes
[Developing]:  #developing

<br>

![cssyes in a Babashka REPL](doc/graphics/screens/bbrepl-white.webp#gh-light-mode-only)
![cssyes in a Babashka REPL](doc/graphics/screens/bbrepl.webp#gh-dark-mode-only)

<br>

## Development Status

| Period | Stage | Release |
|---|---|---|
| 2026 Mar-Nov | Experimental | - |
| 2026 Nov-present  | Alpha | 0.1.0 |


<br />

## Setup

If using with Babashka, requires `v1.13.222` or higher

If using with Jolt, requires `v0.8.24` or higher

<br>

Add as a dependency to your project:

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

`beautify` expects a string of valid CSS and returns a formatted string, tagged with ANSI SGR codes. The SGR codes will activate syntax highlighting in terminal/console environments.

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
(print-css ".foo{color:red;}" {:theme cssyes.themes/neutral-universal})
```

### Options

| Key              | Default                              | Description                              |
| :---             | :---                                 | :---                                     |
| `:theme`         | `cssyes.themes/monokai-universal`    | Map of token to color                    |
| `:indentation`   | `2`                                  | Spaces per nesting level                 |
| `:align-values?` | `true`                               | Align (left-justify) values within a block              |

<br>

## Themes

Presets in `cssyes.themes`:

- `monokai-universal`
- `neutral-universal`

Both use medium-tone colors that read on light and dark backgrounds.

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

### Theme colors

`:red` `:orange` `:yellow` `:olive` `:green` `:cyan` `:blue` `:purple` `:magenta` `:gray` `:black` `:white`

`:neutral`, or a missing token, prints uncolored.

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

```
bb test       # Clojure tests
bb test:bb    # Babashka tests
bb test:jolt  # Jolt tests
bb test:cljs  # ClojureScript tests (run npm install first)
bb test:all   # All of the above
bb visual     # Print sample CSS in each preset theme
```

### Docs

```
bb docs:readme     # after tagging a release: update the version numbers in this README
bb docs:git-deps   # after tagging a release: regenerate doc/git-deps.md
```

`bb docs:git-deps` fills in [bb/tasks/git-deps.template.md](bb/tasks/git-deps.template.md) with the tag and its commit SHA. Commit the regenerated `doc/git-deps.md`.

<br>

## License

Copyright © 2026 Jeremiah Coyle

Distributed under the [Eclipse Public License 2.0](https://www.eclipse.org/legal/epl-2.0)
