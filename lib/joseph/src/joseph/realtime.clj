(ns joseph.realtime
  (:require
    [taoensso.timbre :refer [trace debug info warnf error]]
    ;[ta.helper.date :refer [parse-date]]
    ;[tick.core :as t]
   [tablecloth.api :as tc]
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
 (-> (kibot/get-series symbol "D" 1 {})
     (tc/add-column :symbol symbol)
     ))

(defn get-last-daily-snapshot [symbols]
  (map get-last-daily symbols))

(comment 
  
  (get-last-daily "M2K0")
  (get-last-daily-snapshot ["M2K0" "ZC0"] )

  (kibot/get-snapshot ["AAPL"])
  (kibot/get-snapshot ["$NDX"])
  (kibot/get-snapshot ["FCEL"])
  (kibot/get-snapshot ["MSFT"])
  (kibot/get-snapshot ["BZ0"])

  (kibot/get-snapshot ["RIVN" "MYM0" "RB0" "GOOGL" "FCEL"
                       "NKLA" "M2K0" "INTC" "MES0" "RIG"
                       "ZC0" "FRC" "AMZN" "HDRO" "MNQ0"
                       "BZ0" "WFC" "DAX0" "PLTR" "NG0"])


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
