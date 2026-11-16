(ns cssyes.visual
  "Visual check of the preset themes. Prints the same CSS in each theme in
   cssyes.themes tagged ^:cssyes.visual/samples, and in the default theme with
   each color variant. Run with `bb test:visual`."
  (:require [clojure.string :as str]
            [cssyes.core :as core]
            [cssyes.core-test :refer [example-1-input]]
            [cssyes.themes]
            [sugar.core :as sugar]))

(def color-variants
  [:shadow :extra-dark :dark :medium :light :extra-light :highlight])

(def themes
  "Symbols for the vars in cssyes.themes tagged ^:cssyes.visual/samples, in
   source order."
  (->> (vals (ns-publics 'cssyes.themes))
       (filter (comp ::samples meta))
       (sort-by (comp :line meta))
       (map symbol)))

(def default-theme
  "The symbol in themes whose value is cssyes.core/default-theme."
  (first (filter #(= core/default-theme @(resolve %)) themes)))

(def samples
  "Options maps for print-css, with the theme as a symbol so the label can show
   its name. Each tagged theme, then the default theme with each color variant."
  (concat (for [variant color-variants]
            {:theme         default-theme
             :color-variant variant})
          (for [theme themes]
            {:theme theme})))

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

(-main)
