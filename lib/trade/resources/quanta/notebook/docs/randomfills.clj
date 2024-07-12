(ns quanta.notebook.docs.randomfills
  (:require
   [quanta.trade.broker.protocol :as b]
   [quanta.trade.broker.random-fill :refer [create-random-fill-broker]]
   [missionary.core :as m]
   ))


(defn process-msg
  ([]
   (println "order-manager init! "))
  ([msg]
   (println "order-manager received: " msg))
  ([result msg]
   (println "order-manager received: " msg " result: " result)))


(def example-order-flow
  [{:type :new-order
    :order-id 1
    :asset :BTC
    :side :buy
    :limit 100.0
    :qty 0.001}
   {:type :new-order
    :order-id 2
    :asset :ETH
    :side :sell
    :limit 100.0
    :qty 0.001}
   {:type :cancel-order
    :order-id 2}
   {:type :new-order
    :order-id 3
    :asset :ETH
    :side :sell
    :limit 100.0
    :qty 0.001}
   #_{:type :new-order
      :order-id 4
      :asset :ETH
      :side :sell
      :limit 100.0
      :qty 0.001}])

(def broker1 (create-random-fill-broker
              {:fill-probability 30 :wait-seconds 5}
              (m/seed example-order-flow)))

broker1

(b/order-update-flow broker1)

(m/? (m/reduce process-msg
               (b/order-update-flow broker1)))
