(ns quanta.trade.protocol)

(defprotocol broker
  ; process management
  (shutdown [this])
  (order-update-flow [this]))


(defprotocol ordermanager
  ; order actions
  (send-limit-order [this order-details])
  (cancel-limit-order [this order-id])
  ; process management
  (add-broker [this broker])
  (shutdown [this])
  (order-update-flow [this])
  (working-orders [this]))