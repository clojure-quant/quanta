(ns import
  (:require
   [clojure.edn :as edn]
   [clojure.pprint :refer [print-table]]
   [ta.data.alphavantage :as av]
   [ta.warehouse :as wh]
   [tech.v3.dataset :as ds]
   ))

(wh/init-tswh "../../db/")

(-> "creds.edn" slurp edn/read-string
    :alphavantage av/set-key!)

(defn gc [s]
  (->> s
       (av/get-daily "compact")
       ))

(defn gf [s]
  (->> s
       (av/get-daily "full")
       ))


(defn import-symbol [s]
  (let [d (gf s)
        ds (ds/->dataset d)]
    (println "imported " s " - " (count d) "bars.")
    ;(println (pr-str d))
    (wh/save-ts ds s))
  )

(defn run [_]
  (let [symbols ["MSFT" "SPY" "XOM"]]
    (doall (map import-symbol symbols))
    ))
  
 
