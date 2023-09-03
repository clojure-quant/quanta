(ns ta.data.import
   (:require
     [taoensso.timbre :refer [info warn error]]
     [tablecloth.api :as tc]
     [ta.data.api-ds.kibot :as kibot]
     [ta.data.api-ds.alphavantage :as av]
     [ta.warehouse :as wh]
     [ta.data.settings :refer [determine-wh]]))


(def dict-provider
  {:kibot kibot/get-series
   :alphavantage av/get-series
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



#_(defn init-symbols [w fn-get-history-since-as-ds frequency since symbols]
  (doall (map
          (partial init-symbol w fn-get-history-since-as-ds frequency since)
          symbols)))

(comment 
  (import-series :alphavantage "MSFT" "D" :full {})
  (import-series :alphavantage "EURUSD" "D" :full {})
  (import-series :alphavantage "BTCUSD" "D" :full {})
  
  
  ;
  )
