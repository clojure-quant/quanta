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

(defn add-keltner
  "adds keltner indicator to dataset
   Band   | formula
   Middle | n-bar exponential moving average (EMA)
   Upper  | middle + (n-day atr) * k 
   Lower  | middle + (n-day atr) * k "
  [{:keys [n k pre mid?] :as opts :or {pre "keltner"
                                       mid? true}}
   bar-ds]
  (assert n "keltner misses :n parameter (typically 20)")
  (assert k "keltner misses :k parameter (typically 2.0)")
  (let [mid (ind/ema n (:close bar-ds))
        atr-vec (ind/atr {:n n} bar-ds)
        delta (dfn/* atr-vec k)]
    (add-bands mid delta delta pre mid? bar-ds)))

(defn add-bollinger
  "adds bollinger indicator to dataset
   Band   | formula
   Middle | n-bar simple moving average (SMA)
   Upper  | middle + (n-day standard deviation of price-change) * m 
   Lower  | middle + (n-day standard deviation of price-change) * m "
  [{:keys [n k pre mid?] :as opts :or {pre "bollinger"
                                       mid? true}}
   bar-ds]
  (assert n "bollinger misses :n parameter (typically 20)")
  (assert k "bollinger misses :k parameter (typically 2.0)")
  (let [mid (ind/sma {:n n} (:close bar-ds))
        delta (-> (roll/trailing-stddev n (:close bar-ds))
                  (dfn/* k))]
    (add-bands mid delta delta pre mid? bar-ds)))

(defn add-atr-band
  "adds atr band indicator to dataset
     atr-n is the number of bars for atr calc
     atr-m is the multiplyer
     Band   | formula
     Middle | prior close
     Upper  | middle + (n-day atr) * atr-m 
     Lower  | middle + (n-day atr) * atr-m "
  [{:keys [atr-n atr-m pre mid?]
    :or {pre "atr-band"
         mid? true}} bar-ds]
  (assert atr-n "atr-band needs :atr-n option")
  (assert atr-m "atr-band needs :atr-m option")
  (let [atr-vec (ind/atr {:n atr-n} bar-ds)
        delta (dfn/* atr-vec atr-m)
        mid (ind/prior (:close bar-ds))]
    (add-bands mid delta delta pre mid? bar-ds)))

(comment

  (def ds1
    (tc/dataset {:close [100.0 101.0 103.0 102.0 104.0 105.0]}))

  (add-bollinger {:n 2 :m 3.0} ds1)

  (def ds
    (tc/dataset [{:open 100 :high 120 :low 90 :close 100}
                 {:open 100 :high 120 :low 90 :close 101}
                 {:open 100 :high 140 :low 90 :close 102}
                 {:open 100 :high 140 :low 90 :close 104}
                 {:open 100 :high 140 :low 90 :close 104}
                 {:open 100 :high 160 :low 90 :close 106}
                 {:open 100 :high 160 :low 90 :close 107}
                 {:open 100 :high 160 :low 90 :close 110}]))

  (add-atr-band {:atr-n 5 :atr-m 2.0} ds)

; 
  )