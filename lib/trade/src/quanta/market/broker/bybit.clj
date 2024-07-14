(ns quanta.market.broker.bybit
  "bybit quote feed"
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [nano-id.core :refer [nano-id]]
   [missionary.core :as m]
   [aleph.http :as http]
   [manifold.stream :as s]
   [cheshire.core :refer [parse-string generate-string]]
   [buddy.core.codecs :as crypto.codecs]
   [buddy.core.mac :as crypto.mac]
   [quanta.market.protocol :refer [connection get-quote]]))

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

(defn- sign
  [to-sign key-secret]
  (-> (crypto.mac/hash to-sign {:key key-secret :alg :hmac+sha256})
      (crypto.codecs/bytes->hex)))

(defn auth-msg [{:keys [api-key api-secret]}]
  (let [millis (System/currentTimeMillis)
        expires (+ millis (* 1000 60 5)) ; 5 min
        to-sign (str "GET/realtime" expires)
        signature (sign to-sign api-secret)]
    {"op" "auth"
     "args" [api-key
             expires
             signature]}))

; orderbook responses: type: snapshot,delta
(defn send-msg-simple! [stream msg]
  (let [json (generate-string msg)]
    (info "sending: " json)
    (s/put! stream json)))

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

(defn connect! [{:keys [mode segment]}]
  (let [url (get-ws-url mode segment)
        _ (info "bybit connect mode: " mode " segment: " segment " url: "  url)
        client @(http/websocket-client url)
        ;f (set-interval (gen-ping-sender client) 5000)
        ]
    (info "bybit connected!")
    client))


(defn subscription-msg [asset]
  {"op" "subscribe"
   "args" [(str "publicTrade." asset)]})

(defmethod connection :bybit 
  [opts] 
  ; this returns a missionary flow 
  ; published events of this flow are connection-streams
  (m/observe
   (fn [!]
     (info "connecting..")
     (let [stream (connect! opts)]
       (! stream)
       (fn []
         (info "disconnecting..")
         (.close stream)
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

(defmethod get-quote :bybit
  [type connection asset]
  (m/ap
   (info "get-quote: " asset " waiting for stream..")
   (let [stream (m/?> connection)]
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
         {:error :connecting 
          :msg "stream is not there.. connection err?"})))))



(defn create-order-msg [{:keys [asset side qty limit]}]
  {"op" "order.create"
   "header" {"X-BAPI-TIMESTAMP" (System/currentTimeMillis)
             "X-BAPI-RECV-WINDOW" "8000"
             "Referer" "bot-001" ; for api broker
             }
   "args" [{"symbol" asset
            "side" (case side
                     :long "Buy"
                     :buy "Buy"
                     :short "Sell"
                     :sell :Sell)
            "orderType" "Limit"
            "qty" qty
            "price" limit
            "category" "linear"
            "timeInForce" "PostOnly"}]})

(comment
  ;raw websocket testing:

  (def c (connect! {:mode :main
                    :segment :spot}))
  (s/consume println c)
  (send-msg-simple! c (subscription-msg "BTCUSDT"))
  c
  (.close c)

  ; quote-view 
  (def conn (connection {:type :bybit
                         :mode :main
                         :segment :spot}))

  (m/? (m/reduce println nil (get-quote :bybit conn "BTCUSDT")))

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

; auth
  (require '[clojure.edn :refer [read-string]])
  (def creds
    (-> (System/getenv "MYVAULT")
        (str "/goldly/quanta.edn")
        slurp
        read-string
        :bybit/test))
  creds
  (auth-msg creds)

  (def c (connect :test :trade))
  (s/consume println c)
  (send-msg-simple! c (auth-msg creds))
  (send-msg-simple! c (create-order-msg {:asset "ETHUSDT"
                                         :side :buy
                                         :qty "0.01"
                                         :limit "1000.0"}))

; 
  )
