(ns show
  (:require
   [clojure.edn :as edn]
   [clojure.pprint :refer [print-table]]
   [ta.warehouse :as wh]
   [tech.v3.dataset :as ds]
   ))



(defn run [_]
  (let [ds (wh/load-ts "MSFT")]
    (println ds)
    ))
  
 
