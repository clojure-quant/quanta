(ns quanta.notebook.docs.trade-position
  (:require
   [tick.core :as t]
   [tablecloth.api :as tc]
   [ta.trade.backtest.from-position :refer [signal->roundtrips]]
   [ta.trade.roundtrip.core :refer [roundtrip-stats]]
   [ta.viz.trade.core :refer [roundtrip-stats-ui]]))

(def signal-ds (tc/dataset {:date [(t/instant "2020-01-01T00:00:00Z")
                                   (t/instant "2020-01-02T00:00:00Z")
                                   (t/instant "2020-01-03T00:00:00Z")
                                   (t/instant "2020-02-04T00:00:00Z")
                                   (t/instant "2020-03-05T00:00:00Z")
                                   (t/instant "2020-04-06T00:00:00Z")
                                   (t/instant "2020-05-07T00:00:00Z")]
                            :asset ["BTCUSDT" "BTCUSDT" "BTCUSDT" "BTCUSDT"
                                    "BTCUSDT" "BTCUSDT" "BTCUSDT"]
                            :close [1.0 2.0 3.0 4.0 5.0 6.0 7.0]
                            :signal [:long :hold :flat ;rt1 
                                     :short :hold :hold :flat ; rt2
                                     ]}))
signal-ds

(def rts (signal->roundtrips signal-ds))

(def r (roundtrip-stats rts))

(:roundtrip-ds r)
(:metrics r)
(:nav-ds r)

(roundtrip-stats-ui r)


