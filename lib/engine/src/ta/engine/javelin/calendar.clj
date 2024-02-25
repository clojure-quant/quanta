(ns ta.engine.javelin.calendar
  (:require
    [taoensso.timbre :refer [trace debug info warn error]]
    [javelin.core-clj :refer [cell cell=]]))

(defn create-calendar [env calendar]
  (assert (vector? calendar) "calendar needs to be [:market :interval]")
  (info "creating calendar: " calendar)
  (let [c (cell nil)] ; (nom/error “not started”)
    (swap! (:calendars env) assoc calendar c)
    c))

(defn get-calendar [env calendar]
  (or (get @(:calendars env) calendar)
      (create-calendar env calendar)))

(defn set-calendar! [env {:keys [calendar time]}]
  (info "set-calendar! cal: " calendar " time: " time)
  (let [c (get-calendar env calendar)]
      ; we need to set time by calendar to env!!!!!
    (reset! c time)))

(defn active-calendars [env]
  (let [r (keys @(:calendars env))]
    (if r
      r
      [])))

(comment

  (def env nil)
  env

  (get-calendar env [:us :d])
  env
  (active-calendars env)

  (get-calendar env [:us :h])
  (active-calendars env)

  (set-calendar! env {:calendar [:us :h]
                      :time :now})

  @(get-calendar env [:us :h])
  @(get-calendar env [:us :d])


  (create-calendar env nil)


 ; 
  )

