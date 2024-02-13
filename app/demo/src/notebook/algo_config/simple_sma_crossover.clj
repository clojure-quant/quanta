(ns notebook.algo-config.simple-sma-crossover
  (:require
   [ta.algo.permutate :refer [->assets]]))

(def base {:algo-ns 'notebook.algo.sma3
           :label :sma-crossover-1m
           :bar-category [:us :m]
           :calendar [:us :m] ; hack for dsl-javelin
           :asset "EUR/USD"
           :feed :fx
           :trailing-n 5
           :sma-length-st 2
           :sma-length-lt 3})


(def fx-assets
  ["EUR/USD" "GBP/USD" "EUR/JPY"
   "USD/JPY" "AUD/USD" "USD/CHF"
   "GBP/JPY" "USD/CAD" "EUR/GBP"
   "EUR/CHF" "NZD/USD" "USD/NOK"
   "USD/ZAR" "USD/SEK" "USD/MXN"])

(def algos-fx (->assets base fx-assets))


(def crypto-assets 
  ["BTCUSDT" "ETHUSDT"])

(def algos-crypto (->assets (assoc base :feed :crypto) crypto-assets))

(comment
  
  algos-fx
  algos-crypto

  (require '[modular.system])

  (def live (:live modular.system/system))
  live

  (require '[ta.env.dsl.barstrategy :as dsl])

  (dsl/add-bar-strategies live algos-fx)




 ; 
  )

