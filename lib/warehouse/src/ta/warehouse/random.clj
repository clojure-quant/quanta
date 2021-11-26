(ns ta.warehouse.random
  (:require
   [tablecloth.api :as tc]
   [ta.helper.random :refer [random-series]]
   [ta.helper.date-ds :refer [days-ago]]
   [ta.warehouse :as wh]
   [ta.warehouse.shuffle :refer [shuffle-bar-series]]))

(defn add-open-high-low-volume [ds]
  (let [c (:close ds)]
    (tc/add-columns ds
                    {:open c
                     :high c
                     :low c
                     :volume 0})))

(defn random-dataset [n]
  (-> (tc/dataset
       {:date (->> (range n)
                   (map days-ago)
                   reverse)
        :close (random-series n)})
      add-open-high-low-volume))

(comment
  (random-dataset 10)
;
  )

(defn random-datasets [m n]
  (->> (repeatedly m #(random-dataset n))
       (map-indexed (fn [idx ds]
                      (tc/add-column ds :symbol idx)))))

(comment
  (random-datasets 2 10)
  (last (random-datasets 2 10))
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
                ds-shuffled (shuffle-bar-series ds)]
            (wh/save-symbol w-shuffled ds-shuffled frequency s)))
        symbols)))

(comment
  (create-random-datasets :random ["BTCUSD" "ETHUSD"] "EOD" 10)
;  
  )






