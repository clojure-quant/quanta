(ns demo.playground.symbol-list
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [ta.random :refer [random-ts]]
   [ta.warehouse :as wh]
   [demo.env.config :refer [w-stocks log-config!]])
  (:gen-class))

(def w w-stocks)

(defn print-symbol-list
  [list-name]
  (let [liste (wh/load-list w list-name)]
    (info (pr-str liste))))

(defn fn-print-symbol-list
  [list-map]
  (print-symbol-list (:name list-map)))

(defn -main
  ([]
   (warn "printing default list: currency")
   (print-symbol-list "currency"))
  ([list-name]
   (warn "printing user defined list: " list-name)
   (print-symbol-list list-name)))

(comment

  (wh/load-list w "currency")

  (print-symbol-list "currency")
  (print-symbol-list "fidelity-select")
;  
  )
;  (-main "currency")

;  (-main "fidelity-select")