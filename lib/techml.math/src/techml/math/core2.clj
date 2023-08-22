(ns techml.math.core2
  ;(:refer-clojure :exclude [+ -])
  (:require
    [tech.v3.datatype.functional :as fun]
    [tablecloth.api :as tc]))

(defn col-names [d]
  (->> d tc/columns (map meta) (map :name)))

(defn col-bindings [d]
  (println "col-bindings calc for: " d)
  (let [cols-kw (col-names d)
        binding-tuple (juxt symbol #(get d %))]
   (->> (map binding-tuple cols-kw)
        (apply concat)
        (into []))))


(defn map-bindings [d]
  (println "col-bindings calc for: " d)
  (let [cols-kw (keys d)
        binding-tuple (juxt symbol #(get d %))]
   (->> (map binding-tuple cols-kw)
        (apply concat)
        (into []))))

(map-bindings {:a 1 :b 2})


(defn col-bindings-map [d]
   (->> d col-bindings (partition 2) (map vec) (into {}))
  )

(->> [:a 1 :b 2]
     (partition 2)
     (map vec)
     (into {})
     )


(col-bindings-map d)

(defn safe [d]
  (cond (var? d) (var-get d)
        (symbol? d) (-> d resolve var-get)
        :else d))

(defmacro with-map-bindings [d expr]
 `(let [~@(map-bindings (safe d))]
    ~expr))


(defmacro with-ds-bindings [d expr]
  `(let [~@(col-bindings (safe d))]
        ~expr
       ))


  (require '[tech.v3.dataset :as tds])
  (def d (tds/->dataset {:a [1.0 2.0 3.0 4.0 5.0]
                         :b [1.0 2.0 3.0 4.0 5.0]
                         :c [1.0 2.0 3.0 4.0 100.0]}))

  (with-map-bindings {:a 1 :b 2} (+ a b 100))
  (def m {:a 1 :b 2})
  (with-map-bindings m (+ a b 100))

  (with-ds-bindings d (println a))


   (let [+ fun/+
         - fun/-
             ]
     (let )
     (+ (:a d) (:b d)))
