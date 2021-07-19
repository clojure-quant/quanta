;; # AlphaVantage data feed
;; 
;; Alphavantage supports 5 requests a minute / 500 requests a day.

(ns demo.datasource.alphavantage
  (:require
   [clojure.edn :as edn]
   [clojure.pprint :refer  [print-table]]
   [pinkgorilla.notebook.repl :refer [secret load-edn-resource]]
   [ta.data.alphavantage :as av]
   ;:reload-all
   ))

; screts should not be saved in a notebook
;
; secret loads a key from user defined secrets.
; the current implementation does just read the file test/creds.edn
; in the future the notebook will save creds only in webbrowser local storage
(av/set-key! (secret :alphavantage))

(-> "creds.edn"
    slurp
    edn/read-string
    :alphavantage
    av/set-key!)

(av/search "S&P 500")
(print-table [:symbol :type :name] (av/search "BA"))

;; # stock series

(av/get-daily :compact "MSFT")
(print-table (->> (av/get-daily :compact "MSFT")
                  reverse
                  (take 5)))

;; # fx series

(print-table (take 5 (reverse (av/get-daily-fx :compact "EURUSD"))))

;; # crypto series

(print-table (take 5 (reverse (av/get-daily-crypto :compact "BTC"))))

(av/get-crypto-rating "BTC")

; since we can only do 5 requests a minute, and we have 7 symbols, this
; will at least sleep for 1 minutes, after getting the first 5 symbols. However since before 
; we also execute requests, it might take 2 minutes
(clojure.pprint/print-table
 (map av/get-crypto-rating ["BTC" "ETH" "LTC" "DASH"
                            "NANO" "EOS" "XLM"]))

;; # fidelity select search

(clojure.pprint/print-table (av/search "Fidelity MSCI"))

(clojure.pprint/print-table (load-edn-resource "ta/fidelity-select.edn"))






