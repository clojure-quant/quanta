(ns ta.quote.quote-manager
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [manifold.stream :as s]
   [ta.quote.core :as quote]
   [ta.quote.last-msg-summary :as summary]))

(defn create-quote-manager
  "creates a quote-manager. 
   input: feeds (each with their own stream)
   - subscribe/unsubscribe to :asset/:feed tuple
   - reads all messages from feed :quote-stream, adds :feed
     property to all messages and outputs in ONE output
     :quotestream
   - quote snapshot always has most recent quotes"
  [feeds]
  (let [output-quote-stream (s/stream)
        ;feed-handler (vals feeds)
        ;feed-streams (map quote/quote-stream feed-handler)
        ]
    (doall
     ;(map #(s/connect % output-quote-stream) feed-streams))
     (map (fn [[id feed]]
            (s/consume
             (fn [quote]
               ; @ should prevent dropping quotes.
               @(s/put! output-quote-stream
                        (assoc quote :feed id)))
             (quote/quote-stream feed))) feeds))
    {:feeds feeds
     :quote-stream output-quote-stream
     :summary (summary/create-last-summary output-quote-stream :asset)}))

(defn get-quote-stream [state]
  (:quote-stream state))

(defn get-feed [this feed]
  (get (:feeds this) feed))

(defn quote-snapshot [this]
  (summary/current-summary (:summary this)))

(defn subscribe [this {:keys [asset feed]}]
  (if (and asset feed)
    (let [f (get-feed this feed)]
      (info "subscribing asset [" asset "] @ feed [" feed "] ..")
      (quote/subscribe f asset))
    (warn "cannot subscribe. subscribe needs asset and feed!")))

(defn unsubscribe [this {:keys [asset feed]}]
  (if (and asset feed)
    (let [f (get-feed this feed)]
      (info "un-subscribing asset [" asset "] @ feed [" feed "] ..")
      (quote/unsubscribe f asset))
    (warn "cannot un-subscribe. subscribe needs asset and feed!")))