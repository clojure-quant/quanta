(ns quanta.trade.broker.paper.broker
  (:require
   [missionary.core :as m]
   [nano-id.core :refer [nano-id]]
   [tick.core :as t]
   [quanta.trade.broker.protocol :as B]
   [quanta.trade.broker.paper.orderfiller :refer [random-fill-flow]]
   ))

#_(defn log [& data]
  (let [s (with-out-str (apply println data))]
     ;(println s)
    (spit "/home/florian/repo/clojure-quant/quanta/broker-random.txt" s :append true)))
 
(defn log [order-id & data]
  (apply println "switch [" order-id "]" data))


(defn create-order-flow-switch [{:keys [fill-probability wait-seconds] :as opts} order-input-flow]
  (assert fill-probability "opts needs :fill-probability")
  (assert fill-probability "opts needs :wait-seconds")
  (log "config: " fill-probability wait-seconds)
  (let [order-id-to-flow-a (atom {})
        create-order (fn [order-data]
                       (let [flow (random-fill-flow opts order-data)]
                         (swap! order-id-to-flow-a
                                assoc (:order-id order-data) flow)
                         flow))
        cancel-order (fn [order-id]
                       (let [flow (get @order-id-to-flow-a order-id)]
                         (swap! order-id-to-flow-a dissoc :order-id)))
        output-flow  (m/ap
                      (log "starting")
                      (let [{:keys [type order-id] :as order-action} (m/?> order-input-flow)]
                        (log "order-action received type:" type)
                        (case type
                          :new-order (m/amb (m/?> (create-order order-action)))
                            ;:cancel-order (do (cancel-order orders order-id))
                          {:type :order-update/reject 
                           :message (str "unsupported message type: " type)
                           :order-action order-action})))
        ]
    output-flow
    ))



(defrecord random-fill-broker [opts input-flow output-flow]
  B/broker
  ; process management
  (shutdown [this]
    (log "random-broker shutting down.."))
  (order-update-flow [{:keys [output-flow]}]
    output-flow))

(defn mix
  "Return a flow which is mixed by flows"
  [& flows]
  (m/ap (m/?> (m/?> (count flows) (m/seed flows)))))

(defn create-random-fill-broker [{:keys [fill-probability wait-seconds] :as opts} order-input-flow]
  (let [
        broker (random-fill-broker. opts order-input-flow output-flow)]
    broker))

(comment
  (log "hello")
 
  (m/?
   (m/reduce println nil
             (mix (m/seed [1 2 3 4 5 6 7 8]) (m/seed [:a :b :c]))))

  (require '[quanta.trade.broker.paper.orderflow-simulated :refer [demo-order-action-flow]])

     
  (def switch1 (create-order-flow-switch
                {:fill-probability 30 :wait-seconds 5}
                demo-order-action-flow))

  switch1
    
  (defn log-progress [r order-update]
    (println "broker order-update: " order-update)
    (conj r order-update))

  (def print-progress-task
  (m/reduce log-progress [] switch1))

(def dispose!
  (print-progress-task
   #(println "success: " %)
   #(prn ::crash %)))
  
 
  [{:type :order-update/new-order :date #inst "2024-07-12T20:05:40.758909483-00:00", :order-id 1, } 
   {:type :order-update/fill, :order-id 1, :fill-id eT3h3f, :date #inst "2024-07-12T20:06:00.762817855-00:00",  :asset :BTC, :qty 0.001, :side :buy} 
   {:date #inst "2024-07-12T20:06:00.764297568-00:00", :order-id 2, :type :order-update/new-order}
   {:type :order-update/fill, :order-id 2, :fill-id Ozlotz, :date #inst "2024-07-12T20:06:20.768514580-00:00", :asset :ETH, :qty 0.001, :side :sell} 
   :unknown-message-type 
   {:date #inst "2024-07-12T20:06:25.771038739-00:00", :order-id 3, :type :order-update/new-order} 
   {:type :order-update/fill, :order-id 3, :fill-id SjcXKD, :date #inst "2024-07-12T20:06:45.775714428-00:00",  :asset :ETH, :qty 0.001, :side :sell} 
   {:date #inst "2024-07-12T20:06:45.777047187-00:00", :order-id 4, :type :order-update/new-order}
   {:type :order-update/fill, :order-id 4, :fill-id 4BIYA8, :date #inst "2024-07-12T20:07:00.779794903-00:00", :asset :ETH, :qty 0.001, :side :sell}]



dispose!

  (dispose!)

  

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






