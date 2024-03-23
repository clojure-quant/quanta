(ns ta.trade.metrics
  (:require
   [ta.trade.roundtrip.performance :refer [add-performance]]
   [ta.trade.roundtrip.metrics :refer [calc-roundtrip-metrics]]
   [ta.trade.nav.metrics :refer [calc-nav-metrics]]
   [ta.trade.nav.grouped :refer [grouped-nav]]))

(defn metrics [roundtrip-ds]
  (let [roundtrip-ds (add-performance roundtrip-ds)
        rt-metrics (calc-roundtrip-metrics roundtrip-ds)
        nav-metrics (calc-nav-metrics roundtrip-ds)
        nav-ds (grouped-nav roundtrip-ds)]
    {:roundtrip-ds roundtrip-ds
     :metrics {:roundtrip rt-metrics
               :nav nav-metrics}
     :nav-ds nav-ds}))


