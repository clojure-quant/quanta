(ns juan.series
  (:require
   [ta.data.import :refer [import-series]]
   [juan.data :refer [settings instruments]]
   ))


(defn get-daily-symbol [modus symbol]
  (import-series :kibot {:symbol symbol
                         :frequency "D"
                         :warehouse :juan}
                 modus
                 {}))

(defn get-daily [modus]
  (doall (map #(get-daily-symbol modus %) (map :fx instruments))))


(defn get-daily-futures [modus  month year]
  (doall (map #(get-daily-symbol modus (str % month year)) (map :future instruments))))



(comment

  (get-daily :full)
  (get-daily :append)

  (get-daily-futures :full 12 23)


  ;
  )




