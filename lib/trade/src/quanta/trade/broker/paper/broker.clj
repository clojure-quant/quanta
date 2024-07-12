(ns quanta.trade.broker.random-fill
  (:require
   [missionary.core :as m]
   [nano-id.core :refer [nano-id]]
   [tick.core :as t]
   [quanta.trade.broker.protocol :as B]))

(defn log [& data]
  (let [s (with-out-str (apply println data))]
     ;(println s)
    (spit "/home/florian/repo/clojure-quant/quanta/broker-random.txt" s :append true)))

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
  B/broker
  ; process management
  (shutdown [this]
    (log "random-broker shutting down.."))
  (order-update-flow [{:keys [output-flow]}]
    output-flow))



(defn create-random-fills
  "returns a seq of fills, which can be empty"
  [fill-probability orders]
  (let [working-orders (vals @orders)]
    (log "create random fills for working orders: " working-orders)
    (->> working-orders
         (map #(randomly-fill-order orders fill-probability %))
         (remove nil?))))

(defn mix
  "Return a flow which is mixed by flows"
  [& flows]
  (m/ap (m/?> (m/?> (count flows) (m/seed flows)))))

(defn create-random-fill-broker [{:keys [fill-probability wait-seconds] :as opts} order-input-flow]
  (assert fill-probability "opts needs :fill-probability")
  (assert fill-probability "opts needs :wait-seconds")
  (log "creating random-fill broker " fill-probability wait-seconds)
  (let [orders (atom {})
        fill-flow (m/ap  (log "random-fill-broker orderupdate-flow starting ..")
                         (loop [i 0]
                           (log "random-fill-broker orderupdate loop " i)
                           (let [fills (create-random-fills fill-probability orders)]
                             (m/amb 
                               (when (seq? fills)
                               (log "created fills: " fills))
                               (m/? (m/sleep (* 1000 wait-seconds)))
                               (recur (inc i))))))
        response-flow  (m/ap
                        (log "random-fill-broker response-flow starting..")
                        (let [input-msg (m/?> order-input-flow)]
                          (process-incoming-message orders opts input-msg)))
        output-flow  (mix fill-flow response-flow)
        broker (random-fill-broker. opts orders order-input-flow output-flow)]
    broker))


(comment
  (log "hello")
  (create-random-fills
   30
   [{:order-id 1 :asset :BTC :side :buy :limit 100.0 :qty 0.001}
    {:order-id 2 :asset :ETH :side :sell :limit 100.0 :qty 0.001}])

  (m/?
   (m/reduce println nil
             (mix (m/seed [1 2 3 4 5 6 7 8]) (m/seed [:a :b :c]))))

  (def broker1 (create-random-fill-broker
                {:fill-probability 30 :wait-seconds 5}
                (m/seed [])))

  broker1

  (B/order-update-flow broker1)

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






