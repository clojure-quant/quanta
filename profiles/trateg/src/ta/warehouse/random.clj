(ns ta.warehouse.random
  (:require
   [tech.v3.dataset.print :as print]
   [tech.v3.dataset :as dataset]
   [tech.v3.datatype.datetime :as datetime]
   [tablecloth.api :as tablecloth]
   [ta.dataset.helper :as helper]
   [ta.dataset.date :refer [days-ago-instant]]
   [ta.warehouse :as wh]))

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
        :close (repeatedly n rand)})
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

(comment
  (let [w-random (wh/init {:series "../db/random/"
                           :list "../resources/etf/"})]
    (create-random-datasets w-random ["BTC" "ETH"] "EOD" 10))
;  
  )






