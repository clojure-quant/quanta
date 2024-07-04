(ns quanta.notebook.calendar-debug
  (:require
   [taoensso.timbre :as timbre :refer [info warn error]]
   [ta.calendar.core :refer [calendar-seq-instant]]))


(->> (calendar-seq-instant [:crypto :d])
     (take 3))

(->> (calendar-seq-instant [:crypto :h])
     (take 3))

(->> (calendar-seq-instant [:crypto :m15])
     (take 3))

(->> (calendar-seq-instant [:crypto :m])
     (take 3))


(defn next-dt [calendar]
  (let [[market interval] calendar]
    (let [dt (->> (calendar-seq-instant calendar)
                  first)]
      [interval dt])))

(defn next-market [market]
  (let [intervals [:d :h :m30 :m15 :m]
        calendars (map (fn [int]
                         [market int]) intervals)]
    (->> (map next-dt calendars)
         (into {}))
    ;calendars
    ))

(next-dt [:crypto :d])
(next-dt [:crypto :h])
(next-dt [:crypto :m])
(next-dt [:crypto :m30])


(next-market :crypto)
;; => {:d #inst "2024-07-02T23:59:59.000000000-00:00",
;;     :h #inst "2024-07-04T02:00:00.000000000-00:00",
;;     :m30 #inst "2024-07-04T02:30:00.000000000-00:00",
;;     :m15 #inst "2024-07-04T02:45:00.000000000-00:00",
;;     :m #inst "2024-07-04T02:51:00.000000000-00:00"}

;; day should be one day later.