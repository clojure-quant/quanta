(ns ta.trade.core
  (:require
   [ta.trade.metrics :as m]
   [ta.trade.position :refer [signal->roundtrips]]))

(defn trade-summary [bar-signal-ds]
  (let [roundtrip-ds (signal->roundtrips bar-signal-ds)]
    (m/metrics roundtrip-ds)))

