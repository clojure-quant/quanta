(ns quanta.market.broker.bybit.order
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [missionary.core :as m]
   [quanta.market.broker.bybit.connection :refer [send-msg! bybit-msg]]
   [quanta.market.broker.bybit.secure-connection :refer [secure-connection rpc req]]))

; orderbook responses: type: snapshot,delta

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

(defn send-order [conn
                  {:keys [asset side qty limit] :as order}]
  (m/ap
   (if-let [c (m/?> conn)]
     (let [_ (info "send-order has a connection. sending order")
           order (send-msg! c (create-order-msg order))]
       (m/?> (:msg-flow conn))
       (info "done.")
       )
     (error "cannot send order .. no connection!"))))

(defn send-order3 [conn order]
  (req conn (create-order-msg order)))

(comment
  (require '[clojure.edn :refer [read-string]])
  (def creds
    (-> (System/getenv "MYVAULT")
        (str "/goldly/quanta.edn")
        slurp
        read-string
        :bybit/test))

  (def opts {:mode :test
             :segment :trade
             :account creds})

  (def account
    (secure-connection opts))
  

account

  (m/? (m/reduce println nil
                 (secure-connection opts)))

  (def order
    {:asset "ETHUSDT"
     :side :buy
     :qty "0.01"
     :limit "1000.0"})

  (m/?  (send-order account order))

  (m/? (m/reduce
        println nil
        (send-order account order)))

  (m/? (send-order test-account order))

  (m/? (send-order3 test-account order))

  (m/? (req account
            (create-order-msg order)
            ))
  
  

; 
  )
