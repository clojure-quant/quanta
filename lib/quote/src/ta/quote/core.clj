(ns ta.quote.core
  "a quote feed outputs a quote-stream
   connectivty management via connect/disconnect
   subscription managment via subscribe/unsubscribe"
  (:require [manifold.stream :as s]))

(defprotocol quotefeed
  (connect [this])
  (disconnect [this])
  (subscribe [this asset])
  (unsubscribe [this asset])
  (quote-stream [this]))

(defn create-stream! [this]
  (let [quote-stream (s/stream)]
    (swap! (:state this) assoc :quote-stream quote-stream)))

(defn get-stream [this]
  (-> this :state deref :quote-stream))

(defn publish! [this quote]
  (let [quote-stream (get-stream this)]
    (s/put! quote-stream quote)))

(comment
  (def this {:state (atom {})})
  (create-stream! this)

  (get-stream this)

  (publish! this {:price 100.0 :qty 1})

  (s/take! (get-stream this))

; 
  )








