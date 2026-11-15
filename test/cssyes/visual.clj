(ns cssyes.visual
  "Visual check of the preset themes. Prints the same CSS in each theme, and in
   monokai-universal with each color variant. Run with `bb test:visual`."
  (:require [clojure.string :as str]
            [cssyes.core :as core]
            [cssyes.core-test :refer [example-1-input]]
            [cssyes.themes]
            [sugar.core :as sugar]))

(def color-variants
  [:shadow :extra-dark :dark :medium :light :extra-light :highlight])

(def samples
  "Options maps for print-css, with the theme as a symbol so the label can show
   its name."
  (concat [{:theme 'cssyes.themes/monokai-universal}]
          (for [variant color-variants]
            {:theme         'cssyes.themes/monokai-universal
             :color-variant variant})
          [{:theme 'cssyes.themes/neutral}]))

(defn- label
  "The options map on one line, without commas."
  [opts]
  (str "{" (str/join " " (map pr-str (apply concat opts))) "}"))

(defn -main [& _]
  (doseq [opts samples]
    (println)
    (sugar/print-styled (str " ;; options ->  " (label opts) " ")
                        {:color            :neutral
                         :background-color :subtle-blue})
    (println)
    (core/print-css example-1-input (update opts :theme (comp deref resolve)))))
