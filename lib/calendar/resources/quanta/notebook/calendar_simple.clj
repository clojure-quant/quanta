(ns quanta.notebook.calendar-simple
  (:require
   [ta.calendar.core :refer [calendar-seq]]
   [clojure.pprint :refer [print-table]]))

(def c (calendar-seq :crypto :d))

(take 1 c)

(take 10 c)

(->> (take 1000 c)
     (map println))



