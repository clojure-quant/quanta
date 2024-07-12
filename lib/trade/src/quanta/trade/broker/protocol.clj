(ns quanta.trade.broker.protocol)

(defprotocol broker
  ; process management
  (shutdown [this])
  (order-update-flow [this]))
