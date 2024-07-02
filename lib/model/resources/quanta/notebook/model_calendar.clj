(ns quanta.notebook.model-calendar
  (:require
   [quanta.model.javelin.calendar :as ecal]
   [quanta.model.javelin :as je]))

(def env (je/create-model-javelin))

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

  

