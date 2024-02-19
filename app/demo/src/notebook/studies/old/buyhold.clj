(ns notebook.studies.buyhold
  (:require
   [tablecloth.api :as tc]
   [ta.warehouse :as wh]
   [ta.trade.roundtrip-backtest :refer [run-backtest]]
   [ta.trade.print :refer [print-overview-stats print-roundtrip-stats
                              print-roundtrips print-roundtrips-pl-desc]]
   [ta.trade.metrics.roundtrip-stats :refer [roundtrip-performance-metrics]]
   [ta.algo.buy-hold :refer [buy-hold-signal]]
   [ta.viz.table :refer [print-table]]))

(def s "BTCUSD")
(def f "D")

; show first/last close

(let [c (-> (wh/load-symbol :crypto f s)
            :close)]
  [(first c) (last c)])

; get specific row

(-> (wh/load-symbol :crypto f s)
    (tc/select-rows 0))

(def options
  {:w :crypto
   :symbol "BTCUSD"
   :frequency f})

(def r
  (run-backtest buy-hold-signal options))

; roundtrips
r
(print-roundtrips r)
(print-roundtrips-pl-desc r)

; roundtrip-group
(print-overview-stats r)
(print-roundtrip-stats r)

(roundtrip-performance-metrics r)

(print-table (:ds-roundtrips r))

(meta (:ds-roundtrips r))




