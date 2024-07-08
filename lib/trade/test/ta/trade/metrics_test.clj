(ns ta.trade.metrics-test
  (:require
   [clojure.test :refer :all]
   [tick.core :as t]
   [tablecloth.api :as tc]
   [ta.trade.backtest.from-entry :refer [entry-signal->roundtrips]]
   [ta.trade.roundtrip.core :refer [roundtrip-stats]]))

(def alex-ds (tc/dataset {:asset ["BTC" "BTC" "BTC"]
                          :close [1.0 2.0 3.0]
                          :low [1.0 2.0 3.0]
                          :high [1.0 2.0 3.0]
                          :date [(t/instant "1999-02-01T20:00:00Z")
                                 (t/instant "2000-02-01T20:00:00Z")
                                 (t/instant "2001-02-01T20:00:00Z")]
                          :entry-bool [false false true]
                          :entry [:flat :short :flat]
                          :bars-above-b1h 51
                          :d [1.0 Double/NaN nil]}))

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
  (is (= (metrics-for-bar-ds alex-ds) #{:roundtrip-ds :metrics :nav-ds})))

(comment 
  (metrics-for-bar-ds alex-ds)
  ;; => [:roundtrip-ds :metrics :nav-ds]

  
 ; 
  )