(ns demo.alphavantage
   (:require
   [clojure.edn :as edn]
   [ta.data.alphavantage :as av]
  ))


(-> "../creds.edn" slurp edn/read-string
    :alphavantage av/set-key!)
@av/api-key

(av/search "S&P 500")
(av/search "BA")


(av/get-daily :compact "MSFT")
(av/get-daily-fx :compact "EURUSD")
(av/get-daily-crypto :compact "BTC")

(av/get-crypto-rating "BTC")



(map av/get-crypto-rating ["BTC" "ETH" "LTC" "DASH" 
                        "NANO" "EOS" "XLM"])
