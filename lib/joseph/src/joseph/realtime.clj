(ns joseph.realtime
  (:require
    [taoensso.timbre :refer [trace debug info warnf error]]
    ;[ta.helper.date :refer [parse-date]]
    ;[tick.core :as t]
   [tablecloth.api :as tc]
   [ ta.warehouse.symbol-db :as db]
    [ta.data.api-ds.kibot :as kibot]
    [ta.helper.ds :refer [ds->map]]))

(defn realtime-snapshot [symbols]
  (let [data (kibot/get-snapshot symbols)
        data-ok (if data
                  (ds->map data)
                  [{:symbol "DAX0" :close 17000.0}
                   {:symbol "INTC" :close 40.0}])
        ]
    (info "realtime snapshot symbols: " (map :symbol data-ok))
    data-ok))


(defn get-last-daily [symbol]
  (info "get-last-daily: " symbol)
 (-> (kibot/get-series symbol "D" 1 {})
     (tc/add-column :symbol symbol)
     ))

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
    (realtime-snapshot stocks)
  ))

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
