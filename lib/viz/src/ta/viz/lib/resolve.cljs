(ns ta.viz.lib.resolve
  (:require
   [promesa.core :as p]
   [webly.spa.resolve :refer [get-resolver]]))

; replace symbols with functions
; functions are resolved via requiring-resolve which returns a promesa promise

(defn resolve-symbols
  "input: seq of symbol
   output: a promise, when resolved: map keys:symbol values:fn"
  [symbols]
  ;(println "resolve-symbols: " (pr-str symbols))
  (let [symbols (->> symbols (into #{}) (into [])) ; in case some symbols are duplicates
        requiring-resolve (get-resolver)
        promises (map requiring-resolve symbols)
        all (p/all promises)
        result (p/deferred)]
    (p/then all
            (fn [ps]
              (println "resolve-symbols: successfully resolved all symbols!")
              ;(println "symbols: " (pr-str symbols))
              ;(println "ps: " (pr-str ps))
              (let [tuplets (map (fn [s f]
                                   ;(println "tuplet: " (pr-str [s f]))
                                   [s f]) symbols ps)
                    d (into {} tuplets)]
                ;(println "tuplets: " (pr-str tuplets))
                ;(println "d: " (pr-str d))
                (p/resolve! result d))))
    (p/catch all (fn [e]
                   (println "resolve-symbols: error in resolving all symbols!")
                   (println "resolve-symbols error: " e)
                   (p/reject! result e)))
    result))

(defn symbols-for-col [cols col-key]
  (->> cols
       (map col-key)
       (filter symbol?)))

(defn symbols-for-cols [cols col-keys]
  (let [symbol-lists (map #(symbols-for-col cols %) col-keys)]
    (reduce concat [] symbol-lists)))

(defn update-symbol-col [d m k]
  (let [v (k m)]
    (if (symbol? v)
      (update m k #(get d %))
      m)))

(defn update-col [d m k]
  (let [s (k m)]
    (if (symbol? s)
      (if-let [f (get d s)]
        (assoc {} k f)
        (do (println "could not resolve column: " k "symbol: " s)
            {}))
      {})))

(defn update-symbol-cols [d m ks]
  (let [col-prior m
        changes (map #(update-col d m %) ks)
        col-after (reduce merge m changes)]
    ;(println "prior: " col-prior)
    ;(println "after: " col-after)
    col-after))

(defn resolve-col [cols col-keys]
  (println "resolving columns:" col-keys "for " (count cols) "columns ...")
  (let [symbols (symbols-for-cols cols col-keys)
        p-r (resolve-symbols symbols)
        result (p/deferred)]
    (p/then p-r (fn [d]
                  (println "all columns have been resolved successfully!")
                  ;(println "dict: " (pr-str d))
                  (->>
                   (map #(update-symbol-cols d % col-keys) cols)
                   (p/resolve! result))))
    (p/catch p-r (fn [e]
                   (println "Error in resolving all columns!")
                   (println "resolve-col error: " e)
                   (p/reject! result e)))
    result))

(defn resolve-cols
  "resolves {:format :render-cell} in all columns
   returns a promise"
  [cols]
  (resolve-col cols [:format :render-cell :attrs]))