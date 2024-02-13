(ns ta.calendar.window
  (:require
   [tick.core :as t]))

(defn- now-datetime []
   (t/now))

(defn- subtract-days [dt-inst days]
  ; (t/+ due (t/new-period 1 :months)) this does not work
  ; https://github.com/juxt/tick/issues/65
   (t/<< dt-inst (t/new-duration days :days)))

(defn recent-days-window [n]
  (let [end (now-datetime)]
    {:start (subtract-days end n)
     :end end}))


(defn recent-years-window [n]
  (let [end (now-datetime)]
    {:start (subtract-days end (* 365 n))
     :end end}))


(comment
  (now-datetime)
  (recent-days-window 10)
  (recent-years-window 1)

 ; 
  )
