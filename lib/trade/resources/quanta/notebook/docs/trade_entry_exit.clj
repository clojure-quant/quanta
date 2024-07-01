(ns quanta.notebook.docs.trade-entry-exit
  (:require
   [tick.core :as t]
   [tablecloth.api :as tc]
   [ta.trade.backtest.from-entry :refer [entry-signal->roundtrips]]
   [ta.trade.roundtrip.core :refer [roundtrip-stats]]
   [ta.viz.trade.core :refer [roundtrip-stats-ui]]))

(def ds (tc/dataset {:date (repeatedly 6 #(t/instant))
                     :close [100.0 104.0 106.0 103.0 102.0 108.0]
                     :high [100.0 104.0 106.0 103.0 102.0 108.0]
                     :low [100.0 104.0 106.0 103.0 102.0 108.0]
                     :entry [:long :nil nil :short :nil :nil]}))

ds

(def rts (-> (entry-signal->roundtrips {:asset "QQQ"
                                :entry [:fixed-qty 3.1]
                                :exit [:time 2
                                       :loss-percent 2.5
                                       :profit-percent 5.0]}
                               ds)
             :roundtrips))


rts
 
(-> (roundtrip-stats rts)
    (roundtrip-stats-ui)
 )
