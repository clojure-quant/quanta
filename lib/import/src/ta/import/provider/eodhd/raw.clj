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

(def base-url "https://eodhd.com/api/")

(defn make-request [api-token endpoint query-params]
  (nom/let-nom> [query-params (assoc query-params
                                     :api_token api-token
                                     :fmt "json")
                 result (http-get (str base-url endpoint) query-params)
                 body-json (:body result)
                 body (cheshire/parse-string body-json true)
                 ;kibot-error (extract-error body)
                 ]
     ;(info "kibot response status: " (:status result))           
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



