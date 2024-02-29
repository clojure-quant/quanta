(ns ta.live.quote-manager
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [manifold.stream :as s]
   [ta.quote.core :as quote]
   [ta.env.tools.last-msg-summary :as summary]))

(defn create-quote-manager [feeds]
  (let [output-quote-stream (s/stream)
        ;feed-handler (vals feeds)
        ;feed-streams (map quote/quote-stream feed-handler)
        ]
    (doall
     ;(map #(s/connect % output-quote-stream) feed-streams))
     (map (fn [[id feed]]
            (s/consume  
              (fn [quote]
                 (s/put! output-quote-stream
                         (assoc quote :feed id)))
               (quote/quote-stream feed))) feeds))
    {:feeds feeds
     :quote-stream output-quote-stream
     :summary (summary/create-last-summary output-quote-stream :asset)}))

(defn get-quote-stream [state]
  (:quote-stream state))

(defn get-feed [state feed]
  (get (:feeds state) feed))

(defn quote-snapshot [state]
  (summary/current-summary (:summary state)))

(defn subscribe [state {:keys [asset feed]}]
  (if (and asset feed)
    (let [f (get-feed state feed)]
      (info "subscribing asset [" asset "] @ feed [" feed "] ..")
      (quote/subscribe f asset))
    (warn "cannot subscribe. subscribe needs asset and feed!")))

(defn unsubscribe [state {:keys [asset feed]}]
  (if (and asset feed)
    (let [f (get-feed state feed)]
      (info "un-subscribing asset [" asset "] @ feed [" feed "] ..")
      (quote/unsubscribe f asset))
    (warn "cannot un-subscribe. subscribe needs asset and feed!")))