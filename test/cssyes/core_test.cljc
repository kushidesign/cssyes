(ns cssyes.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [cssyes.core :as sut] ; system under test
            [cssyes.themes :as themes]
            [supports-color.core :as supports-color]))

(def example-1-input
".foo {
  &:link {
    color: silver;
  }
  &::before {
    content: \"cssyes\";
  }
  @media(min-width: 1280px) {
    color: orange;
  }
  @supports(color: color-mix(in oklch, red, red)) {
    color: blue;
  }
}")

(defn- mock-context
  "A supports-color :context map for 256 colors on a background of the given
   polarity. Every other env var is nil, so the real environment is not read."
  [polarity]
  {:env (assoc (zipmap supports-color/env-var-keys (repeat nil))
               "FORCE_COLOR" "2"
               "CLITHEME"    polarity)})

(def example-1-ansi-tagged-output-dark
  "\033[38;5;213m.foo\033[m \033[38;5;251m{\033[m
  \033[38;5;183m&\033[m\033[38;5;213m:\033[m\033[38;5;213mlink\033[m \033[38;5;251m{\033[m
    \033[38;5;83mcolor\033[m\033[38;5;198m:\033[m \033[38;5;81msilver\033[m\033[38;5;251m;\033[m
  \033[38;5;251m}\033[m
  \033[38;5;183m&\033[m\033[38;5;213m::\033[m\033[38;5;213mbefore\033[m \033[38;5;251m{\033[m
    \033[38;5;83mcontent\033[m\033[38;5;198m:\033[m \033[38;5;184m\"cssyes\"\033[m\033[38;5;251m;\033[m
  \033[38;5;251m}\033[m
  \033[38;5;213m@\033[m\033[38;5;213mmedia\033[m\033[38;5;184m(\033[m\033[38;5;213mmin-width\033[m\033[38;5;213m:\033[m \033[38;5;213m1280px\033[m\033[38;5;184m)\033[m \033[38;5;251m{\033[m
    \033[38;5;83mcolor\033[m\033[38;5;198m:\033[m \033[38;5;81morange\033[m\033[38;5;251m;\033[m
  \033[38;5;251m}\033[m
  \033[38;5;213m@\033[m\033[38;5;213msupports\033[m\033[38;5;184m(\033[m\033[38;5;213mcolor\033[m\033[38;5;213m:\033[m \033[38;5;213mcolor-mix\033[m\033[38;5;184m(\033[m\033[38;5;213min\033[m \033[38;5;213moklch\033[m\033[38;5;198m,\033[m \033[38;5;213mred\033[m\033[38;5;198m,\033[m \033[38;5;213mred\033[m\033[38;5;184m)\033[m\033[38;5;184m)\033[m \033[38;5;251m{\033[m
    \033[38;5;83mcolor\033[m\033[38;5;198m:\033[m \033[38;5;81mblue\033[m\033[38;5;251m;\033[m
  \033[38;5;251m}\033[m
\033[38;5;251m}\033[m")

(def example-1-ansi-tagged-output-light
  "\033[38;5;127m.foo\033[m \033[38;5;242m{\033[m
  \033[38;5;92m&\033[m\033[38;5;127m:\033[m\033[38;5;127mlink\033[m \033[38;5;242m{\033[m
    \033[38;5;28mcolor\033[m\033[38;5;160m:\033[m \033[38;5;26msilver\033[m\033[38;5;242m;\033[m
  \033[38;5;242m}\033[m
  \033[38;5;92m&\033[m\033[38;5;127m::\033[m\033[38;5;127mbefore\033[m \033[38;5;242m{\033[m
    \033[38;5;28mcontent\033[m\033[38;5;160m:\033[m \033[38;5;136m\"cssyes\"\033[m\033[38;5;242m;\033[m
  \033[38;5;242m}\033[m
  \033[38;5;127m@\033[m\033[38;5;127mmedia\033[m\033[38;5;136m(\033[m\033[38;5;127mmin-width\033[m\033[38;5;127m:\033[m \033[38;5;127m1280px\033[m\033[38;5;136m)\033[m \033[38;5;242m{\033[m
    \033[38;5;28mcolor\033[m\033[38;5;160m:\033[m \033[38;5;26morange\033[m\033[38;5;242m;\033[m
  \033[38;5;242m}\033[m
  \033[38;5;127m@\033[m\033[38;5;127msupports\033[m\033[38;5;136m(\033[m\033[38;5;127mcolor\033[m\033[38;5;127m:\033[m \033[38;5;127mcolor-mix\033[m\033[38;5;136m(\033[m\033[38;5;127min\033[m \033[38;5;127moklch\033[m\033[38;5;160m,\033[m \033[38;5;127mred\033[m\033[38;5;160m,\033[m \033[38;5;127mred\033[m\033[38;5;136m)\033[m\033[38;5;136m)\033[m \033[38;5;242m{\033[m
    \033[38;5;28mcolor\033[m\033[38;5;160m:\033[m \033[38;5;26mblue\033[m\033[38;5;242m;\033[m
  \033[38;5;242m}\033[m
\033[38;5;242m}\033[m")

(deftest a-test
  (testing "example-1-input, with cssyes.core/beautify and the monokai-universal theme, on a dark background."
    (is (= (sut/beautify example-1-input
                         {:theme          themes/monokai-universal
                          :supports-color (mock-context "dark")})
           example-1-ansi-tagged-output-dark)))
  (testing "example-1-input, with cssyes.core/beautify and the monokai-universal theme, on a light background."
    (is (= (sut/beautify example-1-input
                         {:theme          themes/monokai-universal
                          :supports-color (mock-context "light")})
           example-1-ansi-tagged-output-light))))

(def ^:private variant-theme
  {:selectors      :blue
   :properties     "green"
   :colons         :black
   :values         "#ff0000"
   :curly-brackets :neutral})

(deftest color-variant-test
  (let [ctx (mock-context "light")]
    (testing "Prefixes each base color in the theme, and leaves :black, hex and :neutral unchanged."
      (is (= (sut/beautify example-1-input
                           {:theme          variant-theme
                            :color-variant  :extra-dark
                            :supports-color ctx})
             (sut/beautify example-1-input
                           {:theme          {:selectors      :extra-dark-blue
                                             :properties     :extra-dark-green
                                             :colons         :black
                                             :values         "#ff0000"
                                             :curly-brackets :neutral}
                            :supports-color ctx}))))
    (testing "Changes the output from the variant sugar picks for the background."
      (is (not= (sut/beautify example-1-input
                              {:theme          variant-theme
                               :color-variant  :extra-dark
                               :supports-color ctx})
                (sut/beautify example-1-input
                              {:theme          variant-theme
                               :supports-color ctx}))))
    (testing "A value not in cssyes.core/color-variants leaves the theme unchanged."
      (is (= (sut/beautify example-1-input
                           {:theme          variant-theme
                            :color-variant  :bogus
                            :supports-color ctx})
             (sut/beautify example-1-input
                           {:theme          variant-theme
                            :supports-color ctx}))))))
