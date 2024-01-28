(ns ta.quote.binance
  "binance quote feed"
  (:require
   [ta.quote.core :refer [quotefeed create-stream! publish! get-stream]]
   [aleph.http :as http]
   [manifold.stream :as s]
   [cheshire.core :refer [parse-string generate-string]]))


#_(def ws-url {:live "wss://ws-api.binance.com:443/ws-api/v3"
               :test "wss://testnet.binance.vision/ws-api/v3"})