(ns repl.experiment
  (:require
   [clojure.test :refer :all]
   [taoensso.timbre :refer [trace debug info error]]
   [ta.random :refer [random-ts]]
   [ta.warehouse :as wh]
   [ta.config :refer [w]]))


(taoensso.timbre/info "hi victor")
(info "hi victor")


(ta.warehouse/load-list ta.config/w "currency")


(let [liste (wh/load-list w "currency")]
  (count liste)
  )


(->> "currency"
    (wh/load-list w)
    ; count
     ;last
     first
    )


(ta.warehouse/load-list ta.config/w "fidelity-select")


(defn print-symbol-list
  [list-name]
  (let [liste (wh/load-list w list-name)]
    (info (pr-str liste))
    ))

; (print-symbol-list "currency")
; (print-symbol-list "fidelity-select")


(defn main-print-symbol-list
  [list-map]
  (print-symbol-list (:name list-map))
  )