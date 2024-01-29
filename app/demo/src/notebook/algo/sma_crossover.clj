(ns notebook.algo.sma-crossover
  (:require
   [ta.algo.permutate :refer [->assets]]))

(def base {:algo-ns 'demo.algo.sma3
           :bar-category [:us :m]
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

  (require '[ta.env.live-bargenerator :as env])

  (env/add-bar-strategies live algos-fx)




 ; 
  )

