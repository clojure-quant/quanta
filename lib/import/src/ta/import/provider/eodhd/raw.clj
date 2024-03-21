(ns ta.import.provider.eodhd.raw
  (:require
   [clojure.string :as str]
   [clojure.set]
   [taoensso.timbre :refer [info warn error]]
   [clojure.edn :as edn]
   [cheshire.core :as cheshire] ; JSON Encoding
   [de.otto.nom.core :as nom]
   [ta.import.helper :refer [str->float http-get]]
   [throttler.core]))

;; https://eodhd.com/pricing all world EOD 20 USD/month.
;; https://eodhd.com
;; https://eodhd.com/financial-apis/api-for-historical-data-and-volumes/
;; https://eodhd.com/financial-apis/exchanges-api-list-of-tickers-and-trading-hours/
;; https://eodhd.com/financial-apis/bulk-api-eod-splits-dividends/
;; nice api and entire-market download option.
;; 15 min delayed stock prices entire market. Us stocks + fx in realtime.
;; With End-of-Day data API, we have data for more than 150 000 tickers all around the 
;; world. We cover all US stocks, ETFs, and Mutual Funds (more than 51 000 in total) 
;; from the beginning, for example, the Ford Motors data is from Jun 1972 and so on. 
;; And non-US stock exchanges we cover mostly from Jan 3, 2000.
;; FREE SUBSCRIPTIONS HAVE ACCESS TO 1 YEAR OF EOD DATA.

(def base-url "https://eodhd.com/api/")

(defn make-request [api-token endpoint query-params]
  (nom/let-nom> [query-params (assoc query-params
                                     :api_token api-token
                                     :fmt "json")
                 result (http-get (str base-url endpoint) query-params)
                 _ (warn "eodhd response status: " (:status result))
                 _ (warn "eodhd response: "  result)
                 body-json (:body result)
                 body (cheshire/parse-string body-json true)]
                body))

(defn get-bars [api-token asset start-str end-str]
  (warn "getting bars asset: " asset "from: " start-str " to: " end-str)
  (let [endpoint (str "eod/" asset)]
    (make-request
     api-token
     endpoint {:order "a"
               :period "d"
               :from start-str
               :to end-str})))

(defn get-exchanges [api-token]
  (make-request api-token "exchanges-list/" {}))

(defn get-exchange-tickers [api-token exchange-code]
  (make-request api-token (str "exchange-symbol-list/" exchange-code) {}))

(comment
  (def d (get-bars "65f0ad82c56400.56029279"
                   "MCD.US"
                   "2024-01-01"
                   "2024-03-15"))

  d
;  
  )
