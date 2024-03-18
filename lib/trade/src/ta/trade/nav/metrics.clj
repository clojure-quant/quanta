(ns ta.trade.nav.metrics
  (:require
   [ta.indicator.drawdown :refer [drawdowns-from-value]]))

(defn calc-nav-metrics [roundtrip-perf-ds]
  (assert (:cum-ret-log roundtrip-perf-ds) "to calc nav-metrics :cum-ret-log column needs to be present!")
  (assert (:nav  roundtrip-perf-ds) "to calc nav-metrics :nav column needs to be present!")
  ;(println "NAV metrics...")
  (let [nav (:nav roundtrip-perf-ds)
        cum-ret-log (:cum-ret-log roundtrip-perf-ds)
        dd (drawdowns-from-value nav)]
    ;(println "NAV-metrics .. done!")
    {:cum-pl (last cum-ret-log)
     :max-dd (apply max dd)}))



