(ns notebook.studies.bollinger
  (:require
   [tablecloth.api :as tc]
   [ta.backtest.study :refer [run-study]]
   [ta.helper.print :as helper]
   [ta.helper.window :refer [get-forward-window]]
   [demo.algo.sma :as sma]
   [demo.algo.bollinger :as bs]))

(def default-options
  {:w :crypto
   :f "D"
   :symbol "ETHUSD"
   :sma-length 20
   :stddev-length 20
   :mult-up 1.5
   :mult-down 1.5
   :forward-size 20})

(def r
  (run-backtest bs/bollinger-study
                default-options))

(keys r)

(bs/print-overview r :ds-study)
(bs/print-overview r :ds-events-all)
(bs/print-overview r :ds-events-forward)
(bs/print-overview r :ds-performance)

(bs/print-all r :ds-study)
(bs/print-all r :ds-events-all)
(bs/print-all r :ds-events-forward)
(bs/print-all r :ds-performance)

(bs/print-backtest-numbers r)

; check if :max-forward-up is correct event index: 24 ****************************************

;  get event bar
(-> (:ds-study r)
    (get-forward-window 23 1)
    (tc/select-columns [:index :date :close :bb-lower :bb-upper :above :below]))
; close: 132.5 
; bb-lower: 100.84180207
; bb-upper: 126.24319777
(def bb-range (- 126.24319777 100.84180207))

; get forward window
(-> (:ds-study r)
    (get-forward-window 24 20)
    (tc/select-columns [:index :date :low :high :close]))
; highest high: 166.00000000

(def highest-close
  (- 166.0 132.5))

bb-range
highest-close

(def sma-cross-options {:w :crypto
                        :symbol "ETHUSD"
                        :freqzency "D"
                        :sma-length-st  4  ; (1h = 4* 15 min)
                        :sma-length-lt 24  ; (6h = 24* 15 min)
                        })
(def r2
  (run-study sma/add-sma-indicator
             sma-cross-options))

(helper/print-overview r2)

r2
