(ns quanta.model.javelin.calendar
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [de.otto.nom.core :as nom]
   [javelin.core-clj :refer [cell]]))

(defn create-calendar [env calendar]
  (assert (vector? calendar) "calendar needs to be [:market :interval]")
  (info "creating calendar: " calendar)
  (let [c (cell (nom/fail ::calendar {:calendar :not-initialized}))]
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

