(ns demo.env.reval
  (:require
   [reval.persist.edn :refer [pprint-str]]
   [reval.type.protocol :refer [hiccup-convertable to-hiccup]]))

(defn text-render-fipp
  [o comment]
  [:span.text-green-500 
   comment
   [:p/text
    (pprint-str o)]])

(defn text-render
  [o comment]
  [:span.text-blue-500
   comment
   [:p/text
    (pr-str o)]])

;; clojure

(extend-type clojure.lang.PersistentVector
  hiccup-convertable
  (to-hiccup [self]
    (text-render-fipp self "persistent-vector")))

(extend-type clojure.lang.LazySeq
  hiccup-convertable
  (to-hiccup [self]
    (text-render-fipp self "lazy-seq")))

(extend-type clojure.lang.PersistentArrayMap
  hiccup-convertable
  (to-hiccup [self]
    (text-render-fipp self "persistent-array-map")))

(extend-type clojure.lang.PersistentHashMap
  hiccup-convertable
  (to-hiccup [self]
    (text-render-fipp self "persistent-hash-map")))

(extend-type java.time.Instant
  hiccup-convertable
  (to-hiccup [self]
    (text-render-fipp self "time-instant")))

;; techml

(extend-type tech.v3.dataset.impl.column.Column
  hiccup-convertable
  (to-hiccup [self]
    (text-render self "techml column!")))

(extend-type tech.v3.dataset.impl.dataset.Dataset
  hiccup-convertable
  (to-hiccup [self]
    (text-render self "techml dataset ")))