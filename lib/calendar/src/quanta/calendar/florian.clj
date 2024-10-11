(ns quanta.calendar.florian
    (:require
    [tick.core :as t]
    [ta.calendar.calendars :as caldb]
    [ta.calendar.helper :as calhelp]))
    
  

  (defn market-info [market-kw]
  (let [cal (caldb/get-calendar :us)
        dt (t/instant)
        open? (calhelp/time-open? cal dt)
        business? (calhelp/day-open? cal dt)]
    {:market market-kw
     :open? open?
     :business business?
     :as-of-dt dt}))
  

  (market-info :crypto)
  ;; => {:market :crypto, 
  ;;     :open? true, 
  ;;     :business true, 
  ;;     :as-of-dt #time/instant "2024-10-11T20:51:34.005611822Z"

  
  (market-info :eu)
  ;; => {:market :eu, 
  ;;     :open? true, 
  ;;     :business true, 
  ;;     :as-of-dt #time/instant "2024-10-11T20:51:47.749174115Z"}


 (market-info :jp)
 ;; => {:market :jp, 
 ;;     :open? true, 
 ;;     :business true, 
 ;;     :as-of-dt #time/instant "2024-10-11T20:53:01.242739932Z"}
