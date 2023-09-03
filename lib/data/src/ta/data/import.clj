(ns ta.data.import
   (:require
     [taoensso.timbre :refer [info warn error]]
     [tablecloth.api :as tc]
     [ta.warehouse :as wh]
     [ta.data.settings :refer [determine-wh]]
     [ta.warehouse.symbollist :refer [load-list]]
     ; providers
     [ta.data.api-ds.kibot :as kibot]
     [ta.data.api-ds.alphavantage :as av]
     [ta.data.api-ds.bybit :as bybit]
    ))

(def dict-provider
  {:kibot kibot/get-series
   :alphavantage av/get-series
   :bybit bybit/get-series
   })


(defn import-series [provider symbol interval range opts]
  (info "import provider: " provider " symbol: " symbol " range: " range)
  (let [get-series (get dict-provider provider)
        series-ds (get-series symbol interval range opts)
        c  (tc/row-count series-ds)
        w (determine-wh symbol)]
    (info "imported " symbol " - " c "bars.")
    (when (> c 0)
      (wh/save-symbol w series-ds interval symbol))))


(defn import-symbols [provider symbols interval range opts]
  (let [symbols (if (string? symbols) 
                    (load-list symbols)
                    symbols)]
  (doall (map
          #(import-series provider % interval range opts)
          symbols))))

(comment 
  ;; ALPHAVANTAGE
  ; stored in :stocks warehouse
  (import-series :alphavantage "MSFT" "D" :full {})
  ; stored in :fx warehouse
  (import-series :alphavantage "EURUSD" "D" :full {}) 
  ; gets stored in :crypto warehouse
  (import-series :alphavantage "BTCUSD" "D" :full {})
  
  ;; KIBOT
  (import-series :kibot "SIL0" "D" :full {})
  (import-symbols :kibot ["SIL0" "NG0" "IBM"] "D" :full {})
  (import-symbols :kibot "joseph" "D" :full {})
  (import-symbols :kibot "futures-kibot" "D" :full {})

  ;; BYBIT
  (import-symbols :bybit "crypto" "D" :full {})

  ;
  )
