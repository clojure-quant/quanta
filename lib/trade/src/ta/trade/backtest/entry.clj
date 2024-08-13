(ns ta.trade.backtest.entry
  (:require
   [ta.trade.backtest.entry.size :refer [positionsize]]))

; entry

(defn entry? [signal]
  (and signal ; signal might be nil
       (contains? #{:long :short} signal)))

(defn eventually-entry-position [asset size-rule
                                 {:keys [date idx close entry] :as _row}]
  (when (entry? entry)
    {:side entry
     :asset asset
     :qty (positionsize size-rule close)
     :entry-idx idx
     :entry-date date
     :entry-price close}))

(comment
  (require '[tick.core :as t])

  (def row {:close 100.0 :entry :long
            :idx 107 :date (t/instant)})

  (eventually-entry-position "QQQ" [:fixed-qty 3.1] row)
  (eventually-entry-position "QQQ" [:fixed-amount 15000.0] row)

; 
  )
