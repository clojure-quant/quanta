(ns notebook.algo.poc
  (:require
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as fun]
   [tablecloth.api :as tc]
   [ta.helper.returns :refer [diff]]
   [ta.helper.bin :as b]
   [ta.calendar.core :as c]
   [ta.env.algo.core :refer [get-bars-lower-timeframe]]))

; port of tradingview-indicator
;https://www.tradingview.com/script/nY63MyD9-Time-volume-point-of-control-quantifytools/

; 20240215 awb99
; Multi-calendar
; for each bar, all bars of a higher frequency are loaded
; the higher-frequency-bars get grouped into 10 price-groups (divided equally over the range, binned mathematically); 
; bar-volume gets aggregated over the bars highest volume bar is taken. Very similar to a bar-compressor.
; Lower frequency bars imports for each bar the price of biggest volume.
; Based on this 8 alerts are calculated (alert is Boolean column).

(defn dyn-tf
  "input: calendar [:us :d]
   output: time-interval (example: :h) that is suggested as a basis for the poc calculation"
  [cal]
  (let [tf-m (/ (c/get-bar-duration cal) 60)]
    (cond
      (<= tf-m 30) :tf1
      (<= tf-m 180) :tf2
      (<= tf-m 480) :tf3
      (<= tf-m 1440) :tf4
      (<= tf-m 4320) :tf5
      :else :tf6)))

(defn up? [change]
  (>= change 0.0))

(defn up-volume [volume up?]
  (if up? volume 0.0))

(defn down-volume [volume up?]
  (if up? 0.0 volume))

(defn calc-poc-cols [ds-bars bin-n]
  (let [volume (:volume ds-bars)
        change (diff (:close ds-bars))
        up? (dtype/emap up? :bool change)
        bin (b/bin-full bin-n (:close ds-bars))]
   {:bin bin
    :ds-bin (tc/dataset
             {:change change
              :bin (b/bin-result bin)
              :up? up?
              :volume-up (dtype/emap up-volume :double volume up?)
              :volume-down (dtype/emap down-volume :double volume up?)})}))

(defn price-with-most-time [{:keys [ds-bin bin]}]
  (let [bin-highest-count (-> ds-bin
                              (tc/group-by :bin)
                              (tc/aggregate {:count tc/row-count
                                             :sum #(fun/sum (:volume %))})
                              (tc/order-by [:count] [:asc])
                              tc/first
                              :bin)]
    (b/bin-middle bin bin-highest-count)))



(defn price-with-highest-volume [{:keys [ds-bin _bin]}]
  (-> ds-bin
      (tc/order-by [:volume] [:desc])
      tc/first
      :close))

(defn price-with-highest-up-volume [{:keys [ds-bin _bin]}]
  (-> ds-bin
      (tc/order-by [:volume-up] [:desc])
      tc/first
      :close))

(defn price-with-highest-down-volume [{:keys [ds-bin _bin]}]
  (-> ds-bin
      (tc/order-by [:volume-down] [:desc])
      tc/first
      :close))

(defn poc-prices 
  "returns a map with differently calculated poc prices.
   input: ds-bars (bars of the higher frequency)
          bin-nr (number of bins for time-poc calculation)"
  [ds-bars bin-n]
  (let [poc (calc-poc-cols ds-bars bin-n)]
    {:time (price-with-most-time poc)
     :volume (price-with-highest-volume poc)
     :up-volume (price-with-highest-up-volume poc)
     :down-volume (price-with-highest-down-volume poc)}))

; Ensuring sufficient distance between close and TPOC/VPOC, equal to or greater than half of bar range.

(defn anomalies
  "returns a map with anomalies (keys anomaly-type, value true/false)
   idea is to use it for alert generation."
  [poc close low hl2 low-1 high-1]
  (let [volume-spread (abs (- close (:volume poc)))
        volume-spread-big? (>= volume-spread (- hl2 low))
        time-spread (abs (- close (:time poc)))
        time-spread-big? (>= volume-spread (- hl2 low))]
    {:trapped-volume-down (or (and (> close low-1)
                                   (< (:volume poc) low-1))
                              (and (< close low-1)
                                   (< (:volume poc) close)
                                   volume-spread-big?))
     :trapped-volume-up (or  (and (< close high-1)
                                  (> (:volume poc) high-1))
                             (and (> close high-1)
                                  (> (:volume poc) close)
                                  volume-spread-big?))
     :trapped-time-down (or (and (> close low-1)
                                 (< (:time poc) low-1))
                            (and (< close low-1)
                                 (< (:time poc) close)
                                 time-spread-big?))
     :trapped-time-up (or (and (< close high-1)
                               (> (:time poc) high-1))
                          (and (> close high-1)
                               (> (:time poc) close)
                               time-spread-big?))}))


(defn indicator-poc [env {:keys [lower-timeframe bin-n] :as spec} ds-bars]
  (let [ds-bars-ltf (get-bars-lower-timeframe env spec lower-timeframe)]
    (poc-prices ds-bars-ltf bin-n)))



(def default-options
  {:vpoc-tpoc {:input-source :close
               :tpoc-threshold 50 ; [1 100] With 50% threshold, a highlight will occur when >= 50% of total time was traded at point of control.
               :vpoc-threshold 50 ; [1 100]
               :delta-rule :standard-vpoc ; [:vpoc+delta :vpoc-each-side] Standard VPOC = volume point of control. VPOC with delta indication = volume point of control with direction indicated as color (buy/sell). VPOCs separately for each side = volume point of control for each side separately with direction indicated as color.")
  }})