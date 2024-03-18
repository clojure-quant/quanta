(ns ta.trade.core
  (:require
   [ta.trade.roundtrip.create :refer [signal->roundtrips]]
   [ta.trade.roundtrip.performance :refer [add-performance]]
   [ta.trade.roundtrip.metrics :refer [calc-roundtrip-metrics]]
   [ta.trade.nav.metrics :refer [calc-nav-metrics]]
   [ta.trade.nav.grouped :refer [grouped-nav]]))

(defn trade-summary [bar-signal-ds]
  (let [roundtrip-ds (-> bar-signal-ds signal->roundtrips add-performance)
        rt-metrics (calc-roundtrip-metrics roundtrip-ds)
        nav-metrics (calc-nav-metrics roundtrip-ds)
        nav-ds (grouped-nav roundtrip-ds)]
    {:roundtrip-ds roundtrip-ds
     :metrics {:roundtrip rt-metrics
               :nav nav-metrics}
     :nav-ds nav-ds}))

(comment
  (require '[tick.core :as t])
  (require '[tablecloth.api :as tc])

  (def signal-ds (tc/dataset {:date [(t/instant "2020-01-01T00:00:00Z")
                                     (t/instant "2020-01-02T00:00:00Z")
                                     (t/instant "2020-01-03T00:00:00Z")
                                     (t/instant "2020-02-04T00:00:00Z")
                                     (t/instant "2020-03-05T00:00:00Z")
                                     (t/instant "2020-04-06T00:00:00Z")
                                     (t/instant "2020-05-07T00:00:00Z")]
                              :close [1 2 3 4 5 6 7]
                              :signal [:long :hold :flat ;rt1 
                                       :short :hold :hold :flat ; rt2
                                       ]}))
  signal-ds

  (def r (trade-summary signal-ds))

  (:roundtrip-ds r)
  (:metrics r)
  (:nav-ds r)
  

;
  )