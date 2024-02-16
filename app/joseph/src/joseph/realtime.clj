(ns joseph.realtime
  (:require
    [taoensso.timbre :refer [trace debug info warnf error]]
    [tablecloth.api :as tc]
    [ta.db.asset.symbol-db :as db]
    [ta.helper.ds :refer [ds->map]]
    [ta.import.provider.kibot.ds :as kibot]))

(defn realtime-snapshot [symbols]
  (let [data (kibot/get-snapshot symbols)
        data-ok (if data
                  (ds->map data)
                  [])]
    (info "realtime snapshot symbols: " (map :symbol data-ok))
    data-ok))


(defn get-last-daily [asset]
  (info "get-last-daily: " asset)
  (-> (kibot/get-bars {:asset asset
                       :calendar [:us :d]}
                       {:dstart nil})
      (tc/add-column :symbol asset)
      (tc/select-rows [-1])))


(defn get-last-daily-snapshot [symbols]
  (let [[symbol-first & symbols-rest] symbols
        series (map get-last-daily symbols-rest)]
    (reduce (fn [acc i]
              (tc/concat acc i)) 
            (get-last-daily symbol-first)
            series)))

(defn get-instrument [s]
  (let [i (db/instrument-details s)]
    (if i i 
        (case s
          "DAX0" {:symbol s :category :future}
          {:symbol s
           :category :equity}
          )
        )))

(defn stock? [s]
  (let [i (get-instrument s)]
    (= :equity (:category i))))

(defn realtime-snapshot-stocks [symbols]
  (let [stocks (filter stock? symbols)]
    (realtime-snapshot stocks)))

(defn daily-snapshot-futures-raw [symbols]
  (let [futures (remove stock? symbols)
        futures (remove #(= "DAX0" %) futures)]
    (get-last-daily-snapshot futures)))


(defn daily-snapshot-futures [symbols]
  (if (empty? symbols)
      []
      (-> symbols
          (daily-snapshot-futures-raw)
          (ds->map))))

(comment 
  
  (get-last-daily "M2K0")
  
  (get-last-daily-snapshot ["AAPL" "MSFT" "FCEL" "NKLA"])
  (get-last-daily-snapshot ["M2K0" "ZC0" "BZ0"
                            "MNQ0" "MES0"] )


  (kibot/get-snapshot ["AAPL"])
  (kibot/get-snapshot ["$NDX"])
  (kibot/get-snapshot ["FCEL"])
  (kibot/get-snapshot ["MSFT"])
  (kibot/get-snapshot ["BZ0"])

  (kibot/get-snapshot ["RIVN" "MYM0" "RB0" "GOOGL" "FCEL"
                       "NKLA" "M2K0" "INTC" "MES0" "RIG"
                       "ZC0" "FRC" "AMZN" "HDRO" "MNQ0"
                       "BZ0" "WFC" "DAX0" "PLTR" "NG0"])

  (realtime-snapshot-stocks ["RIVN" "MYM0" "RB0" "GOOGL" "FCEL"
                       "NKLA" "M2K0" "INTC" "MES0" "RIG"
                       "ZC0" "FRC" "AMZN" "HDRO" "MNQ0"
                       "BZ0" "WFC" "DAX0" "PLTR" "NG0"])

  (require '[tech.v3.dataset.print :refer [print-range]])
  (-> ["RIVN" "MYM0" "RB0" "GOOGL" "FCEL"
       "NKLA" "M2K0" "INTC" "MES0" "RIG"
       "ZC0" "FRC" "AMZN" "HDRO" "MNQ0"
       "BZ0" "WFC" "DAX0" "PLTR" "NG0"]
      (daily-snapshot-futures-raw)   
      (print-range :all))

  (kibot/get-bars {:asset "NG0"
                   :calendar [:us :d]})
  (daily-snapshot-futures-raw [])
  (daily-snapshot-futures-raw ["NG0"])
  (daily-snapshot-futures ["NG0"])

  (kibot/get-snapshot  
     ["$NDX"
      "AAPL" "FCEL" "MSFT" "BZ0"])


  (realtime-snapshot ["AAPL"])
  
  (realtime-snapshot ["RIVN" "MYM0" "RB0" "GOOGL" "FCEL" 
                      "NKLA" "M2K0" "INTC" "MES0" "RIG" 
                      "ZC0" "FRC" "AMZN" "HDRO" "MNQ0" 
                      "BZ0" "WFC" "DAX0" "PLTR" "NG0"])
  
  (realtime-snapshot ["MES0"])





  ;
  
 ; 
  )
