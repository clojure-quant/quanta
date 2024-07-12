(ns quanta.trade.random-fill-broker
   (:require
    [missionary.core :as m]
    [nano-id.core :refer [nano-id]]
    [tick.core :as t]
    [quanta.trade.protocol :refer [broker order-update-flow]]))

 (defn log [& data]
   (let [s (with-out-str (apply println data))]
     (println s)
     (spit "/home/florian/repo/clojure-quant/quanta/broker.txt" s :append true)))

 (defn add-order [orders order-details]
   (log "adding order: " order-details)
   (swap! orders assoc (:order-id order-details) order-details))

 (defn remove-order [orders order-id]
   (log "removing order: " order-id)
   (swap! orders dissoc order-id))
 
 

 (defn process-incoming-message [orders opts msg]
   (log "random-fill-broker received msg: " msg)
   (let [out-msg (case (:type msg)
                   :new-order (do (add-order orders msg)
                                  {:type :new-order/confirmed :order-id (:order-id msg)})
                   :cancel-order (do (remove-order orders (:order-id msg))
                                     {:type :cancel-order/confirmed :order-id (:order-id msg)})
                   :unknown-message-type)]
     out-msg))

 (defrecord random-fill-broker [opts orders input-flow output-flow]
   broker
  ; process management
   (shutdown [this]
     (println "random-broker shutting down.."))
   (order-update-flow [{:keys [output-flow]}]
     output-flow))

 (defn randomly-fill-order
   "probabilistically returns either a filled order, or nil"
   [orders fill-probability {:keys [order-id qty side asset] :as order}]
   (when (< (rand-int 100) fill-probability)
     (remove-order orders order-id)
     {:order-id order-id
      :fill-id (nano-id 6)
      :date (t/instant)
      :asset asset
      :qty qty
      :side side}))
 

 (defn create-random-fills
   "returns a seq of fills, which can be empty"
   [fill-probability orders]
   (let [working-orders (vals @orders)]
     (println "create random fills for working orders: " working-orders)
     (->> working-orders
          (map #(randomly-fill-order orders fill-probability %))
          (remove nil?))))
 
 (def >clock    ;; A shared process emitting `nil` every second.
   (m/stream
    (m/ap
     (loop [i 0]
       (m/amb
        (m/? (m/sleep 1000))
        (println "i: " i)
        (recur (inc i)))))))

 (defn create-random-fill-broker [{:keys [fill-probability wait-seconds] :as opts} order-input-flow]
   (assert fill-probability "opts needs :fill-probability")
   (assert fill-probability "opts needs :wait-seconds")
   (log "creating random-fill broker " fill-probability wait-seconds)
   (let [orders (atom {})
         fill-flow (m/ap  (log "random-fill-broker orderupdate-flow starting ..")
                           (loop [i 0]
                             (log "random-fill-broker orderupdate loop " i)
                             (let [fills (create-random-fills fill-probability orders)]
                               (when (seq? fills)
                                 (log "created fills: " fills)
                                 (m/amb fills))
                               (m/? (m/sleep (* 1000 wait-seconds)))
                               (recur (inc i)))))
         response-flow  (m/ap
                    (log "random-fill-broker response-flow starting..")
                     (let [input-msg (m/?> order-input-flow)]
                       (process-incoming-message orders opts input-msg)))
         output-flow  (m/signal (m/latest concat fill-flow response-flow))
         broker (random-fill-broker. opts orders order-input-flow output-flow)]
     broker))
 
 

 (defn process-msg
    ([]
    (log "order-manager init! "))
    ([msg]
     (log "order-manager received: " msg))
    ([result msg]
     (log "order-manager received: " msg " result: " result)))


 (comment

   (create-random-fills
    30
    [{:order-id 1 :asset :BTC :side :buy :limit 100.0 :qty 0.001}
     {:order-id 2 :asset :ETH :side :sell :limit 100.0 :qty 0.001}])

   (log "hello")

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
       {:type :new-order
       :order-id 4
       :asset :ETH
       :side :sell
       :limit 100.0
       :qty 0.001}
      ])

   (def broker1 (create-random-fill-broker
                 {:fill-probability 30 :wait-seconds 5}
                 (m/seed example-order-flow)))
   

   broker1

   

 (m/? (m/reduce process-msg
                (order-update-flow broker1)))

 (defn counter [r _] (inc r))
;; A reducing function counting the number of items.

 (m/?
  (m/reduce counter 0 (m/eduction (take 6) (order-update-flow broker))))

 (working-orders broker)

 (send-limit-order broker {:order-id (nano-id 6)
                           :asset "BTC"
                           :side :buy
                           :limit 10000.0
                           :qty 0.01})

; 
 )






