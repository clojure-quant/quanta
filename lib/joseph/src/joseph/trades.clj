(ns joseph.trades
  (:require
    [clojure.edn :as edn]
    [ta.helper.date :refer [parse-date]]
    [tick.core :as t] 
   ))

(def d parse-date)

(def trades
  [{:symbol "QQQ"
    :direction :long
    :entry-date (d "2021-03-15")
    :exit-date (d "2021-05-12")
    :entry-price 300.50
    :exit-price 320.10}
   {:symbol "QQQ"
    :direction :long
    :entry-date (d "2022-03-15")
    :exit-date (d "2022-05-12")
    :entry-price 300.50
    :exit-price 320.10}
   {:symbol "QQQ"
    :direction :long
    :entry-date (d "2023-03-15")
    :exit-date (d "2023-05-12")
    :entry-price 300.50
    :exit-price 320.10}
   {:symbol "QQQ"
    :direction :short
    :entry-date (d "2023-06-15")
    :exit-date (d "2023-07-12")
    :entry-price 400.50
    :exit-price 420.10}])


(defn entry-vol [{:keys [entry-price qty direction]}]
  (* entry-price qty))

(defn load-trades []
  (->> (slurp "../resources/trades-upload.edn")
       (edn/read-string)
       ;(filter #(= :equity (:category %)))
       (map #(update % :entry-date parse-date))
       (map #(update % :exit-date parse-date))
       (map #(assoc % :entry-vol (entry-vol %)))
       ))

(def cutoff-date (parse-date "2022-01-01"))

(defn invalid-date [{:keys [entry-date exit-date]}]
  (or (t/<= entry-date cutoff-date)
      (t/<= exit-date cutoff-date)))

(defn load-trades-valid []
  (->> (load-trades)
       (remove invalid-date)))

(defn load-trades-demo []
  trades)

(comment
  (-> (load-trades-demo) count)
  (-> (load-trades) count)
  (->> (load-trades)
        ;(filter #(= :future (:category %))) 
       (map :symbol)
       (into #{})
       (into []))
   ;; => ["ZC" "DAX" "M2K" "MNQ" "BZ" "RB" "MYM" "MES" "NG"]
   ;; => ["RIVN" "GOOGL" "FCEL" "NKLA" "INTC" "FRC" "AMZN" "WFC" "PLTR"]
  )