(ns demo.studies.buyhold
  (:require
   [tablecloth.api :as tc]
   [ta.warehouse :as wh]
   [ta.backtest.roundtrip-backtest :refer [run-backtest]]
   [ta.backtest.print :refer [print-overview-stats print-roundtrip-stats
                              print-roundtrips print-roundtrips-pl-desc]]
   [ta.algo.buy-hold :refer [buy-hold-signal]]
   [demo.env.config :refer [w-crypto]]))

(def s "BTCUSD")
(def f "D")

; show first/last close

(let [c (-> (wh/load-symbol w-crypto f s)
            :close)]
  [(first c) (last c)])

; get specific row

(-> (wh/load-symbol w-crypto f s)
    (tc/select-rows 0))

(def options
  {:w w-crypto
   :symbol "BTCUSD"
   :frequency f})

(def r
  (run-backtest buy-hold-signal options))

; roundtrips
(print-roundtrips r)
(print-roundtrips-pl-desc r)

; roundtrip-group
(print-overview-stats r)
(print-roundtrip-stats r)





