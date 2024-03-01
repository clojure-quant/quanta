(ns ta.quote.fix
  (:require
   [taoensso.timbre :as timbre :refer [info warn error]]
   [fix-engine.api.core :as fix-api]
   [fix-engine.core :as fix]
   [ta.quote.core :refer [quotefeed create-stream! publish! get-stream connect]]))

(defn fix-quote->quote
  "converts a fix-quote message to quote-format"
  [msg]
  ; {:msg-type :quote-data-full, 
  ;  :symbol EUR/JPY, :md-number-entries 2, :md-sub-entry-type 1, 
  ;  :md-entry-price 154.097}
  {:asset (:symbol msg)
   :price (:md-entry-price msg)
   :size 100.0 ; double so that we are compatible with crypto volumes that are double
   })

(defrecord quotefeed-fix [opts state]
  quotefeed
  (connect [this]
    (info "fix connect: " (:opts this))
    (let [client (fix-api/connect (:opts this))
          publish-quote! (fn [fix-msg]
                           (let [quote (fix-quote->quote fix-msg)]
                             (publish! this quote)))]
      (swap! (:state this) assoc :client client)
      (fix-api/on-quote client publish-quote!)))
  (disconnect [this]
    (let [{:keys [client] :as state} @(:state @this)]
      (info "fix disconnect: " (:opts this))
      (fix/end-session client)
      (swap! (:state this) dissoc :client)))
  (subscribe [this asset]
    (let [{:keys [client]} @(:state this)]
      (info "fix subscribe: " asset "fix-client: " client)
      (fix-api/subscribe client {:symbol asset})))
  (unsubscribe [this asset]
    (info "fix unsubscribe [NOT IMPLEMENTED]: " asset))
  (quote-stream [this]
    (get-stream this)))

(defn create-quotefeed-fix [opts]
  (info "creating FIX FEED: " opts)
  (let [feed (quotefeed-fix. opts (atom {}))]
    (create-stream! feed)
    feed))

(defn start-fix-feed-autoconnect [opts]
  (let [feed (create-quotefeed-fix opts)]
    (connect feed)
    feed))

(comment

  ;; in demo see: notebook.live.fix-quotes

  (require '[ta.quote.core :refer [connect disconnect subscribe quote-stream publish!]])

  ; fix-engine test
  (def fix-client (fix-api/connect :ctrader-tradeviewmarkets-quote))
  fix-client

  (fix-api/on-quote fix-client print-quote)
  (fix-api/subscribe fix-client {:symbol "USD/JPY"})
  (fix-api/subscribe fix-client {:symbol "EUR/USD"})
  (fix-api/subscribe fix-client {:symbol "USD/SEK"})

  (get-in fix-client [:client :id])
  ;; => :demo.tradeviewmarkets.3152195-CSERVER

  (fix/end-session (get-in fix-client [:client :id]))

  (fix/get-session :ctrader-tradeviewmarkets-quote)
  (fix/get-session :demo.tradeviewmarkets.3152195-CSERVER)
 ; 
  )