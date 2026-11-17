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
  /* media query */
  @media(min-width: 1280px) {
    color: var(--my-color);
    background: linear-gradient(black, white);
    line-height: 1.2;
    padding: 20px;
  }
  /* feature support */
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

(def example-1-ansi-tagged-output-dark-monokai
  "\033[38;5;213m.foo\033[m \033[38;5;251m{\033[m
  \033[38;5;183m&\033[m\033[38;5;213m:\033[m\033[38;5;213mlink\033[m \033[38;5;251m{\033[m
    \033[38;5;83mcolor\033[m\033[38;5;198m:\033[m \033[38;5;81msilver\033[m\033[38;5;251m;\033[m
  \033[38;5;251m}\033[m
  \033[38;5;183m&\033[m\033[38;5;213m::\033[m\033[38;5;213mbefore\033[m \033[38;5;251m{\033[m
    \033[38;5;83mcontent\033[m\033[38;5;198m:\033[m \033[38;5;184m\"cssyes\"\033[m\033[38;5;251m;\033[m
  \033[38;5;251m}\033[m
  \033[38;5;251m/* media query */\033[m
  \033[38;5;213m@\033[m\033[38;5;213mmedia\033[m\033[38;5;184m(\033[m\033[38;5;213mmin-width\033[m\033[38;5;213m:\033[m \033[38;5;213m1280px\033[m\033[38;5;184m)\033[m \033[38;5;251m{\033[m
    \033[38;5;83mcolor\033[m\033[38;5;198m:\033[m       \033[38;5;183mvar\033[m\033[38;5;184m(\033[m\033[38;5;81m--my-color\033[m\033[38;5;184m)\033[m\033[38;5;251m;\033[m
    \033[38;5;83mbackground\033[m\033[38;5;198m:\033[m  \033[38;5;183mlinear-gradient\033[m\033[38;5;184m(\033[m\033[38;5;81mblack\033[m\033[38;5;198m,\033[m \033[38;5;81mwhite\033[m\033[38;5;184m)\033[m\033[38;5;251m;\033[m
    \033[38;5;83mline-height\033[m\033[38;5;198m:\033[m \033[38;5;81m1.2\033[m\033[38;5;251m;\033[m
    \033[38;5;83mpadding\033[m\033[38;5;198m:\033[m     \033[38;5;81m20px\033[m\033[38;5;251m;\033[m
  \033[38;5;251m}\033[m
  \033[38;5;251m/* feature support */\033[m
  \033[38;5;213m@\033[m\033[38;5;213msupports\033[m\033[38;5;184m(\033[m\033[38;5;213mcolor\033[m\033[38;5;213m:\033[m \033[38;5;213mcolor-mix\033[m\033[38;5;184m(\033[m\033[38;5;213min\033[m \033[38;5;213moklch\033[m\033[38;5;198m,\033[m \033[38;5;213mred\033[m\033[38;5;198m,\033[m \033[38;5;213mred\033[m\033[38;5;184m)\033[m\033[38;5;184m)\033[m \033[38;5;251m{\033[m
    \033[38;5;83mcolor\033[m\033[38;5;198m:\033[m \033[38;5;81mblue\033[m\033[38;5;251m;\033[m
  \033[38;5;251m}\033[m
\033[38;5;251m}\033[m")

(def example-1-ansi-tagged-output-light-monokai
  "\033[38;5;127m.foo\033[m \033[38;5;242m{\033[m
  \033[38;5;92m&\033[m\033[38;5;127m:\033[m\033[38;5;127mlink\033[m \033[38;5;242m{\033[m
    \033[38;5;28mcolor\033[m\033[38;5;160m:\033[m \033[38;5;26msilver\033[m\033[38;5;242m;\033[m
  \033[38;5;242m}\033[m
  \033[38;5;92m&\033[m\033[38;5;127m::\033[m\033[38;5;127mbefore\033[m \033[38;5;242m{\033[m
    \033[38;5;28mcontent\033[m\033[38;5;160m:\033[m \033[38;5;136m\"cssyes\"\033[m\033[38;5;242m;\033[m
  \033[38;5;242m}\033[m
  \033[38;5;242m/* media query */\033[m
  \033[38;5;127m@\033[m\033[38;5;127mmedia\033[m\033[38;5;136m(\033[m\033[38;5;127mmin-width\033[m\033[38;5;127m:\033[m \033[38;5;127m1280px\033[m\033[38;5;136m)\033[m \033[38;5;242m{\033[m
    \033[38;5;28mcolor\033[m\033[38;5;160m:\033[m       \033[38;5;92mvar\033[m\033[38;5;136m(\033[m\033[38;5;26m--my-color\033[m\033[38;5;136m)\033[m\033[38;5;242m;\033[m
    \033[38;5;28mbackground\033[m\033[38;5;160m:\033[m  \033[38;5;92mlinear-gradient\033[m\033[38;5;136m(\033[m\033[38;5;26mblack\033[m\033[38;5;160m,\033[m \033[38;5;26mwhite\033[m\033[38;5;136m)\033[m\033[38;5;242m;\033[m
    \033[38;5;28mline-height\033[m\033[38;5;160m:\033[m \033[38;5;26m1.2\033[m\033[38;5;242m;\033[m
    \033[38;5;28mpadding\033[m\033[38;5;160m:\033[m     \033[38;5;26m20px\033[m\033[38;5;242m;\033[m
  \033[38;5;242m}\033[m
  \033[38;5;242m/* feature support */\033[m
  \033[38;5;127m@\033[m\033[38;5;127msupports\033[m\033[38;5;136m(\033[m\033[38;5;127mcolor\033[m\033[38;5;127m:\033[m \033[38;5;127mcolor-mix\033[m\033[38;5;136m(\033[m\033[38;5;127min\033[m \033[38;5;127moklch\033[m\033[38;5;160m,\033[m \033[38;5;127mred\033[m\033[38;5;160m,\033[m \033[38;5;127mred\033[m\033[38;5;136m)\033[m\033[38;5;136m)\033[m \033[38;5;242m{\033[m
    \033[38;5;28mcolor\033[m\033[38;5;160m:\033[m \033[38;5;26mblue\033[m\033[38;5;242m;\033[m
  \033[38;5;242m}\033[m
\033[38;5;242m}\033[m")

(def example-1-ansi-tagged-output-dark
  ".foo \033[38;5;251m{\033[m
  \033[38;5;184m&\033[m\033[38;5;184m:\033[mlink \033[38;5;251m{\033[m
    \033[38;5;183mcolor\033[m\033[38;5;198m:\033[m \033[38;5;81msilver\033[m\033[38;5;251m;\033[m
  \033[38;5;251m}\033[m
  \033[38;5;184m&\033[m\033[38;5;184m::\033[mbefore \033[38;5;251m{\033[m
    \033[38;5;183mcontent\033[m\033[38;5;198m:\033[m \033[38;5;83m\"cssyes\"\033[m\033[38;5;251m;\033[m
  \033[38;5;251m}\033[m
  \033[38;5;198m/* media query */\033[m
  @media\033[38;5;184m(\033[mmin-width\033[38;5;184m:\033[m 1280px\033[38;5;184m)\033[m \033[38;5;251m{\033[m
    \033[38;5;183mcolor\033[m\033[38;5;198m:\033[m       \033[38;5;184mvar\033[m\033[38;5;184m(\033[m\033[38;5;214m--my-color\033[m\033[38;5;184m)\033[m\033[38;5;251m;\033[m
    \033[38;5;183mbackground\033[m\033[38;5;198m:\033[m  \033[38;5;184mlinear-gradient\033[m\033[38;5;184m(\033[m\033[38;5;81mblack\033[m\033[38;5;198m,\033[m \033[38;5;81mwhite\033[m\033[38;5;184m)\033[m\033[38;5;251m;\033[m
    \033[38;5;183mline-height\033[m\033[38;5;198m:\033[m \033[38;5;81m1.2\033[m\033[38;5;251m;\033[m
    \033[38;5;183mpadding\033[m\033[38;5;198m:\033[m     \033[38;5;81m20px\033[m\033[38;5;251m;\033[m
  \033[38;5;251m}\033[m
  \033[38;5;198m/* feature support */\033[m
  @supports\033[38;5;184m(\033[mcolor\033[38;5;184m:\033[m color-mix\033[38;5;184m(\033[min oklch\033[38;5;198m,\033[m red\033[38;5;198m,\033[m red\033[38;5;184m)\033[m\033[38;5;184m)\033[m \033[38;5;251m{\033[m
    \033[38;5;183mcolor\033[m\033[38;5;198m:\033[m \033[38;5;81mblue\033[m\033[38;5;251m;\033[m
  \033[38;5;251m}\033[m
\033[38;5;251m}\033[m")

(def example-1-ansi-tagged-output-light
  ".foo \033[38;5;242m{\033[m
  \033[38;5;136m&\033[m\033[38;5;136m:\033[mlink \033[38;5;242m{\033[m
    \033[38;5;92mcolor\033[m\033[38;5;160m:\033[m \033[38;5;26msilver\033[m\033[38;5;242m;\033[m
  \033[38;5;242m}\033[m
  \033[38;5;136m&\033[m\033[38;5;136m::\033[mbefore \033[38;5;242m{\033[m
    \033[38;5;92mcontent\033[m\033[38;5;160m:\033[m \033[38;5;28m\"cssyes\"\033[m\033[38;5;242m;\033[m
  \033[38;5;242m}\033[m
  \033[38;5;160m/* media query */\033[m
  @media\033[38;5;136m(\033[mmin-width\033[38;5;136m:\033[m 1280px\033[38;5;136m)\033[m \033[38;5;242m{\033[m
    \033[38;5;92mcolor\033[m\033[38;5;160m:\033[m       \033[38;5;136mvar\033[m\033[38;5;136m(\033[m\033[38;5;166m--my-color\033[m\033[38;5;136m)\033[m\033[38;5;242m;\033[m
    \033[38;5;92mbackground\033[m\033[38;5;160m:\033[m  \033[38;5;136mlinear-gradient\033[m\033[38;5;136m(\033[m\033[38;5;26mblack\033[m\033[38;5;160m,\033[m \033[38;5;26mwhite\033[m\033[38;5;136m)\033[m\033[38;5;242m;\033[m
    \033[38;5;92mline-height\033[m\033[38;5;160m:\033[m \033[38;5;26m1.2\033[m\033[38;5;242m;\033[m
    \033[38;5;92mpadding\033[m\033[38;5;160m:\033[m     \033[38;5;26m20px\033[m\033[38;5;242m;\033[m
  \033[38;5;242m}\033[m
  \033[38;5;160m/* feature support */\033[m
  @supports\033[38;5;136m(\033[mcolor\033[38;5;136m:\033[m color-mix\033[38;5;136m(\033[min oklch\033[38;5;160m,\033[m red\033[38;5;160m,\033[m red\033[38;5;136m)\033[m\033[38;5;136m)\033[m \033[38;5;242m{\033[m
    \033[38;5;92mcolor\033[m\033[38;5;160m:\033[m \033[38;5;26mblue\033[m\033[38;5;242m;\033[m
  \033[38;5;242m}\033[m
\033[38;5;242m}\033[m")

(deftest a-test
  (testing "example-1-input, with cssyes.core/beautify and the monokai theme, on a dark background."
    (is (= (sut/beautify example-1-input
                         {:theme          themes/monokai
                          :supports-color (mock-context "dark")})
           example-1-ansi-tagged-output-dark-monokai)))
  (testing "example-1-input, with cssyes.core/beautify and the monokai theme, on a light background."
    (is (= (sut/beautify example-1-input
                         {:theme          themes/monokai
                          :supports-color (mock-context "light")})
           example-1-ansi-tagged-output-light-monokai))))

(deftest default-theme-test
  (testing "example-1-input, with cssyes.core/beautify and no :theme, uses the alabaster theme."
    (is (= (sut/beautify example-1-input
                         {:supports-color (mock-context "dark")})
           example-1-ansi-tagged-output-dark))
    (is (= (sut/beautify example-1-input
                         {:supports-color (mock-context "light")})
           example-1-ansi-tagged-output-light))))

(deftest comments-test
  (let [opts {:theme          {:comments :red}
              :supports-color (mock-context "dark")}]
    (testing "A comment at the top level gets the :comments color."
      (is (= (sut/beautify ".a { color: red; }\n/* between */\n.b { color: red; }" opts)
             ".a {\n  color: red;\n}\n\033[38;5;198m/* between */\033[m\n.b {\n  color: red;\n}")))
    (testing "A comment before a declaration goes on its own line and gets the :comments color."
      (is (= (sut/beautify ".a { /* in block */ color: red; }" opts)
             ".a {\n  \033[38;5;198m/* in block */\033[m\n  color: red;\n}")))
    (testing "A comment after the last declaration goes on its own line and gets the :comments color."
      (is (= (sut/beautify ".a { color: red; /* after decl */ }" opts)
             ".a {\n  color: red;\n  \033[38;5;198m/* after decl */\033[m\n}")))
    (testing "A comment inside a value stays in place and gets the :comments color."
      (is (= (sut/beautify ".a { color: /* mid */ red; }" opts)
             ".a {\n  color: \033[38;5;198m/* mid */\033[m red;\n}")))
    (testing "A comment inside a selector stays in place and gets the :comments color."
      (is (= (sut/beautify ".a /* sel */ { color: red; }" opts)
             ".a \033[38;5;198m/* sel */\033[m {\n  color: red;\n}")))
    (testing "A slash that does not start a comment stays part of the value."
      (is (= (sut/beautify ".b { width: calc(1px / 2); }" opts)
             ".b {\n  width: calc(1px / 2);\n}")))))

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
