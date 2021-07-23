(ns demo.warehouse.import
  (:require
   [clojure.edn :as edn]
   [taoensso.timbre :refer [trace debug info infof  error]]
   [tech.v3.dataset :as tds]
   [ta.data.alphavantage :as av]
   [ta.warehouse :as wh]
   [demo.env.warehouse :refer [w]]))

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
        ds (tds/->dataset d)]
    (println "imported " s " - " (count d) "bars.")
    ;(println (pr-str d))
    (wh/save-ts w ds s)))

(defn import-ts [{:keys [symbol-list]}]
  (let [symbols (wh/load-list w symbol-list)]
    (infof "importing symbol-list: %s (%s) " symbol-list (count symbols))
    (doall (map import-symbol symbols))))

(defn show [s]
  (let [ds (wh/load-ts w s)
        r (tds/mapseq-reader ds)
        l (last r)]
    (println s " - last: " l)))

(defn show-ts [{:keys [symbol-list]}]
  (let [symbols (wh/load-list w symbol-list)]
    (infof "showing symbol-list: %s (%s) " symbol-list (count symbols))
    (doall (map show symbols))))