(ns quanta.market.broker.bybit.task.order
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [missionary.core :as m]
   [quanta.market.broker.bybit.connection :as c]
   [quanta.market.broker.bybit.task.auth :refer [authenticate!]]))

; orderbook responses: type: snapshot,delta

(defn order-create-msg [{:keys [asset side qty limit]}]
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

(def order-response-failed-example
  {:retCode 110007,
   :retMsg "ab not enough for new order",
   :connId "cpv85t788smd5eps8ncg-2tgm",
   :op "order.create",
   :header {:Timenow 1721152638764,
            :X-Bapi-Limit-Status 9,
            :X-Bapi-Limit-Reset-Timestamp 1721152638762,
            :Traceid "2bc167807ee719a474416104ae7e964b",
            :X-Bapi-Limit 10},
   :reqId "5bY1PVT-"
   :data {}})

(def order-response-failed-example2
{:retCode 10005,
 :retMsg "Permission denied for current apikey",
 :connId "cpv86i6c0hvd5nkl25n0-2v79",
 :op "order.create",
 :reqId "XiMTlhQV"})

(defn order-create! [conn order]
  (let [msg (order-create-msg order)]
    (info "order-create: " order " ..")
    (c/rpc-req! conn msg)))

(defn order-cancel-msg [{:keys [asset order-id]}]
  {"op" "order.cancel"
   "header" {"X-BAPI-TIMESTAMP" (System/currentTimeMillis)
             "X-BAPI-RECV-WINDOW" "8000"
             "Referer" "bot-001" ; for api broker
             }
   "args" [{"category" "linear" ; product type: spot, linear, inverse, option
            "symbol" asset
            ;"orderId" order-id ; bybit order id
            "orderLinkId" order-id ; user order id
            }]})

(def order-cancel-failed-example 
{:retCode 10005,
 :retMsg "Permission denied for current apikey",
 :connId "cpv86i6c0hvd5nkl25n0-2v7a",
 :op "order.cancel",
 :reqId "XMrZJoEs"}  
  )
(def order-cancel-failed-example2
{:retCode 110001,
 :retMsg "order not exists or too late to cancel",
 :connId "cpv86i6c0hvd5nkl25n0-2v7a",
 :op "order.cancel",
 :header
 {:Timenow "1721243099025",
  :X-Bapi-Limit-Status "9",
  :X-Bapi-Limit-Reset-Timestamp "1721243099024",
  :Traceid "0313d8af73592c2034d873ef1f6479ac",
  :X-Bapi-Limit "10"},
 :reqId "-Cquvtz7",
 :data {}})



(defn order-cancel! [conn order]
  (let [msg (order-cancel-msg order)]
    (info "order-cancel: " order " ..")
    (c/rpc-req! conn msg)))

(comment
  (require '[clojure.edn :refer [read-string]])
  (def creds
    (-> (System/getenv "MYVAULT")
        (str "/goldly/quanta.edn")
        slurp
        read-string
        :bybit/test))

  (def account {:mode :test
                :segment :trade
                :account creds})

  account
  (def conn
    (c/connection-start! account))

  conn

  (c/info? conn)

  (def order
    {:asset "ETHUSDT"
     :side :buy
     :qty "0.01"
     :limit "1000.0"})

  (m/?  (authenticate! conn account))

  (m/?  (order-create! conn order))

  
 (def cancel
  {:asset "ETHUSDT"
   :order-id "my-id-007"})

   (m/? (order-cancel! conn cancel))
  

; 
  )
