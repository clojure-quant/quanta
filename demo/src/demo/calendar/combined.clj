(ns demo.calendar.combined
  (:require
   [tick.core :as t]
   [quanta.calendar.combined :refer [combined-event-seq]]
   [clojure.pprint :refer [print-table]]))

(def start-dt (t/now))
start-dt

(def windows [[:crypto :h]
              [:crypto :m]])

(defn print-n [windows days]
  (let [end-dt (t/>> start-dt (t/new-duration days :days))
        c (combined-event-seq {:start start-dt 
                               :end end-dt} windows)]
    (->> c
         (print-table))))

(print-n [[:crypto :h]
          [:crypto :m]]
         3)

(print-n [[:eu :d]
          [:us :d]]
         10)

(print-n [[:test-short :d]
          [:test-short :h]
          [:test-short :m30]]
         30)