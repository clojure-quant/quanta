(ns joseph.trades
  (:require
    [clojure.edn :as edn]
    [ta.helper.date :refer [parse-date]]
    [tick.core :as t] 
   ))

(def d parse-date)

(def trades
  [{:symbol "QQQ"
    :side :long
    :entry-date (d "2021-03-15")
    :exit-date (d "2021-05-12")
    :entry-price 300.50
    :exit-price 320.10}
   {:symbol "QQQ"
    :side :long
    :entry-date (d "2022-03-15")
    :exit-date (d "2022-05-12")
    :entry-price 300.50
    :exit-price 320.10}
   {:symbol "QQQ"
    :side :long
    :entry-date (d "2023-03-15")
    :exit-date (d "2023-05-12")
    :entry-price 300.50
    :exit-price 320.10}
   {:symbol "QQQ"
    :side :short
    :entry-date (d "2023-06-15")
    :exit-date (d "2023-07-12")
    :entry-price 400.50
    :exit-price 420.10}])


(defn entry-vol [{:keys [entry-price qty side]}]
  (* entry-price qty))

(defn live-trade? [{:keys [exit-date]}]
  (nil? exit-date))

(defn parse-date-if-exists [dt-str]
  (when dt-str (parse-date dt-str)))

(defn load-trades []
  (->> (slurp "../resources/trades-upload.edn")
       (edn/read-string)
       ;(remove live-trade?)
       (map #(update % :entry-date parse-date))
       (map #(update % :exit-date parse-date-if-exists))
       (map #(assoc % :entry-vol (entry-vol %)))
       ))

(def cutoff-date (parse-date "2022-01-01"))

(defn invalid-date [{:keys [entry-date exit-date]}]
  (or (t/<= entry-date cutoff-date)
      (and exit-date (t/<= exit-date cutoff-date))))

(defn symbol= [s]
  (fn [{:keys [symbol]}]
    (= symbol s)))

(defn load-trades-valid []
  (->> (load-trades)
       (remove invalid-date)
       ;(remove (symbol= "DAX0"))
       ;(remove (symbol= "BZ0"))
       ;(remove (symbol= "MNQ0"))
       ;(remove (symbol= "MYM0"))
       ))

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
  
  (load-trades-valid)
  (-> (load-trades-valid) count)

  )