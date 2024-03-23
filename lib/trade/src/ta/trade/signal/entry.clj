(ns ta.trade.signal.entry)

(defmulti positionsize
  (fn [[type opts] close] type))

(defmethod positionsize :fixed-qty
  [[_type fixed-qty] _close]
  fixed-qty)

(defmethod positionsize :fixed-amount
  [[_type fixed-qty] close]
  (/ fixed-qty close))

; entry

(defn entry? [signal]
  (contains? #{:long :short} signal))

(defn eventually-entry-position [asset size-rule
                                 {:keys [date idx close signal] :as row}]
  (when (entry? signal)
    {:side signal
     :asset asset
     :qty (positionsize size-rule close)
     :entry-idx idx
     :entry-date date
     :entry-price close}))

(comment
  (require '[tick.core :as t])

  (def row {:close 100.0 :signal :long
            :idx 107 :date (t/instant)})

  (eventually-entry-position "QQQ" [:fixed-qty 3.1] row)
  (eventually-entry-position "QQQ" [:fixed-amount 15000.0] row)

; 
  )
