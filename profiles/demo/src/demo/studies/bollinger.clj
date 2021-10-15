(ns demo.studies.bollinger
  (:require
   [taoensso.timbre :refer [trace debug info error]]
   [tablecloth.api :as tablecloth]
   [ta.trade.backtest :as backtest]
   [ta.dataset.helper :as helper]
   [ta.indicator.sma :as sma]
   [demo.studies.helper.bollinger :as bs]
   [demo.env.config :refer [w-crypto]]))

(def default-options
  {:sma-length 20
   :stddev-length 20
   :mult-up 1.5
   :mult-down 1.5
   :forward-size 20})

(def r
  (backtest/run-study w-crypto "ETHUSD" "D"
                      bs/bollinger-study
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
    (backtest/get-forward-window 23 1)
    (tablecloth/select-columns [:index :date :close :bb-lower :bb-upper :above :below]))
; close: 132.5 
; bb-lower: 100.84180207
; bb-upper: 126.24319777
(def bb-range (- 126.24319777 100.84180207))

; get forward window
(-> (:ds-study r)
    (backtest/get-forward-window 24 20)
    (tablecloth/select-columns [:index :date :low :high :close]))
; highest high: 166.00000000

(def highest-close
  (- 166.0 132.5))

bb-range
highest-close

(def sma-cross-options {:sma-length-st  4  ; (1h = 4* 15 min)
                        :sma-length-lt 24  ; (6h = 24* 15 min)
                        })
(def r2
  (backtest/run-study w "ETHUSD" "D"
                      sma/add-sma-indicator
                      sma-cross-options))

(helper/print-overview r2)

r2
