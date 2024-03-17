(ns ta.trade.core
  (:require
   [ta.trade.roundtrip-backtest :refer [signal-ds->roundtrips]]
   [ta.trade.metrics.roundtrip :refer [roundtrip-metrics]]
   [ta.trade.nav.realized :refer [realized-nav]]
   [ta.trade.metrics.nav :refer [nav-metrics]]))


(defn trade-summary [bar-signal-ds]
(let [{:keys [trade-ds roundtrip-ds]} (signal-ds->roundtrips bar-signal-ds)
      rt-metrics (roundtrip-metrics roundtrip-ds)
      nav-ds (realized-nav trade-ds)
      nav-metrics (nav-metrics trade-ds)
      ]
  {:trade-ds trade-ds
   :roundtrip-ds roundtrip-ds
   :nav-ds nav-ds
   :metrics {:roundtrip rt-metrics
             :nav nav-metrics
             }}))


(comment 
  (require '[tick.core :as t])
  (require '[tablecloth.api :as tc])

(def signal-ds (tc/dataset {:date [(t/instant "2020-01-01T00:00:00Z")
                                   (t/instant "2020-01-12T00:00:00Z")
                                   (t/instant "2020-01-17T00:00:00Z")
                                   (t/instant "2020-01-20T00:00:00Z")
                                   (t/instant "2020-01-22T00:00:00Z")
                                   (t/instant "2020-01-23T00:00:00Z")
                                   (t/instant "2020-01-24T00:00:00Z")]
                            :close [1 2 3 4 5 6 7]
                            :signal [:buy :hold :flat :buy :hold :hold :flat]}))
signal-ds

(trade-summary signal-ds)

;
    )