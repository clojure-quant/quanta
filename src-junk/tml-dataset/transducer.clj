(ns ta.dataset.transducer
  (:require
   [clojure.core.reducers :as r]
   [tech.v3.dataset :as ds]))

(defprotocol ITransposable
  (-row-major    [obj])
  (-column-major [obj]))

(deftype row-view [ds rows]
  clojure.lang.IPersistentCollection
  (cons [this r]
    (row-view. ds (reduce-kv (fn [acc k v]
                               (assoc acc k (conj (rows k []) v)))
                             rows r)))
  (empty [this] (row-view. (empty ds) {}))
  ITransposable
  (-column-major [this]
    (ds/->dataset rows))
  (-row-major [this] this)
  clojure.lang.Seqable
  (seq [this] (concat (ds/mapseq-reader ds)
                      (ds/mapseq-reader (ds/->dataset rows)))))

(extend-protocol
 clojure.core.protocols/CollReduce
  row-view
  (coll-reduce [coll f]
    (reduce f (r/cat (ds/mapseq-reader (.ds coll))
                     (ds/mapseq-reader (ds/->dataset (.rows coll))))))
  (coll-reduce [coll f init]
    (reduce f init (r/cat (ds/mapseq-reader (.ds coll))
                          (ds/mapseq-reader (ds/->dataset (.rows coll)))))))

(extend-protocol
 ITransposable
  tech.v3.dataset.impl.dataset.Dataset
  (-row-major [this] (row-view. this {}))
  (-column-major [this] this)
  nil
  (-row-major [this] (row-view. (ds/->dataset {}) {}))
  (-column-major [this] (ds/->dataset {}))
  clojure.lang.PersistentArrayMap
  (-row-major [this] (row-view. (ds/->dataset {}) {}))
  (-column-major [this] (ds/->dataset {})))

(defn row-major [coll]
  (if (extends? ITransposable (type coll))
    (-row-major coll)
    (if (seq coll)
      coll
      (throw (ex-info "under construction!" {})))))

(defn column-major [coll]
  (if (extends? ITransposable (type coll))
    (-column-major coll)
    (if (seq coll)
      coll
      (throw (ex-info "under construction!" {})))))

(defn into-dataset
  ([to] (column-major to))
  ([to from]
   (->> from
        row-major
        (into (row-major to))
        column-major))
  ([to xform from]
   (->> from
        row-major
        (into (row-major to) xform)
        column-major)))

(def +empty-records+ (row-major (ds/->dataset {})))

(comment

  (def d (ds/->dataset {:a [1 2 3]
                        :b [:foo :bar :baz]}))

;;long way...
  (->> (row-major d)
       (transduce (map (fn [{:keys [a] :as r}]
                         (assoc r :c (* a 3))))
                  conj
                  (empty (row-major d)))
       (column-major))

  ; with transducer

  (def xf (map (fn [{:keys [a] :as r}]
                 (assoc r :c (* a 3)))))

  (->> d
       row-major
       (into +empty-records+ xf)
       column-major)

; shrot transducer style

  (into-dataset {} xf  d)

;

  (->> (row-major (tablecloth.api/dataset  [{:a 1 :b 6}
                                            {:a 1 :b 6}
                                            {:a 1 :b 6}
                                            {:a 1 :b 6}]))
     ;;semantically equivalent to ds/mapseq-reader here
       (filter #(= (:b %) :baz)) ; filter evemts
       (map (fn [r] (assoc r :c "some-value")))
       (into-dataset {}))

;  
  )







