(ns notebook.studies.sma
  (:require
   [ta.backtest.study :refer [run-study]]
   [ta.trade.roundtrip-backtest :refer [run-backtest run-backtest-parameter-range]]
   [ta.trade.metrics.roundtrip-stats :as s]
   [ta.trade.print :as p]
   [demo.algo.sma :refer [sma-signal]]
   [ta.algo.buy-hold :refer [buy-hold-signal]]))

;; daily backtest

(def options-d
  {:w :crypto
   :symbol "ETHUSD"
   :frequency "D"
   :sma-length-st 5
   :sma-length-lt 50})

(run-study sma-signal options-d)

(def r-d
  (run-backtest sma-signal options-d)
  ;(run-backtest supertrend-signal (assoc options-d :w w-shuffled))
  ;(run-backtest buy-hold-signal (assoc options-d :symbol "BTCUSD"))
  )
(-> r-d
    :ds-roundtrips
    (s/calc-roundtrip-stats :position))

(p/print-roundtrips r-d)
(p/print-overview-stats r-d)
(p/print-roundtrip-stats r-d)
(p/viz-roundtrips r-d)

(->> (run-backtest-parameter-range
      sma-signal options-d
      :w [:crypto :shuffled])
     s/backtests->performance-metrics)
; great! shuffled returns dont produce positive returns

(->> (run-backtest-parameter-range
      sma-signal options-d
      :symbol ["ETHUSD" "BTCUSD"])
     s/backtests->performance-metrics)

(def options-15
  {:w :crypto
   :symbol "ETHUSD"
   :frequency "15"
   :sma-length-st 20
   :sma-length-lt 200})

(def r-15
  (run-backtest sma-signal options-15)
  ;(run-backtest supertrend-signal (assoc options-d :w w-shuffled))
  ;(run-backtest buy-hold-signal (assoc options-d :symbol "BTCUSD"))
  )
(:ds-study r-15)

(p/print-roundtrips r-15)
(p/print-nav r-15)
(p/print-roundtrip-stats r-15)
(s/roundtrip-metrics r-15)

(-> (run-backtest buy-hold-signal options-15)
    p/print-nav)

;; sma strategy brings *2 the total return of buy+hold
;; RISK IS SUBSTANTIALLY LESS.

(->> (run-backtest-parameter-range
      sma-signal options-15
      :sma-length-st [10 20 30 40 50 60 70 80 90 100])
     s/backtests->performance-metrics)

; not better than daily.

(->> (run-backtest-parameter-range
      sma-signal options-15
      :symbol ["ETHUSD" "BTCUSD"])
     s/backtests->performance-metrics)



