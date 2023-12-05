(ns juan.series
  (:require
   [taoensso.timbre :refer [info warn error]]
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
  (info "kibot daily timeseries download ..")
  (doall (map #(get-daily-symbol modus %) (map :fx instruments)))
  (info "kibot daily timeseries download finished!")
  )


(defn get-daily-futures [modus  month year]
  (doall (map #(get-daily-symbol modus (str % month year)) (map :future instruments))))



(comment

  (get-daily :full)
  (get-daily :append)

  (get-daily-futures :full 12 23)


  ;
  )




