# Consuming {{PROJECT}} as a git dependency

This doc explains, by example, all the possible variations of the coordinate map
you would use to specify {{PROJECT}} as a git dependency.

`:git/url` is required. The library is named `{{LIB}}`, so the git URL cannot be inferred.

`:git/sha` is always required.

`:git/tag` is an optional readable label.

<br>

```clojure
;; deps.edn

{:deps {{{LIB}}
        {:git/url {{URL}}
         :git/tag {{TAG}}
         :git/sha {{SHA}}}}}
```
<br>

The coordinate map in the example above can take several forms, outlined in the sections below.

<br>

## URL + tag + SHA (recommended)

The `:git/tag` entry documents which release the SHA corresponds to.

```clojure
{{LIB}}
{:git/url {{URL}}
 :git/tag {{TAG}}
 :git/sha {{SHA}}}
```

<br>

## URL + SHA

```clojure
{{LIB}}
{:git/url {{URL}}
 :git/sha {{SHA}}}
```

<br>

## Resolving the SHA from a tag

If you'd rather not copy the SHA by hand, write the dependency with just
the `:git/url` and `:git/tag` entries:

```clojure
{{LIB}}
{:git/url {{URL}}
 :git/tag {{TAG}}}
```

then run:

```bash
clojure -X:deps git-resolve-tags
```

This scans your `deps.edn` for git deps that have a `:git/tag` but no
`:git/sha`, resolves each tag to its commit SHA, and writes the SHA back
into the file in place. It needs a `:git/tag` to resolve from. It will not help
the SHA-only form.
