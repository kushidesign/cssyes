(ns cssyes.themes)

;; -----------------------------------------------------------------------------
;; # ANSI colors
;; -----------------------------------------------------------------------------

(def ansi-colors-medium
  "A map of color keywords to their corresponding 256-color ANSI escape codes.
   Values match the medium-* entries of the Bling 8-bit palette. :neutral is
   intentionally absent, so tokens themed :neutral print uncolored."
  {:red     "\033[38;5;196m"
   :orange  "\033[38;5;172m"
   :yellow  "\033[38;5;178m"
   :olive   "\033[38;5;106m"
   :green   "\033[38;5;40m"
   :cyan    "\033[38;5;42m"
   :blue    "\033[38;5;39m"
   :purple  "\033[38;5;141m"
   :magenta "\033[38;5;201m"
   :gray    "\033[38;5;247m"
   :black   "\033[38;5;16m"
   :white   "\033[38;5;231m"
   :reset   "\033[0m"})

(def ansi-colors-soft
  "Lower-chroma alternative to ansi-colors-medium. Values match the soft-*
   entries of the Bling 8-bit palette. The palette has no soft gray, so :gray,
   :black, and :white are the same as in ansi-colors-medium."
  {:red     "\033[38;5;173m"
   :orange  "\033[38;5;136m"
   :yellow  "\033[38;5;142m"
   :olive   "\033[38;5;144m"
   :green   "\033[38;5;108m"
   :cyan    "\033[38;5;73m"
   :blue    "\033[38;5;110m"
   :purple  "\033[38;5;146m"
   :magenta "\033[38;5;175m"
   :gray    "\033[38;5;247m"
   :black   "\033[38;5;16m"
   :white   "\033[38;5;231m"
   :reset   "\033[0m"})

;; Toggle between ansi-colors-medium and ansi-colors-soft here.
(def ansi-colors ansi-colors-medium)


;; -----------------------------------------------------------------------------
;; # Themes
;; -----------------------------------------------------------------------------

;; Universal themes use medium-tone colors that read on light and dark
;; backgrounds.

(def monokai-universal
  {:selectors      :magenta
   :curly-brackets :gray
   :round-brackets :yellow
   :semi-colons    :gray
   :properties     :green
   :values         :blue
   :strings        :yellow
   :vars           :blue
   :commas         :red
   :colons         :red
   :numbers        :blue
   :cssfn          :purple
   :ampersands     :purple
   :pseudo-colons  :magenta
   :comments       :gray})

(def neutral
  {:selectors      :neutral
   :curly-brackets :gray
   :round-brackets :gray
   :semi-colons    :gray
   :properties     :neutral
   :values         :neutral
   :strings        :neutral
   :vars           :neutral
   :commas         :gray
   :colons         :gray
   :numbers        :neutral
   :cssfn          :neutral
   :ampersands     :gray
   :pseudo-colons  :neutral
   :comments       :gray})
