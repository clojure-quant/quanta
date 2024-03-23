(ns ta.indicator.band
  (:require
   [tablecloth.api :as tc]
   [tech.v3.datatype.functional :as dfn]
   [ta.indicator :as ind]
   [ta.indicator.rolling :as roll]))

(defn add-bands
  "helper function to add upper/lower (+ optionally mid) band to dataset.
   mid: vector of mid price
   delta: amount that gets added/subtracted from mid
   base-name: start-string of added columns
   mid?: boolean weather to add mid band"
  [mid delta-up delta-down base-name mid? ds]
  (let [col-mid (->> "-mid" (str base-name) keyword)
        col-upper (->> "-upper" (str base-name) keyword)
        col-lower (->> "-lower" (str base-name) keyword)
        ds (if mid?
             (tc/add-column ds col-mid mid)
             ds)]
    (tc/add-columns ds {col-upper (dfn/+ mid delta-up)
                        col-lower (dfn/- mid delta-down)})))

(defn add-bollinger
  "adds bollinger indicator to dataset
   Band   | formula
   Middle | n-bar simple moving average (SMA)
   Upper  | middle + (n-day standard deviation of price-change) * m 
   Lower  | middle + (n-day standard deviation of price-change) * m "
  [{:keys [n m pre mid?] :as opts :or {pre "bollinger"
                                       mid? true
                                       n 20
                                       m 2.0}}
   bar-ds]
  (let [mid (ind/sma n (:close bar-ds))
        delta (-> (roll/trailing-return-stddev n bar-ds)
                  (dfn/* m))]
    (add-bands mid delta delta pre mid? bar-ds)))

(comment

  (def ds
    (tc/dataset {:close [100.0 101.0 103.0 102.0 104.0 105.0]}))

  (add-bollinger {:n 2 :m 3.0} ds)

; 
  )