(ns show
  (:require
   [clojure.edn :as edn]
   [clojure.pprint :refer [print-table]]
   [ta.warehouse :as wh]
   [tech.v3.dataset :as ds]
   ))


(wh/init-tswh "../../db/")

(defn show [s]
  (let [ds (wh/load-ts s)]
    (println s)
    (println ds)))


(defn run [_]
    (let [symbols ["MSFT" "SPY" "XOM"]]
      (doall (map show symbols))))

  
 
