(ns quanta.trade.broker.manager
  (:require
   [missionary.core :as m]
   [nano-id.core :refer [nano-id]]
   [tick.core :as t]
    ;[quanta.trade.broker.protocol :as B]
    ;[quanta.trade.broker.paper.orderfiller :refer [random-fill-flow]] 
   [quanta.trade.broker.paper.broker :refer [create-order-flow-switch]]
   
   ))


(defn create-limit-order [this {:keys [asset side quantity limit
                                       order-id broker]
                                :as order-details}]
  (assert (string? asset) "limit-order :asset has to be a string")
  (assert (keyword? side) "limit-order :side has to be a keyword")
  (assert (contains? #{:buy :sell} side) "limit-order :side has to be either :long or :short")
  (assert (double? limit) "limit-order :limit needs to be double")
  (assert (double? quantity) "limit-order :quantity needs to be double")
  (let [order (if order-id
                order-details
                (assoc order-details :order-id (nano-id 6)))]
    (println "create limit order: " order)
    (if-let [send @(:! this)]
      (send order)
      (println "cannot send limit order - no flows connected")
      )
     ))

(defn start-ordermanager [broker]
  (let [!-a (atom nil)
        order-action-flow (m/observe (fn [!]
                                       (println "order action flow starting..")
                                       (! :init)
                                       (reset! !-a !)
                                       #(println "ordermanager cancelled" :cancelled)))
        random-broker-1 (create-order-flow-switch {:fill-probability 30 :wait-seconds 5}
                                                  order-action-flow
                                                  )
        print-output-flow (m/reduce println [] random-broker-1)
        dispose! (print-output-flow
                     #(println "order history: " %)
                     #(prn ::crash %))
        ]
    {:! !-a
     :order-action-flow order-action-flow
     :broker {:random-1 random-broker-1}
     :dispose! dispose!
     }))

(def om (start-ordermanager nil))

(create-limit-order om {:asset "BTC" :side :buy 
                        :broker :random1
                        :limit 100.0 :quantity 0.01})


(println "asdf")





