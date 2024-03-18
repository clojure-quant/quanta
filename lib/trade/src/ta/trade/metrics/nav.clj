(ns ta.trade.metrics.nav
  (:require
   [clojure.set]
   [ta.trade.drawdown :refer [trailing-sum drawdowns-from-value]]
   [ta.trade.position-pl :refer [position-pl]]))

(defn nav-metrics [backtest-ds]
  (println "nav metrics...")
  (let [pl-log (position-pl (:close backtest-ds) (:position backtest-ds))
        cum-pl (trailing-sum pl-log)
        dd (drawdowns-from-value cum-pl)
        max-dd (apply max dd)
        cum-pl-last (last cum-pl)
          ;ds-study-pl (tc/add-columns ds-study
          ;                            {:pl-log pl-log
          ;                             :pl-cum cum-pl
          ;                             :dd dd})
        ]
    (println "nav-metrics .. done!")
    {:cum-pl cum-pl-last
     :max-dd max-dd}))