(ns demo.playground.symbollist
  (:require
   [ta.warehouse :as wh]
   [demo.env.config :refer [w-stocks]])
  (:gen-class))

(def w w-stocks)

(defn print-symbol-list
  [list-name]
  (let [liste (wh/load-list w list-name)]
    (println (pr-str liste))))

(defn fn-print-symbol-list
  [list-map]
  (print-symbol-list (:name list-map)))

(defn -main
  ([]
   (println "printing default list: currency")
   (print-symbol-list "currency"))
  ([list-name]
   (println "printing user defined list: " list-name)
   (print-symbol-list list-name)))

(comment

  (wh/load-list w "currency")

  (print-symbol-list "currency")
  (print-symbol-list "fidelity-select")
;  
  )
;  (-main "currency")

;  (-main "fidelity-select")