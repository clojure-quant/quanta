(ns quanta.notebook.trade-entry-exit
  (:require
   [tick.core :as t]
   [tablecloth.api :as tc]
   [ta.trade.signal.core :refer [create-positions]]
   [ta.trade.metrics :refer [metrics]]
   [ta.viz.ds.metrics :refer [metrics-render-spec-impl]]))

(def ds (tc/dataset {:date (repeatedly 6 #(t/instant))
                     :close [100.0 104.0 106.0 103.0 102.0 108.0]
                     :high [100.0 104.0 106.0 103.0 102.0 108.0]
                     :low [100.0 104.0 106.0 103.0 102.0 108.0]
                     :entry [:long :nil nil :short :nil :nil]}))

ds

(def rts (-> (create-positions {:asset "QQQ"
                                :entry [:fixed-qty 3.1]
                                :exit [:time 2
                                       :loss-percent 2.5
                                       :profit-percent 5.0]}
                               ds)
             :roundtrips))

rts
 
(-> (metrics rts)
    (metrics-render-spec-impl)
 )
