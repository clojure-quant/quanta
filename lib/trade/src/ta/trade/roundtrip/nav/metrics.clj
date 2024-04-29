(ns ta.trade.roundtrip.nav.metrics
  (:require
   [ta.indicator.drawdown :refer [drawdowns-from-value]]))

(defn calc-nav-metrics [roundtrip-perf-ds]
  (assert (:cum-ret-volume roundtrip-perf-ds) "to calc nav-metrics :cum-ret-log column needs to be present!")
  (let [cum-ret-volume (:cum-ret-volume roundtrip-perf-ds)
        dd (drawdowns-from-value cum-ret-volume)]
    {:cum-pl (last cum-ret-volume)
     :max-dd (apply max dd)}))



