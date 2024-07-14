(ns quanta.market.connection.bybit
  "bybit quote feed"
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [nano-id.core :refer [nano-id]]
   [missionary.core :as m]
   [aleph.http :as http]
   [manifold.stream :as s]
   [cheshire.core :refer [parse-string generate-string]]))

;; https://bybit-exchange.github.io/docs/v5/ws/connect

(def ws-url {:live "wss://stream.bybit.com/v5/public/spot"
             :test "wss://stream-testnet.bybit.com/v5/public/spot"})

(defn send-msg! [stream msg]
  (let [req-id (nano-id 8)
        msg (assoc msg "req_id" req-id)
        json (generate-string msg)]
    (info "sending: " json)
    (s/put! stream json)))

(defn set-interval [callback ms]
  (future (while true (do (Thread/sleep ms) (callback)))))

(defn gen-ping-sender [msg-stream]
  (fn []
    (debug "sending bybit ping..")
    (send-msg! msg-stream {"op" "ping"})))

(defn connect-live []
  (info "bybit connect ")
  (let [client @(http/websocket-client (:live ws-url))
        ;f (set-interval (gen-ping-sender client) 5000)
        ]
    {:msg-stream client
     ;:ping-sender f
     }))

(defn send! [conn msg]
  (let [msg-stream (:msg-stream conn)]
    (send-msg! msg-stream msg)))

(defn subscription-msg [asset]
  {"op" "subscribe"
   "args" [(str "publicTrade." asset)]})


(def bybit-websocket-live
  ; this returns a missionary flow 
  ; published events of this flow are connection-streams
  (m/observe
   (fn [!]
     (info "connecting..")
     (let [{:keys [msg-stream ping-sender] :as new-state} (connect-live)]
       (! msg-stream)
       (fn []
         (info "disconnecting..")
         (.close msg-stream)
         ;(future-cancel ping-sender)
         )))))

(defn bybit-msg [msg-stream]
  (m/observe
   (fn [!]
     (info "bybit websocket reader create..")
     (let [!json (fn [msg]
                   (! (parse-string msg true)))]
       (s/consume !json msg-stream)
       (fn []
         (println "bybit reader disconnecting.."))))))

;; QUOTE 

(defn bybit-data->tick [{:keys [s p v T]}]
  {:asset s
   :price (parse-double p)
   :size (parse-double v)})

;({:asset BTCUSDT, :price 60007.77, :size 9.9E-5}
; {:asset BTCUSDT, :price 60007.94, :size 0.003858}
; {:asset BTCUSDT, :price 60008.02, :size 0.001843})
;({:asset BTCUSDT, :price 60008.97, :size 0.0049})

(defn extract-quotes [{:keys [type data] :as full-msg}]
  (when (= type "snapshot")
    (info "full snapshot: " data)
    (map bybit-data->tick data)))

(defn get-quote [asset]
  (m/ap
   (info "get-quote: " asset " waiting for stream..")
   (let [stream (m/?> bybit-websocket-live)]
     (if stream
       (do
         (info "subscribing for asset: " asset)
         (send-msg! stream (subscription-msg asset))
         (let [msg (m/?> (bybit-msg stream))]
           (extract-quotes msg)
           ;msg
           ))
       (do
         (error "stream is not there.")
         {:msg :connecting})))))

(comment
  ;raw websocket testing:
  
  (def c (connect-live))
  (s/consume println (:msg-stream c))
  (send! c (subscription-msg "BTCUSDT"))
  c
  (.close (:msg-stream c))

  ; quote-view 
  (m/? (m/reduce println nil (get-quote "BTCUSDT")))

  
(defn cont [flow]
  (->> flow 
       (m/reductions (fn [r v] v) nil)
       (m/relieve {})))

  (let [assets ["BTCUSDT" "ETHUSDT"]
        quotes (map get-quote assets)
        quotes-cont (map cont quotes)
        last-quotes (apply m/latest vector quotes-cont)]
    (m/? 
     (m/reduce println nil last-quotes)))
; 
    )
