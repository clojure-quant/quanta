(ns ta.data.import
   (:require
    [taoensso.timbre :refer [info warn error]]
    [tablecloth.api :as tc]
    [ta.warehouse.symbollist :refer [load-list]]
     ; import
    [ta.data.import.warehouse :refer [save-symbol]]
    [ta.warehouse.symbol-db :as db]
    [ta.data.import.append :as append]
     ; providers
    [ta.data.api-ds.kibot :as kibot]
    [ta.data.api-ds.alphavantage :as av]
    [ta.data.api-ds.bybit :as bybit]))

(def dict-provider
  {:kibot kibot/get-series
   :alphavantage av/get-series
   :bybit bybit/get-series
   })

(defn import-series 
  ([provider symbol interval range]
    (import-series provider symbol interval range {}))
  ([provider symbol interval range opts]
  (info "import provider: " provider " symbol: " symbol " range: " range)
  (let [get-series (get dict-provider provider)
        series-ds (get-series symbol interval range opts)
        c  (tc/row-count series-ds)]
    (info "imported " symbol " - " c "bars.")
    (when (> c 0)
      (save-symbol symbol interval series-ds)))))


(defn append-series 
  ([provider symbol interval]  
   (append-series provider symbol interval {}))
  ([provider symbol interval opts]
   (info "append provider: " provider " symbol: " symbol " interval: " interval)
   (let [get-series (get dict-provider provider)]
     (append/append-symbol get-series symbol interval opts))))


(defn import-symbols 
  ([provider symbols interval range]
   (import-symbols provider symbols interval range {}))
 ([provider symbols interval range opts]
  (let [symbols (if (string? symbols) 
                    (load-list symbols)
                    symbols)]
  (doall (map
          #(import-series provider % interval range opts)
          symbols)))))

(defn get-category [symbol]
  (-> symbol db/instrument-details :category))

(comment 
  (db/instrument-details "MSFT")
  (get-category "MSFT")
  (get-category "MSFT2")  
 ; 
  )


(defn import-one
  [symbol interval range]
  (let [category (get-category symbol)
        provider (case category
                   :crypto :bybit
                   :future :kibot
                   :kibot)]
    (cond
      (= range :full) 
      (import-series provider symbol interval :full)  

      (= range :append)
      (append-series provider symbol interval)  

      :else 
      (info "no import task for range: " range))
    ))


(comment 
  (-> (db/get-symbols) count)
;  
  )



(defn import-list
   [symbols interval range]
   (let [symbols (cond 
                   (string? symbols) (load-list symbols)
                   (= symbols :all) (db/get-symbols)
                   :else symbols)]
     (doall (map
             #(import-one % interval range)
             symbols))))


(comment 
  ;; import -full
  (import-series :alphavantage "MSFT" "D" :full)
  (import-series :alphavantage "EURUSD" "D" :full) 
  (import-series :alphavantage "BTCUSD" "D" :full) 
  (import-series :kibot "SIL0" "D" :full)
  (import-series :bybit "BTCUSD" "D" :full)


  ; append 
  (append-series :alphavantage "MSFT" "D")
  (append-series :alphavantage "EURUSD" "D")
  (append-series :alphavantage "QQQ" "D")
  (append-series :bybit "BTCUSD" "D")
  (append-series :bybit "LTCUSD" "D")
  (append-series :kibot "SIL0" "D")
  (append-series :kibot "EURUSD" "D")

  ; symbol list
  (import-symbols :kibot ["SIL0" "NG0" "IBM"] "D" :full {})
  (import-symbols :kibot "joseph" "D" :full {})
  (import-symbols :kibot "futures-kibot" "D" :full {})
  (import-symbols :bybit "crypto" "D" :full {}) 


  (import-list "crypto" "D" :append)
  (import-list "joseph" "D" :append)
  (import-list ["SIL0" "NG0" "IBM"] "D" :append)

  (import-list :all "D" :append)
  
  ;
  )
