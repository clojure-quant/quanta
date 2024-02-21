(ns juan.algo.pivot
  (:require
   [tech.v3.datatype.functional :as fun]
   [tablecloth.api :as tc]
   [ta.math.bin :refer [bin-full bin-middle]]
   [ta.math.percentile :as p]))

;Binned volume distribution
;Input: tick or bar. 
;       - If bar 25% of volume to open/high/low/close. 
;       - If bbo-quote then avg bid ask = mid.  If trade, trade price.
;Accumulate over timeframe. Input: time frame. And input stream
;Bin price levels. Input:bin interval.
;Auto binning mode. Useful?
;Eliminate below median.


(defn ds-col [ds-bars volume-25 col]
  (tc/dataset {:price (col ds-bars)
               :volume volume-25}))

(defn bin-middle-safe [b bin]
  (try
    (bin-middle b bin)
    (catch Exception ex
      0.0)))

(defn bars->volume-distribution 
  "input: bar-ds
   output: ds with bin stats (:bin :price :count :volume)"
  [{:keys [step]} bar-ds]
  (assert step ":step required to calculate bars->volume-distribution")
  (let [volume-25 (fun/* 0.25 (:volume bar-ds))
        open-ds (ds-col bar-ds volume-25 :open)
        high-ds (ds-col bar-ds volume-25 :high)
        low-ds (ds-col bar-ds volume-25 :low)
        close-ds (ds-col bar-ds volume-25 :close)
        all-ds (tc/concat open-ds high-ds low-ds close-ds)
        b (bin-full {:step step} (:price all-ds))
        bin (:result b)]
    (-> all-ds
        (tc/add-column :bin bin)
        (tc/group-by [:bin])
        (tc/aggregate {:price (fn [ds]
                                (let [bin (-> ds :bin tc/first first)]
                                  (bin-middle-safe b bin)
                                  ))
                       :count (fn [ds]
                                (->> ds tc/row-count))
                       :volume (fn [ds]
                                 (->> ds
                                      :volume
                                      fun/sum))}))))

  
(defn pivots [env {:keys [percentile] :as opts} ds-bars]
  (assert percentile "pivot calculation needs :percentile 0-100")
  (let [dist (bars->volume-distribution opts ds-bars)
        cutoff-volume (p/percentile percentile (:volume dist))
        pivots  (tc/select-rows dist 
                                (fn [{:keys [volume]}]
                                  (>= volume cutoff-volume)))]
    {:dist dist
     :cutoff-volume cutoff-volume
     :pivots pivots}))


;(defn avg-distance [binned-volume-distribution]
;   (-> binned-volume-distribution
;        (Sort-by :bin)
;        (Add-col :distance (chg (:bin ds)
;        (Mean :distance))