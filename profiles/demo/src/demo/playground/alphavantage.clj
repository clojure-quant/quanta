(ns demo.playground.alphavantage
  (:require
   [taoensso.timbre :refer [trace debug info infof error]]
   [clojure.pprint :refer [print-table]]
   [tech.v3.dataset :as tds]
   [ta.data.alphavantage :as av]
   [ta.warehouse :as wh]
   ;[demo.env.warehouse :refer [w]]
   ))
; select search
(av/search "S&P 500")
(print-table [:symbol :type :name] (av/search "BA"))
(print-table (av/search "Fidelity MSCI"))

;; # stock series
(av/get-daily :compact "MSFT")
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



