(ns ta.tradingview.date
  (:require
    [tick.core :as tick]
   )
  )






#_(defn date->ui-int
  "date => integer YYYYMMDD"
  [date]
  (let [day (tick/day date)
        hour (t/hour date)
        min (t/minute date)
        sec (t/second date)]
    (+ (* day 1000000) (* hour 10000) (* min 100) sec)))



(comment

  ;(date->ui-int (tick/now))

  ;
  )