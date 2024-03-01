(ns ta.quote.bybit
  "bybit quote feed"
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [ta.quote.core :refer [quotefeed create-stream! publish! get-stream connect]]
   [aleph.http :as http]
   [manifold.stream :as s]
   [cheshire.core :refer [parse-string generate-string]]))

;; https://bybit-exchange.github.io/docs/v5/ws/connect

(def ws-url {:live "wss://stream.bybit.com/v5/public/spot"
             :test "wss://stream-testnet.bybit.com/v5/public/spot"})

;; SENDING

(def req-nr (atom 1))

(defn send-msg! [stream msg]
  (let [msg (assoc msg "req_id" (swap! req-nr inc))
        json (generate-string msg)]
    (debug "sending: " json)
    (s/put! stream json)))

(defn send-ping! [stream]
  (send-msg! stream {"op" "ping"}))

(defn send-subscribe! [stream asset]
  (send-msg! stream {"op" "subscribe"
                     "args" [(str "publicTrade." asset)]}))

(defn set-interval [callback ms]
  (future (while true (do (Thread/sleep ms) (callback)))))

(defn gen-ping-sender [{:keys [state] :as this}]
  (fn []
    (debug "sending bybit ping..")
    (send-ping! (:client @state))))

;; RECEIVING

(defn bybit-data->tick [{:keys [s p v T]}]
  {:asset s
   :price (parse-double p)
   :size (parse-double v)})

(defn process-incoming-msg [stream-out msg]
  (let [{:keys [type data] :as full-msg} (parse-string msg true)]
    (if (= type "snapshot")
      (let [quotes-converted (map bybit-data->tick data)]
        (debug "quote(s) received:" data)
        (doall (map (partial s/put! stream-out) quotes-converted)))
      (debug "other msg received: " full-msg))))

(defrecord quotefeed-websocket-bybit [opts state]
  quotefeed
  (connect [this]
    (info "bybit connect: " (:opts this))
    (let [client @(http/websocket-client (:live ws-url))
          f (set-interval (gen-ping-sender this) 5000)]
      (swap! (:state this) assoc :f f :client client)
      (create-stream! this)
      (s/consume (partial process-incoming-msg (get-stream this)) client)))
  (disconnect [this]
    (info "bybit disconnect: " (:opts this))
    (info "state: " @(:state this))
    (future-cancel (:f @(:state this)))
    (swap! (:state this) assoc :symbols {})
    (swap! (:state this) dissoc :f))
  (subscribe [this asset]
    (info "bybit subscribe: " asset)
    (send-subscribe! (:client @state) asset))
  (unsubscribe [this asset]
    (info "bybit unsubscribe: " asset " NOT IMPLEMENTED"))
  (quote-stream [this]
    (get-stream this)))

(defn create-quotefeed-bybit [opts]
  (let [feed (quotefeed-websocket-bybit. opts (atom {:symbols {}}))]
    feed))

(defn start-quotefeed-bybit-autoconnect [opts]
  (let [feed (create-quotefeed-bybit opts)]
    (connect feed)
    feed))

(comment

  ;raw websocket testing:
  (def client
    @(http/websocket-client (:live ws-url)))
  client

  (def s-out (s/stream))

  (s/consume (partial process-incoming-msg s-out) client)
  (send-ping! client)
  (send-subscribe! client "ETHUSDT")

; quotefeed protocol
  (def feed (start-quotefeed-bybit-autoconnect {}))

  (require '[ta.quote.core :refer [quote-stream subscribe]])
  (s/consume (fn [tick]
               (info "bybit tick received: " tick))
             (quote-stream feed))

  (subscribe feed "ETHUSDT")
  (subscribe feed "BTCUSDT")

; 
  )

