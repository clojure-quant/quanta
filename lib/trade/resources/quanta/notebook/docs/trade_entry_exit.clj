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
        (roundtrip-stats-ui))

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

    (require '[ta.trade.roundtrip.validation :refer [validate-roundtrips validate-roundtrips-ds]])
    (require '[tech.v3.dataset :as tds])
    (->>  alex-ds
          (entry-signal->roundtrips {:asset "BTC"
                                     :entry [:fixed-amount 100000]
                                     :exit [:time 5
                                            :loss-percent 4.0
                                            :profit-percent 5.0]})
          :roundtrips
      ;    (validate-roundtrips-ds)
      (roundtrip-stats)
          )
    
    

