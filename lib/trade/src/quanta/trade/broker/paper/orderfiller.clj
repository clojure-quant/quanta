(ns quanta.trade.broker.paper.orderfiller
   (:require
    [missionary.core :as m]
    [nano-id.core :refer [nano-id]]
    [tick.core :as t])
   (:import [missionary Cancelled]))

 (defn random-fill
   "probabilistically returns either a filled order, or nil"
   [fill-probability {:keys [order-id qty side asset] :as order}]
   (when (< (rand-int 100) fill-probability)
     {:type :order-update/fill
      :order-id order-id
      :fill-id (nano-id 6)
      :date (t/instant)
      :asset asset
      :qty qty
      :side side}))

 (defn log [order-id & data]
   (apply println "random-fill [" order-id "]" data))

 (defn random-fill-flow
   "returns a flow of fills. 
   fills happen randomly. 
   when the order is filled, the flow stops."
   [{:keys [fill-probability
            wait-seconds]}
    {:keys [order-id] :as order}]
   (m/ap (log order-id "order created")
         (loop [i 0]
           (if (= i 0)
             (m/amb
              {:type :order-update/new-order
               :order-id order-id
               :date (t/instant)}
              (recur (inc i)))
             (let [fill (random-fill fill-probability order)]
               (if fill
                 (do (log order-id "filled: " fill)
                     (m/amb fill))
                 (let [cancelled? (try
                                    (log order-id "not filled. sleeping: " wait-seconds)
                                    (m/? (m/sleep (* 1000 wait-seconds) false))
                                    (catch Cancelled _ true))]
                   (if cancelled?
                     (do (log order-id " cancelled")
                         (m/amb  {:type :order-update/canceled
                          :order-id order-id
                          :date (t/instant)}))
                     (m/amb (recur (inc i)))))))))))

(defn log-progress [r order-update]
  (println "order-update: " order-update)
  (conj r order-update))

(comment
  (def order {:order-id 2
              :asset :BTC
              :side :buy
              :limit 100.0
              :qty 0.001})

  (def fill-flow (random-fill-flow {:fill-probability 20
                                    :wait-seconds 5}
                                   order))

  (def print-progress-task
    (m/reduce log-progress [] fill-flow))

  (def dispose!
    (print-progress-task
     #(println "order history: " %)
     #(prn ::crash %)))
  

  (dispose!)

 ; 
  )

 