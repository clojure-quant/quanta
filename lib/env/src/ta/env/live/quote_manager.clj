(ns ta.live.quote-manager
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [manifold.stream :as s]
   [ta.quote.core :as quote]
   [ta.env.tools.last-msg-summary :as summary]))

(defn create-quote-manager [feeds]
  (let [global-quote-stream (s/stream)
        feed-handler (vals feeds)
        feed-streams (map quote/quote-stream feed-handler)]
    (doall
     (map #(s/connect % global-quote-stream) feed-streams))
    {:feeds feeds
     :global-quote-stream global-quote-stream
     :summary-quote (summary/create-last-summary global-quote-stream :asset)}))

(defn get-feed [state feed]
  (get (:feed state) feed))

(defn quote-snapshot [state]
  (summary/current-summary (:summary-quote state)))

(defn subscribe [state {:keys [asset feed]}]
 (if (and asset feed)
  (let [f (get-feed state feed)]
    (info "added algo with asset [" asset "] .. subscribing with feed " feed " ..")
    (quote/subscribe f asset))
  (warn "added algo without asset .. not subscribing!")))