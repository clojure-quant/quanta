(ns quanta.trade.broker.paper.orderflow-simulated
  (:require
   [missionary.core :as m]))

(defn simulated-order-action-flow
  "returns a missionary flow that fires input orders over time.
     input is a partition-2 seq.
     first value of a partition is the next sleep time in secons
     second falue is a order-action (:type :new-order or :cancel-order)"
  [time-order-partitions]
  (let [input (m/seed (partition 2 time-order-partitions))]
    (m/ap   (let [[sleep-sec order] (m/?> input)]
              (m/? (m/sleep (* 1000 sleep-sec)))
              order))))

(comment
  (def example-order-action-flow
    [0 {:type :new-order
        :order-id 1
        :asset :BTC
        :side :buy
        :limit 100.0
        :qty 0.001}
     2 {:type :new-order
        :order-id 2
        :asset :ETH
        :side :sell
        :limit 100.0
        :qty 0.001}
     3 {:type :cancel-order
        :order-id 2}
     16 {:type :new-order
         :order-id 3
         :asset :ETH
         :side :sell
         :limit 100.0
         :qty 0.001}
     10 {:type :new-order
         :order-id 4
         :asset :ETH
         :side :sell
         :limit 100.0
         :qty 0.001}])

  (simulated-order-action-flow simulated-order-action-flow)

  (m/? (m/reduce println nil
                 (simulated-order-action-flow simulated-order-action-flow)))

;; will print over time the following:
; nil {:type :new-order, :order-id 1, :asset :BTC, :side :buy, :limit 100.0, :qty 0.001}
; nil {:type :new-order, :order-id 2, :asset :ETH, :side :sell, :limit 100.0, :qty 0.001}
; nil {:type :cancel-order, :order-id 2}
; nil {:type :new-order, :order-id 3, :asset :ETH, :side :sell, :limit 100.0, :qty 0.001}
; nil {:type :new-order, :order-id 4, :asset :ETH, :side :sell, :limit 100.0, :qty 0.001}

; 
  )



  

