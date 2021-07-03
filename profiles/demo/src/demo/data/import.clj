(ns demo.data.import
  (:require
   [clojure.edn :as edn]
   [ta.data.alphavantage :as av]
   [ta.warehouse :as wh]
   [tech.v3.dataset :as ds]))

(-> "creds.edn" slurp edn/read-string
    :alphavantage av/set-key!)
(defn gc [s]
  (->> s
       (av/get-daily "compact")))
(defn gf [s]
  (->> s
       (av/get-daily "full")))

(defn import-symbol [s]
  (let [d (gf s)
        ds (ds/->dataset d)]
    (println "imported " s " - " (count d) "bars.")
    ;(println (pr-str d))
    (wh/save-ts ds s)))

(defn show [s]
  (let [ds (wh/load-ts s)]
    (println s)
    (println ds)))


;(def symbols ["MSFT" "SPY" "XOM"])
(def symbols (wh/load-list  "fidelity-select"))

(defn import [_]
  (doall (map import-symbol symbols)))

(defn run [_]
    (doall (map show symbols)))