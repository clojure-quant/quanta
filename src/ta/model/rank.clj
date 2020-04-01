(ns ta.model.rank
  (:require
   [ta.model.trade :refer [get-ts set-ts map-symbols]]))

(defn rank-input [model field-in index]
  ;(println "rank input" index)
  (map-symbols
   (fn [[symbol data-symbol]]
     ;(println "rank-input " symbol index)
     (let [field-val (get-ts model symbol field-in index)
           ;_ (println [symbol field-val])
           ]
       [symbol field-val]))
   model))

(defn to-map [ranked]
  (let [m (atom {})]
    (doall (map (fn [[key data]]
           ;(println "key: " key " data: " data)
                  (swap! m assoc key data))
                ranked))
    @m))

(defn rank-calc [data field-in ascending? index]
  (let [ranks (rank-input data field-in index)
        ranks-sorted (sort-by #(get % 1) ranks)
        ranks-sorted (if ascending? ranks-sorted (reverse ranks-sorted))
        ;_ (println "sorted: " ranks-sorted)
        ranked (map-indexed
                (fn [idx itm] [(get itm 0) [(get itm 1) (+ idx 1)]])
                ranks-sorted)
        ;_ (println "ranked: " ranked)
        ]
    (to-map ranked)))
;; => #'tradeg.crosssection/rank-calc

(defn rank-series-for-key [rank-list symbol]
  (let [extract-array (fn [ranking] (symbol ranking))
        extract-rank (fn [ranking] (get (symbol ranking) 1))]
    (into []
          (map-indexed
           (fn [idx itm] (extract-rank itm))
           rank-list))))

(defn rank [model length field-in ascending? field-out]
  (let [;_ (println "rank-list calc indices: " length)
        rank-list (map (fn [index]
                         (rank-calc model field-in ascending? index))
                       (range length))]
      ;(to-map 
    (doall
     (map-symbols
      (fn [[symbol d]]
               ;(println symbol "data:" d)
        (let [rank-ts (rank-series-for-key rank-list symbol)]
          (set-ts model symbol field-out rank-ts)
             ;[symbol (assoc d field-out rank-ts)]
          ))
      model))
    @model));)

(comment

  (def data-
    (atom {:symbols
           {:a {:price [100 105 110]}
            :b {:price [105 110 109]}
            :c {:price [110 104 103]}}}))

  (rank-input data- :price 1)
  (rank-calc data- :price false 1)

  (to-map [[:b [110 1]] [:a [105 2]] [:c [104 3]]])

  (rank-series-for-key
   [{:c [110 1], :b [105 2], :a [100 3]}
    {:b [110 1], :a [105 2], :c [104 3]}
    {:a [110 1], :b [109 2], :c [103 3]}]
   :a))
