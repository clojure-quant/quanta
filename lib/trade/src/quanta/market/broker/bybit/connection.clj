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
  (j/read-value json j/keyword-keys-object-mapper))

(defn msg-flow [!-a]
  (m/observe
   (fn [!]
     (info "creating msg-flow reader..")
     (reset! !-a !)
     (fn []
       (info "removing msg-flow reader..")
       (reset! !-a nil)))))

;(future-cancel ping-sender)

(defn connection-start! [opts]
  (info "connection-start..")
  (let [stream (connect! opts)
        !-a (atom nil)
        on-msg (fn [json]
                 (let [msg (json->msg json)]
                   (info "!msg rcvd: " msg)
                   (when @!-a
                     (@!-a msg))))
        msg-flow (msg-flow !-a)]
    (s/consume on-msg stream)
    (info "connected!")
    {:account opts
     :api :bybit
     :stream stream
     :msg-flow msg-flow}))

(defn connection-stop! [{:keys [stream msg-flow]}]
   (info "connection-stop.. ")
  (.close stream)
  
  )

(defn send-msg! [{:keys [stream]} msg]
  (let [json (j/write-value-as-string msg)]
    (info "send-msg!: " json)
    (if stream 
       (s/put! stream json)  
       (error "send-msg! stream not found.")
      )
    ))

(defn send-msg-task [conn msg]
  (m/sp (send-msg! conn msg)))

(defn rpc-req! [conn msg]
  (m/sp
   (let [id (nano-id 8)
         p-reqId (fn [{:keys [reqId]}]
                   (= id reqId))
         result (first-match p-reqId (:msg-flow conn))
         msg (assoc msg :reqId id)]
     (info "making request current conn:" conn)
     (send-msg! conn msg)
     (info "waiting for result")
     (m/? result))))






(defn connection3 [opts]
   ; this returns a missionary flow 
  ; published events of this flow are connection-streams
  (m/observe
   (fn [!]
     (info "connecting..")
     (let [stream (connect! opts)
           !-a (atom nil)
           on-msg (fn [json]
                    (let [msg (json->msg json)]
                       (info "!msg rcvd: " msg)
                       (when @!-a
                         (@!-a msg))))
           msg-flow (msg-flow !-a)]
       (s/consume on-msg stream)
       (info "connected!")
       (! {:account opts
           :api :bybit
           :stream stream 
           :msg-flow msg-flow})
       (fn []
         (info "disconnecting.. stream " stream)
         (.close stream))))))




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