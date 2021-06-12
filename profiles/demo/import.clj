(ns import
  (:require
   [clojure.edn :as edn]
   [clojure.pprint :refer [print-table]]
   [ta.data.alphavantage :as av]
   [ta.warehouse :as wh]
   [tech.v3.dataset :as ds]
   ))

(-> "creds.edn" slurp edn/read-string
    :alphavantage av/set-key!)

(defn gc [s]
  (->> s
       (av/get-daily "compact")
       ;(map :close)
       ))

(defn gf [s]
  (->> s
       (av/get-daily "full")
       ;(map :close)
       ))


(defn run [_]
  (let [d (gc "MSFT")
        ds (ds/->dataset d)
        ]
    (println (pr-str d))
    (wh/save-ts ds "MSFT")
    ))
  
 
