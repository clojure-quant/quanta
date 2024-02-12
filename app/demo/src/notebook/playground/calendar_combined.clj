(ns notebook.playground.calendar-combined
  (:require
   [ta.calendar.combined :refer [calendar-seq-combined]]
   [clojure.pprint :refer [print-table]]))


(def c (calendar-seq-combined [[:crypto :h]
                               [:crypto :m]]))

(take 1 c)

(defn print-n [windows n]
  (let [c (calendar-seq-combined windows)]
    (->> (take n c)
         (print-table))))


(print-n [[:crypto :h]
          [:crypto :m]]
         1000)


(print-n [[:eu :d]
          [:us :d]]
         10)

(print-n [[:test-short :d]
          [:test-short :h]
          [:test-short :m30]
          ]
         30)