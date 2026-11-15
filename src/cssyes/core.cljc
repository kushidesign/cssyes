(ns cssyes.core 
  (:require
    [clojure.string :as str]
    [cssyes.themes :as themes]
    [sugar.core :as sugar]))

(declare beautify)

(def default-theme themes/neutral)

(defn print-css 
  "Themed printing with indentation. Returns nil."
  ([s]
   (print-css s nil))
  ([s opts]
   (println 
    (beautify s
              (merge
               {:indentation 2
                :theme       default-theme}
               opts)))))

(def color-variants
  "The values :color-variant takes. Each is a prefix of a sugar palette name,
   such as :dark in :dark-blue."
  #{:shadow :extra-dark :dark :medium :light :extra-light :highlight})

(def ^:private variant-base-colors
  "The sugar base colors that have variants. :black and :white have none."
  #{"red" "orange" "yellow" "olive" "green" "cyan" "blue" "purple" "magenta"
    "gray"})

(defn- with-color-variant
  "Returns theme with each base color prefixed by variant, so :blue becomes
  :dark-blue for the variant :dark. Other colors are unchanged. Returns theme
  unchanged when variant is not in color-variants.

  Example:
    (with-color-variant {:properties :blue :colons :black} :dark)
    => {:properties :dark-blue :colons :black}"
  [theme variant]
  (if (contains? color-variants variant)
    (reduce-kv (fn [m k color]
                 (assoc m k (if (and (or (keyword? color) (string? color))
                                     (contains? variant-base-colors (name color)))
                              (keyword (str (name variant) "-" (name color)))
                              color)))
               {}
               theme)
    theme))

(def ^:private ^:dynamic *supports-color*
  "A supports-color :context map passed to sugar.core/styled, for mocking in
   tests. nil reads the live process and terminal."
  nil)

(defn- colorize
  "Colors a string with sugar.core/styled, using the color the theme map gives
  for theme-key. A base color such as :red resolves to a lighter variant on a
  dark terminal background and a darker one on a light background. Returns the
  original string if the theme has no color for theme-key.

  Example:
    (colorize \"{\" :curly-brackets {:curly-brackets :blue})"
  [text theme-key theme]
  (if (or (empty? text) (nil? text))
    text
    (if-let [color (get theme theme-key)]
      (if *supports-color*
        (sugar/styled text {:color color} *supports-color*)
        (sugar/styled text {:color color}))
      text)))

(defn- make-indent
  "Generates a string of spaces for the given indentation level.

  Example:
    (make-indent 2 2) ;; Returns \"    \""
  [level spaces]
  (apply str (repeat (* level spaces) " ")))

(def ^:private css-token-re
  #"(?is)/\*.*?\*/|url\([^)]+\)|'(?:\\'|[^'])*'|\"(?:\\\"|[^\"])*\"|[{};()]|[^{};()'\"]+")

(def ^:private selector-token-re 
  #"::|:|,|\(|\)|\s+|[a-zA-Z0-9_.-]+|.")

(def ^:private value-token-re
  #"(?i)'(?:\\'|[^'])*'|\"(?:\\\"|[^\"])*\"|--[a-zA-Z0-9_-]+|[a-zA-Z0-9_-]+\(|#[a-zA-Z0-9]+|(?:\d*\.\d+|\d+)(?:[a-zA-Z]+|%)?|\(|\)|,|\s+|[a-zA-Z_-]+|.")


(defn- block-max-prop-length
  "Looks ahead in the token stream to find the maximum property length
  within the current CSS block, ignoring nested blocks. Used for
  left-justifying values.

  Example:
    (block-max-prop-length '(\"width\" \":\" \" 10px\" \";\" \"}\"))"
  [toks]
  (loop [ts toks
         chunk []
         max-len 0
         depth 0]
    (if (empty? ts)
      max-len
      (let [t (first ts)]
        (cond
          ;; Skip over comments without affecting block depth or length
          (str/starts-with? t "/*")
          (recur (rest ts) chunk max-len depth)

          ;; Increment depth when entering a nested scope (e.g., media queries)
          (= t "{")
          (recur (rest ts) [] max-len (inc depth))

          ;; Decrement depth, or calculate final property length if exiting
          (= t "}")
          (if (zero? depth)
            (let [full-str  (str/trim (str/join "" chunk))
                  colon-idx (str/index-of full-str ":")]
              (if colon-idx
                (max max-len (count (str/trim (subs full-str 0 colon-idx))))
                max-len))
            (recur (rest ts) [] max-len (dec depth)))

          ;; Check property length at the end of a standard declaration
          (= t ";")
          (if (zero? depth)
            (let [full-str  (str/trim (str/join "" chunk))
                  colon-idx (str/index-of full-str ":")
                  prop-str  (if colon-idx 
                              (subs full-str 0 colon-idx) 
                              "")
                  prop-len  (if colon-idx 
                              (count (str/trim prop-str)) 
                              0)]
              (recur (rest ts) [] (max max-len prop-len) depth))
            (recur (rest ts) [] max-len depth))

          ;; Accumulate standard tokens into the current chunk
          :else
          (recur (rest ts) (conj chunk t) max-len depth))))))

(defn- format-selector
  "Tokenizes and applies syntax highlighting to a CSS selector string.

  Example:
    (format-selector \".btn:hover\" {:selectors :blue :pseudo-colons :red})"
  [sel-str theme]
  (let [tokens (re-seq selector-token-re sel-str)]
    (->> tokens
         (map (fn [t]
                (cond
                  ;; Colorize pseudo-classes and pseudo-elements
                  (or (= t ":") (= t "::"))
                  (colorize t :pseudo-colons theme)

                  ;; Colorize the nesting selector
                  (= t "&")
                  (colorize t :ampersands theme)

                  ;; Colorize commas separating multiple selectors
                  (= t ",")
                  (colorize t :commas theme)

                  ;; Colorize round brackets in structural pseudo-classes
                  (or (= t "(") (= t ")"))
                  (colorize t :round-brackets theme)

                  ;; Preserve whitespace exactly as authored
                  (re-matches #"\s+" t)
                  t

                  ;; Treat all other text as standard selector text
                  :else
                  (colorize t :selectors theme))))
         (str/join ""))))

(defn- format-value-aligned
  "Tokenizes a CSS value string and applies syntax highlighting.
  Also manages parenthesis depth to correctly align multi-layered values
  on newlines without breaking internal function arguments.

  Example:
    (format-value-aligned \"calc(2px + 3px)\" my-theme 12)"
  [val-str theme align-spaces]
  (let [tokens    (re-seq value-token-re val-str)
        align-str (apply str (repeat align-spaces " "))]
    (loop [toks        tokens
           depth       0
           skip-space? true
           out         []]
      (if (empty? toks)
        (str/join "" out)
        (let [t         (first toks)
              next-toks (rest toks)]
          (cond
            ;; Handle spaces: drop them if they follow an alignment newline
            (re-matches #"\s+" t)
            (if skip-space?
              (recur next-toks depth skip-space? out)
              (recur next-toks depth false (conj out t)))

            ;; Colorize quoted strings
            (re-matches #"(?s)^(?:'.*'|\".*\")$" t)
            (recur next-toks
                   depth
                   false
                   (conj out (colorize t :strings theme)))

            ;; Catch CSS functions (e.g., calc(, min(, linear-gradient()
            (str/ends-with? t "(")
            (let [fn-name (subs t 0 (dec (count t)))]
              (recur next-toks
                     (inc depth)
                     false
                     (conj out
                           (colorize fn-name :cssfn theme)
                           (colorize "(" :round-brackets theme))))

            ;; Catch standalone opening brackets
            (= t "(")
            (recur next-toks
                   (inc depth)
                   false
                   (conj out (colorize t :round-brackets theme)))

            ;; Catch standalone closing brackets and reduce depth
            (= t ")")
            (recur next-toks
                   (max 0 (dec depth))
                   false
                   (conj out (colorize t :round-brackets theme)))

            ;; Top-level commas trigger a newline; nested commas do not
            (= t ",")
            (let [colored-comma (colorize t :commas theme)]
              (if (zero? depth)
                (recur next-toks
                       depth
                       true
                       (conj out colored-comma "\n" align-str))
                (recur next-toks
                       depth
                       false
                       (conj out colored-comma))))

            ;; Colorize the argument side of CSS variables
            (str/starts-with? t "--")
            (recur next-toks
                   depth
                   false
                   (conj out (colorize t :vars theme)))

            ;; Colorize numerical values including percentages and units
            (re-matches #"(?i)^(?:\d*\.\d+|\d+)(?:[a-zA-Z]+|%)?$" t)
            (recur next-toks
                   depth
                   false
                   (conj out (colorize t :numbers theme)))

            ;; Default to standard value styling for text and hex codes
            :else
            (recur next-toks
                   depth
                   false
                   (conj out (colorize t :values theme)))))))))

(defn- process-declaration
  "Formats a single CSS property-value declaration. Applies padding
  to the property side so that all values in the block align vertically.

  Example:
    (process-declaration [\"width\" \":\" \" 10px\"] 1 2 theme 15)"
  [chunk indent indent-spaces theme prop-align]
  (let [full-str  (str/trim (str/join "" chunk))
        colon-idx (str/index-of full-str ":")]
    (if (and colon-idx (not (empty? full-str)))
      (let [prop               (str/trim (subs full-str 0 colon-idx))
            val-part           (subs full-str (inc colon-idx))
            indent-str         (make-indent indent indent-spaces)
            effective-prop-len (max prop-align (count prop))
            pad-len            (- effective-prop-len (count prop))
            padding            (apply str (repeat pad-len " "))
            align-spaces       (+ (* indent indent-spaces)
                                  effective-prop-len
                                  2)]
        (str indent-str
             (colorize prop :properties theme)
             (colorize ":" :colons theme)
             padding
             " "
             (format-value-aligned val-part theme align-spaces)))
      
      ;; Handle blocks missing a colon (e.g., nested selectors or media queries)
      (when-not (empty? full-str)
        (str (make-indent indent indent-spaces)
             (format-selector full-str theme))))))

;; TODO - respect no-color
(defn beautify
  "Formats and syntax highlights a valid CSS string for Clojure/Babashka.
  Maintains exact alignment for layered values and left-justifies properties.

  Options map supports:
    :indentation   - Number of spaces for each level (default: 2)
    :align-values? - Left-justify values across the block (default: true)
    :theme         - Map of syntax elements to color keywords
                     (e.g., :properties :blue, :numbers :orange).
                     Presets live in cssyes.themes
                     (e.g., cssyes.themes/neutral)
    :color-variant - One of cssyes.core/color-variants, such as :extra-dark.
                     Prefixes each base color in the theme, so :blue becomes
                     :extra-dark-blue, in place of the variant sugar picks
                     for the terminal's background.
    :supports-color - A supports-color :context map passed to
                      sugar.core/styled, for mocking in tests

  Example:
    (beautify-css \".btn { color: red; }\" {:indentation 2})"
  [css-string & [opts]]
  (binding [*supports-color* (:supports-color opts)]
    (let [ret (let [indent-spaces (:indentation opts 2)
                    theme         (with-color-variant
                                   (get opts :theme default-theme)
                                   (:color-variant opts))
                    align-vals?   (:align-values? opts true)
                    tokens        (re-seq css-token-re css-string)]
                (loop [toks        tokens
                       indent      0
                       chunk       []
                       out         []
                       align-stack [0]]
                  (if (empty? toks)
                    (str (str/trim (str/join "" out)) "\n")
                    (let [t         (first toks)
                          next-toks (rest toks)]
                      (cond
                        ;; Format floating comments and maintain indentation levels
                        (str/starts-with? t "/*")
                        (recur next-toks
                               indent
                               []
                               (conj out
                                     (make-indent indent indent-spaces)
                                     (colorize t :comments theme)
                                     "\n")
                               align-stack)

                        ;; Enter a new block: format the selector and scan for max prop len
                        (= t "{")
                        (let [selector   (str/trim (str/join "" chunk))
                              next-align (if align-vals?
                                           (block-max-prop-length next-toks)
                                           0)]
                          (recur next-toks
                                 (inc indent)
                                 []
                                 (conj out
                                       (make-indent indent indent-spaces)
                                       (format-selector selector theme)
                                       " "
                                       (colorize "{" :curly-brackets theme)
                                       "\n")
                                 (conj align-stack next-align)))

                        ;; Exit a block: process remaining declarations and decrease indent
                        (= t "}")
                        (let [decl       (process-declaration chunk
                                                              indent
                                                              indent-spaces
                                                              theme
                                                              (peek align-stack))
                              new-indent (max 0 (dec indent))
                              new-stack  (if (next align-stack)
                                           (pop align-stack)
                                           [0])]
                          (recur next-toks
                                 new-indent
                                 []
                                 (conj out
                                       (if decl (str decl "\n") "")
                                       (make-indent new-indent indent-spaces)
                                       (colorize "}" :curly-brackets theme)
                                       "\n")
                                 new-stack))

                        ;; End of declaration: process property and value string
                        (= t ";")
                        (let [decl (process-declaration chunk
                                                        indent
                                                        indent-spaces
                                                        theme
                                                        (peek align-stack))]
                          (recur next-toks
                                 indent
                                 []
                                 (conj out
                                       (if decl
                                         (str decl (colorize ";" :semi-colons theme) "\n")
                                         ""))
                                 align-stack))

                        ;; Accumulate text into the chunk until a structural token is hit
                        :else
                        (recur next-toks
                               indent
                               (conj chunk t)
                               out
                               align-stack))))))
          ret (str/replace ret #"\n$" "")]
      ret)))
