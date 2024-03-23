(ns ta.trade.core
  (:require
   [ta.trade.metrics :as m]
   [ta.trade.position :refer [signal->roundtrips]]))

(defn trade-summary [bar-signal-ds]
  (let [roundtrip-ds (signal->roundtrips bar-signal-ds)]
    (m/metrics roundtrip-ds)))

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
                              :close [1.0 2.0 3.0 4.0 5.0 6.0 7.0]
                              :signal [:long :hold :flat ;rt1 
                                       :short :hold :hold :flat ; rt2
                                       ]}))
  signal-ds

  (signal->roundtrips signal-ds)

  (def r (trade-summary signal-ds))

  (:roundtrip-ds r)
  (:metrics r)
  (:nav-ds r)

;
  )