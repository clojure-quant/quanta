(ns ta.roundtrip-test
  (:require
   [clojure.test :refer :all]
   [tablecloth.api :as tc]
   [ta.algo.buy-hold :refer [buy-hold-signal]]
   [ta.backtest.signal :refer [trade-signal]]
   [ta.backtest.roundtrip-backtest :refer [backtest-ds]]
   [ta.backtest.roundtrip-stats :refer [calc-roundtrip-stats position-stats]]))


(def ds-bars
  (let [n 12]
    (-> {:date (range n)
         :close [1 1 1 1 10 10 10 10 100 100 100 100]}
        (tc/dataset {:dataset-name "ds1"}))))

; ds-bars

(def position-metrics
  (-> ds-bars
      (backtest-ds buy-hold-signal {})
    ;:ds-roundtrips
    ;(calc-roundtrip-stats :position)
      (position-stats)))


(deftest position-metrics-test
  (is (= (get-in position-metrics [:long :trades]) 1)) ; buy-hold has 1 long trade
  (is (= (get-in position-metrics [:long :pl-log-cum]) 2.0)) ; cum log pl is 2 (factor 100)
  )





