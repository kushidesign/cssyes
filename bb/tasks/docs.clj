(ns tasks.docs
  "Helpers for the docs:update task in bb.edn."
  (:require [babashka.fs :as fs]
            [babashka.process :as p]
            [clojure.string :as str]))

(defn- sh
  "Runs a command and returns its trimmed stdout. Does not throw on failure."
  [& args]
  (-> (apply p/shell {:out :string :err :string :continue true} args)
      :out
      str/trim))

(defn- version-key
  "Sort key for a tag like v0.10.1: a vector of its numbers, or nil if the tag
   is not a plain vN.N.N release."
  [tag]
  (when-let [[_ & nums] (re-find #"^v?(\d+)\.(\d+)\.(\d+)$" tag)]
    (mapv parse-long nums)))

(defn- latest-release
  "The highest vN.N.N tag on the origin remote, as {:tag \"v0.3.0\" :sha \"...\"},
   or nil if the remote has none. Asks the remote with `git ls-remote`, so tags
   that were never fetched locally count. For an annotated tag the sha is the
   commit it points at (the peeled ^{} entry)."
  []
  (let [shas (->> (str/split-lines (sh "git ls-remote --tags origin"))
                  (keep #(re-find #"^(\w{40})\s+refs/tags/(\S+?)(\^\{\})?$" %))
                  (reduce (fn [m [_ sha tag peeled]]
                            (if (or peeled (not (contains? m tag)))
                              (assoc m tag sha)
                              m))
                          {}))]
    (when-let [tag (->> (keys shas)
                        (filter version-key)
                        (sort-by version-key)
                        last)]
      {:tag tag :sha (get shas tag)})))

(defn- lib-name
  "The lib symbol from `(def lib '...)` in build.clj, as a string."
  []
  (second (re-find #"[(]def lib '(\S+)[)]" (slurp "build.clj"))))

(defn- replace-in-file
  "Applies each [pattern replacement] pair to the file at path. Returns true if
   the contents changed."
  [path pairs]
  (let [before (slurp path)
        after  (reduce (fn [s [re rep]] (str/replace s re rep)) before pairs)]
    (when (not= before after) (spit path after))
    (not= before after)))

(defn update-docs
  "Updates README.md, and doc/git-deps.md if it exists, to the latest release
   tag on the origin remote (v0.2.0 -> 0.2.0 for Maven versions). Searches each
   file for these patterns and rewrites the value in place:

     <lib> {:mvn/version \"...\"}
     [<lib> \"...\"]
     :git/tag \"...\"
     :git/sha \"...\"

   Leaves the files alone if the remote has no release tags."
  []
  (if-let [{:keys [tag sha]} (latest-release)]
    (let [version (str/replace tag #"^v" "")
          q       (java.util.regex.Pattern/quote (lib-name))
          pairs   [[(re-pattern (str "(" q " [{]:mvn/version \")[^\"]+(\"[}])")) (str "$1" version "$2")]
                   [(re-pattern (str "([\\[]" q " \")[^\"]+(\"[\\]])")) (str "$1" version "$2")]
                   [#"(:git/tag\s+\")[^\"]+(\")" (str "$1" tag "$2")]
                   [#"(:git/sha\s+\")[^\"]+(\")" (str "$1" sha "$2")]]]
      (println "Latest release on origin:" tag sha)
      (doseq [path ["README.md" "doc/git-deps.md"]
              :when (fs/exists? path)]
        (println (if (replace-in-file path pairs) "Updated  " "Unchanged") path)))
    (println "No release tags on origin, so the docs are unchanged. Push a tag, then rerun.")))
