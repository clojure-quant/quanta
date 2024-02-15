(ns notebook.algo.poc
  (:require
   [tech.v3.datatype.functional :as fun]
   [tablecloth.api :as tc]
   [ta.calendar.core :as c]))

; port of tradingview-indicator
;https://www.tradingview.com/script/nY63MyD9-Time-volume-point-of-control-quantifytools/

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


(defn add-poc-cols [ds-bars bin-n]
  (let [low (apply fun/min (:close ds-bars))
        high (apply fun/max (:close ds-bars))]
    (-> ds-bars
        (add-col :change (change (:close ds-bars))
                 :bin (bin (:close ds-bars) min max bin-nr))
        :up? (>= chg 0.0)
        :volume-up (if up? volume 0.0)
        :volume-down (if up? 0.0 volume))))



(defn bin-with-most-time [ds-bars-poc]
  (-> ds-bars-poc
      (tc/group-by :bin)
      (tc/aggregate {:count tc/row-count
                     :sum #(fun/sum (:volume %))})
      (tc/sort-by :count :desc)
      tc/first-row
      :bin))

(defn price-with-most-time [ds-bars-poc bin-nr min max]
  (let [bin (bin-with-most-time ds-bars-poc)
        [lower upper] (bin-bounds bin-nr min max)]
    (avg lower upper)))

(defn price-with-highest-volume [ds-bars]
  (-> ds-bars
      (tc/sort-by :volume :desc)
      (tc/first-row)
      :close))

(defn price-with-highest-up-volume [ds-bars]
  (-> ds-bars
      (tc/sort-by :volume-up :desc)
      (tc/first-row)
      :close))

(defn price-with-highest-down-volume [ds-bars]
  (-> ds-bars
      (tc/sort-by :volume-up :desc)
      (tc/first-row)
      :close))

(defn poc-prices 
  "returns a map with differently calculated poc prices.
   input: ds-bars (bars of the higher frequency)
          bin-nr (number of bins for time-poc calculation)"
  [ds-bars bin-n]
  (let [ds-bars-poc (add-poc-cols ds-bars bin-n)]
    {:time (price-with-most-time ds-bars-poc bin-n min max)
     :volume (price-with-highest-volume ds-bars-poc)
     :up-volume (price-with-highest-up-volume ds-bars-poc)
     :down-volume (price-with-highest-down-volume ds-bars-poc)}))

; Ensuring sufficient distance between close and TPOC/VPOC, equal to or greater than half of bar range.

(defn anomalies
  "returns a map with anomalies (keys anomaly-type, value true/false)
   idea is to use it for alert generation."
  [poc close low-1 high-1]
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



(defn indicator-poc [env spec ds-bars]
  (let [ds-bars-ltf (get-bars-lower-timeframe env spec (:lower-timeframe spec))]
    (price-with-highest-volume ds-bars-ltf)
    (-> ds-bars)))

