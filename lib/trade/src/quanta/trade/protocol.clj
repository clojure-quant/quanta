(ns quanta.trade.protocol)




(defprotocol ordermanager
  ; order actions
  (send-limit-order [this order-details])
  (cancel-limit-order [this order-id])
  ; process management
  (add-broker [this broker])
  (shutdown [this])
  (order-update-flow [this])
  (working-orders [this]))