(ns juan.study.live
  (:require
   [tick.core :as t]
   [tablecloth.api :as tc]
   [ta.algo.backtest :refer [backtest-algo-date]]))

(def algo-spec
  [:day {:type :trailing-bar
         :algo   ['juan.algo.intraday/ensure-date-unique
                  'juan.algo.daily/daily]
         :calendar [:us :d]
         :asset "EURUSD"
         :import :kibot
         :feed :fx
         :trailing-n 80
         :atr-n 10
         :step 0.0001
         :percentile 70}
   :daily-history {:formula [:day]
                   :algo 'ta.viz.publish/publish-dataset
                   :topic [:juan :daily-history]
                   :columns [:date :atr :close]}])

(def r (backtest-algo-date :duckdb algo-spec
        (t/zoned-date-time "2024-02-22T17:00-05:00[America/New_York]")))

r

(keys r)

(tc/select-columns @(:day r) [:date :atr :close :ppivotnr])


(:daily-history r)

