(ns ta.db.bars.random
  (:require
   [tablecloth.api :as tc]
   [ta.helper.random :refer [random-series]]
   [ta.helper.date-ds :refer [days-ago]]
   [ta.db.bars.shuffle :refer [shuffle-bar-series]]
   [ta.db.bars.protocol :as b]))

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

(defn create-random-datasets [db calendar n assets]
  (doall
   (map (fn [asset]
          (let [ds (random-dataset n)]
            (b/append-bars db {:asset asset 
                               :calendar calendar} 
                           ds)))
        assets)))

(defn create-shuffled-datasets [db-source db-shuffled calendar assets window]
  (doall
   (map (fn [asset]
          (let [opts {:asset asset
                      :calendar calendar}
                ds (b/get-bars db-source opts window)
                ds-shuffled (shuffle-bar-series ds)]
            (b/append-bars db-shuffled opts ds-shuffled)))
        assets)))

(comment
  (create-random-datasets :random ["BTCUSD" "ETHUSD"] "EOD" 10)
;  
  )






