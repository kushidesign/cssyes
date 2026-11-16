(ns cssyes.themes)

;; -----------------------------------------------------------------------------
;; # Themes
;; -----------------------------------------------------------------------------

;; Theme values are sugar colors. sugar.core/styled resolves a base color such
;; as :red to a lighter variant on a dark terminal background, a darker one on a
;; light background, and a medium one when the background is not detected.

(def ^:cssyes.visual/samples zenburn 
  {:selectors      :olive
   :curly-brackets :gray
   :round-brackets :yellow
   :semi-colons    :gray
   :properties     :orange
   :values         :gray
   :strings        :orange
   :vars           :orange
   :commas         :orange
   :colons         :orange
   :numbers        :blue
   :cssfn          :yellow
   :ampersands     :olive
   :pseudo-colons  :olive
   :comments       :purple})

(def ^:cssyes.visual/samples alabaster 
  {:selectors      :neutral
   :curly-brackets :gray
   :round-brackets :yellow
   :semi-colons    :gray
   :properties     :purple
   :values         :blue
   :strings        :green
   :vars           :orange
   :commas         :red
   :colons         :red
   :numbers        :blue
   :cssfn          :yellow
   :ampersands     :yellow
   :pseudo-colons  :yellow
   :comments       :red})

(def ^:cssyes.visual/samples monokai
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

(def ^:cssyes.visual/samples neutral
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
