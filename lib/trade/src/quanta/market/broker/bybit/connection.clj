(ns quanta.market.broker.bybit.connection
  "bybit quote feed"
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [missionary.core :as m]
   [nano-id.core :refer [nano-id]]
   [jsonista.core :as j] ; json read/write
   [aleph.http :as http]
   [manifold.stream :as s] ; websocket to bybit
   [quanta.market.broker.util :refer [first-match next-value always]]
   [quanta.market.protocol :refer [connection]]))

;; https://bybit-exchange.github.io/docs/v5/ws/connect

(def websocket-destination-urls
  {:main {:spot "wss://stream.bybit.com/v5/public/spot"
          :future "wss://stream.bybit.com/v5/public/linear" ; USDT, USDC perpetual & USDC Futures
          :inverse "wss://stream.bybit.com/v5/public/inverse"
          :option "wss://stream.bybit.com/v5/public/option" ; USDC Option
          :trade "wss://stream.bybit.com/v5/trade"
          :private "wss://stream.bybit.com/v5/private"}
   :test {:spot "wss://stream-testnet.bybit.com/v5/public/spot"
          :future "wss://stream-testnet.bybit.com/v5/public/linear" ; USDT, USDC perpetual & USDC Futures
          :inverse "wss://stream-testnet.bybit.com/v5/public/inverse"
          :option "wss://stream-testnet.bybit.com/v5/public/option" ; USDC Option
          :trade "wss://stream-testnet.bybit.com/v5/trade"
          :private "wss://stream-testnet.bybit.com/v5/private"}})

(defn get-ws-url [mode destination]
  (get-in websocket-destination-urls [mode destination]))

(defn connect! [{:keys [mode segment]}]
  (let [url (get-ws-url mode segment)
        _ (info "bybit connect mode: " mode " segment: " segment " url: "  url)
        client @(http/websocket-client url)
        ;f (set-interval (gen-ping-sender client) 5000)
        ]
    (info "bybit connected!")
    client))

(defn json->msg [json]
  (let [msg (j/read-value json j/keyword-keys-object-mapper)]
    (info "msg rcvd: " msg)
    msg))

(defn msg-flow [stream]
  (m/observe
   (fn [!]
     (info "creating msg-flow reader..")
     (let [msg-flow (s/consume #(! (json->msg %)) stream)]
       (fn []
         (info "removing msg-flow reader.."))))))

;(future-cancel ping-sender)

(defn connection3 [opts]
   ; this returns a missionary flow 
  ; published events of this flow are connection-streams
  (m/observe
   (fn [!]
     (info "connecting..")
     (let [stream (connect! opts)
           msg-flow (msg-flow stream)]
       (info "connected!")
       (! {:account opts
           :api :bybit
           :stream stream 
           :msg-flow msg-flow})
       (fn []
         (info "disconnecting.. stream " stream)
         (.close stream))))))

(defn send-msg! [{:keys [stream]} msg]
  (let [json (j/write-value-as-string msg)]
    (info "sending: " json)
    (s/put! stream json)))

(defn set-interval [callback ms]
  (future (while true (do (Thread/sleep ms) (callback)))))

(defn gen-ping-sender [msg-stream]
  (fn []
    (debug "sending bybit ping..")
    (send-msg! msg-stream {"op" "ping"})))

(defn get-and-keep-connection [conn ms]
  (m/ap
   (let [c (m/? (next-value (:msg-flow conn)))]
     (m/amb=
      c
      (m/? (m/sleep ms c))))))

(comment
   ;raw websocket testing:

  (def c (connect! {:mode :main
                    :segment :spot}))
  (s/consume println c)
  (send-msg-simple! c (subscription-msg "BTCUSDT"))
  c
  (.close c)

;  
  )