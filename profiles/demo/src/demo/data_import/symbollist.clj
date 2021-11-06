(ns demo.playground.symbollist
  (:require
   [ta.warehouse :as wh])
  )

(defn print-symbol-list
  [list-name]
  (let [liste (wh/load-list list-name)]
    (println (pr-str liste))))

(defn fn-print-symbol-list
  [list-map]
  (print-symbol-list (:name list-map)))



(comment

  (wh/load-list "currency")

  (print-symbol-list "currency")
  (print-symbol-list "fidelity-select")
;  
  )
