(ns ta.tradingview.date
  (:require
    [tick.core :as tick]
   )
  )






(defn date->ui-int
  "date => integer YYYYMMDD"
  [date]
  (let [day (t/day date)
        hour (t/hour date)
        min (t/minute date)
        sec (t/second date)]
    (+ (* day 1000000) (* hour 10000) (* min 100) sec)))



(comment

  
    ; epoch conversions
  (to-epoch-no-ms (t/now))
  (to-epoch-no-ms (-> 14 t/days t/ago))
  (type (to-date 1487289600))
  (type (tick/date-time 2010 10 3)
        )

  (dt2str 1487289600)
  (c/from-long 1487289600000)

  (println yyyyMMdd)
  (fmt/unparse yyyyMMdd (t/date-time 2010 10 3))

  (server-time)

  ;
  )