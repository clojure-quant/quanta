(ns ta.trade.metrics-test
  (:require
   [clojure.test :refer :all]
   [tick.core :as t]
   [tablecloth.api :as tc]
   [ta.trade.data :refer [bar-entry-ds time-bar-entry-ds]]
   [ta.trade.backtest.from-entry :refer [entry-signal->roundtrips]]
   [ta.trade.roundtrip.core :refer [roundtrip-stats]]))

(defn metrics-for-bar-ds [bar-ds]
  (->>  bar-ds
        (entry-signal->roundtrips {:asset "BTC"
                                   :entry [:fixed-amount 100000]
                                   :exit [:time 5
                                          :loss-percent 4.0
                                          :profit-percent 5.0]})
        :roundtrips
      ;    (validate-roundtrips-ds)
        (roundtrip-stats)
        keys
        (into #{})))

(deftest metrics-test-only-loss
  ; this tests tests on one hand the entry-exit => roundtrip generation
  ; on the other hand it tests a special case where there are no wins.
  (is (= (metrics-for-bar-ds bar-entry-ds) #{:roundtrip-ds :metrics :nav-ds})))

(comment
  (metrics-for-bar-ds bar-entry-ds)
  ;; => [:roundtrip-ds :metrics :nav-ds]

; 
  )