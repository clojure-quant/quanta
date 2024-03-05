(ns notebook.strategy.sma-crossover.spec
  (:require
   [ta.algo.permutate :refer [->assets]]))

(def sma-spec {:type :trailing-bar
               :algo 'notebook.strategy.sma-crossover.algo/bar-strategy
               :calendar [:us :m] ; hack for dsl-javelin
               :asset "EUR/USD"
               :feed :fx
               :trailing-n 1000
               :sma-length-st 10
               :sma-length-lt 59})

(def fx-assets
  ["EUR/USD" "GBP/USD" "EUR/JPY"
   "USD/JPY" "AUD/USD" "USD/CHF"
   "GBP/JPY" "USD/CAD" "EUR/GBP"
   "EUR/CHF" "NZD/USD" "USD/NOK"
   "USD/ZAR" "USD/SEK" "USD/MXN"])

(def algos-fx (->assets sma-spec fx-assets))


(def crypto-assets
  ["BTCUSDT" "ETHUSDT"])

(def algos-crypto (->assets (assoc sma-spec :feed :crypto) crypto-assets))
