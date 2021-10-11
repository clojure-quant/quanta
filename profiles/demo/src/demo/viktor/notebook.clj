(ns demo.viktor.notebook
  (:require
   [taoensso.timbre :refer [trace debug info error]]
   [ta.dataset.backtest :as backtest]
   [ta.dataset.helper :as helper]
   [demo.viktor.strategy-bollinger :as bs]
   [demo.env.warehouse :refer [w]]))

(def default-options
  {:sma-length 20
   :stddev-length 20
   :mult-up 1.5
   :mult-down 1.5
   :forward-size 20})

(def r
  (backtest/run-study w "ETHUSD" "D"
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
