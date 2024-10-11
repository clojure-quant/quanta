(ns quanta.calendar.florian
    (:require
    [tick.core :as t]
    [ta.calendar.calendars :as caldb]
    [ta.calendar.helper :as calhelp]))

  (defn market-info [market-kw]
  (let [cal (caldb/get-calendar market-kw)
        dt (t/instant)
        open? (calhelp/time-open? cal dt)
        business? (calhelp/day-open? cal dt)]
    {:market market-kw
     :open? open?
     :business business?
     :as-of-dt dt}))
  

  (market-info :crypto)
  ;; => {:market :crypto, :open? true, :business true, :as-of-dt #time/instant "2024-10-11T20:57:54.366650110Z"}

  
  (market-info :eu)
  ;; => {:market :eu, :open? true, :business true, :as-of-dt #time/instant "2024-10-11T20:58:03.620756893Z"}




 (market-info :jp)
 ;; => {:market :jp, :open? true, :business true, :as-of-dt #time/instant "2024-10-11T20:58:13.100221665Z"}


