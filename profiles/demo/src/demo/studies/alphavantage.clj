;;; # AlphaVantage data feed
;;; 
;;; Alphavantage supports 5 requests a minute / 500 requests a day.

(ns demo.studies.alphavantage
  (:require
   [clojure.pprint :refer :all]
   [pinkgorilla.notebook.repl :refer [secret load-edn-resource]]
   [ta.data.alphavantage :refer [set-key! search get-daily get-daily-fx get-daily-crypto get-crypto-rating]]
   ;:reload-all
   ))

; screts should not be saved in a notebook
;
; secret loads a key from user defined secrets.
; the current implementation does just read the file test/creds.edn
; in the future the notebook will save creds only in webbrowser local storage
(set-key! (secret :alphavantage))

(clojure.pprint/print-table [:symbol :type :name] (search "BA"))

(clojure.pprint/print-table (take 5 (reverse (get-daily :compact "MSFT"))))

(clojure.pprint/print-table (take 5 (reverse (get-daily-fx :compact "EURUSD"))))

;(def symbols ["BTC" "ETH" "LTC" "DASH" "NANO" "EOS" "XLM"])
(clojure.pprint/print-table (take 5 (reverse (get-daily-crypto :compact "BTC"))))

(clojure.pprint/print-table (search "Fidelity MSCI"))

(clojure.pprint/print-table (load-edn-resource "ta/fidelity-select.edn"))

(get-crypto-rating "BTC")

; since we can only do 5 requests a minute, and we have 7 symbols, this
; will at least sleep for 1 minutes, after getting the first 5 symbols. However since before 
; we also execute requests, it might take 2 minutes
(clojure.pprint/print-table
 (map get-crypto-rating ["BTC" "ETH" "LTC" "DASH" "NANO" "EOS" "XLM"]))



