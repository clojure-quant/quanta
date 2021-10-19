(ns demo.playground.alphavantage
  (:require
   [clojure.pprint :refer [print-table]]
   [ta.data.alphavantage :as av]))

; select search
(av/search "S&P 500")
(print-table [:symbol :type :name] (av/search "BA"))
(print-table (av/search "Fidelity MSCI"))

(av/search "gld")

;; # stock series
(av/get-daily :compact "QQQ")
(print-table (->> (av/get-daily :compact "MSFT")
                  reverse
                  (take 5)))

;; # fx series
(print-table (take 5 (reverse (av/get-daily-fx :compact "EURUSD"))))

;; # crypto series
(print-table (take 5 (reverse (av/get-daily-crypto :compact "BTC"))))

; crypto rating
(av/get-crypto-rating "BTC")

(print-table
 (map av/get-crypto-rating ["BTC" "ETH" "LTC" "DASH"
                            "NANO" "EOS" "XLM"]))



