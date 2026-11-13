(ns cssyes.visual
  "Visual check of the preset themes. Prints the same CSS in each theme.
   Run with `bb visual`."
  (:require [cssyes.core :as core]
            [cssyes.core-test :refer [example-1-input]]
            [cssyes.themes :as themes]))

(def preset-themes
  [["monokai-universal" themes/monokai-universal]
   ["neutral-universal" themes/neutral-universal]])

(defn -main [& _]
  (doseq [[theme-name theme] preset-themes]
    (println (str "\n;; " theme-name "\n"))
    (core/print-css example-1-input {:theme theme})))
