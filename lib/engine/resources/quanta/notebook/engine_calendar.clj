(ns quanta.notebook.engine-calendar
  (:require
   [ta.engine.javelin.calendar :as ecal]
   [ta.engine.javelin :as je]))

(def env (je/create-engine-javelin))

env

(ecal/get-calendar env [:us :d])
env
(ecal/active-calendars env)

(ecal/get-calendar env [:us :h])
(ecal/active-calendars env)

(ecal/set-calendar! env {:calendar [:us :h]
                         :time :now})

@(ecal/get-calendar env [:us :h])
@(ecal/get-calendar env [:us :d])

  

