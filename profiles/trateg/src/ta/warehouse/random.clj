(ns ta.warehouse.random
  (:require
   [tech.v3.dataset.print :as print]
   [tech.v3.dataset :as dataset]
   [tech.v3.datatype.datetime :as datetime]
   [tablecloth.api :as tablecloth]
   [ta.helper.print :as helper]
   [ ta.backtest.date :refer [days-ago-instant]]
   [ta.helper.random :refer [random-series]]
   [ta.warehouse :as wh]
   [ta.warehouse.shuffle :refer [shuffle-bar-series]]
   ))

(defn add-open-high-low-volume [ds]
  (let [c (:close ds)]
    (tablecloth/add-columns ds
                            {:open c
                             :high c
                             :low c
                             :volume 0})))

(defn random-dataset [n]
  (-> (tablecloth/dataset
       {:date (->> (range n)
                   (map days-ago-instant)
                   reverse)
        :close (random-series n)})
      add-open-high-low-volume))

(comment
  (random-dataset 10)
;
  )

(defn random-datasets [m n]
  (repeatedly m #(random-dataset n)))

(comment
  (random-datasets 2 10)
  (first (random-datasets 2 10))
;
  )

(defn create-random-datasets [w symbols frequency n]
  (doall
   (map (fn [s]
          (let [ds (random-dataset n)]
            (wh/save-symbol w ds frequency s)))
        symbols)))

(defn create-shuffled-datasets [w-source w-shuffled symbols frequency]
  (doall
   (map (fn [s]
          (let [ds (wh/load-symbol w-source frequency s)
                ds-shuffled (shuffle-bar-series ds)
                ]
            (wh/save-symbol w-shuffled ds-shuffled frequency s)))
        symbols)))


(comment
  (let [w-random (wh/init {:series "../db/random/"
                           :list "../resources/etf/"})]
    ; do not run this directly. instead use demo.warehouse.create-random/task-create-random
    (create-random-datasets w-random ["BTCUSD" "ETHUSD"] "EOD" 10))
;  
  )






