(ns notebook.live.quotefeed-fix
  (:require
    [taoensso.timbre :as timbre :refer [info warn error]]
    [manifold.stream :as s]
    [ta.quote.fix :refer [create-quotefeed-fix]]
    [ta.quote.core :refer [connect disconnect subscribe quote-stream publish!]]))


;; 1. create quote-feed

(def feed (create-quotefeed-fix :ctrader-tradeviewmarkets-quote))
feed
@(:state feed)
(connect feed)
(quote-stream feed)

;; 2. create consumer , and send a test quote to consumer.
  
(defn print-quote [quote]
  (info "quote received: " quote))

(s/consume print-quote (quote-stream feed))

(publish! feed {:asset "QQQ" :price 120.99 :qty 100})

;; 3. subscribe to quotes
(subscribe feed "EUR/USD")
(subscribe feed "USD/JPY")
(subscribe feed "USD/SEK")  
  
;; 4. look to console to see printed messages.  


;; 5. shutdown.
(disconnect feed)
  