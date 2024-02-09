(ns ta.env.tools.last-msg-summary
  "create stream listener that keeps the last value of a message
   that can be categorised with an identifier-fn
   The current table can be queried with 'current-summary'"
  (:require
   [manifold.stream :as s]))

(defn- gen-process-msg [state identifier-fn]
  (fn [msg]
    (let [k (identifier-fn msg)]
      (swap! (:last state) assoc k msg))))

(defn create-last-summary [stream identifier-fn]
  (let [state {:identifier-fn identifier-fn
               :last (atom {})}]
    (s/consume (gen-process-msg state identifier-fn) stream)
    state))

(defn current-summary [state]
  (let [last @(:last state)]
    (vals last)))

(comment 
  
  (def stream (s/stream))
  (def state (create-last-summary stream :asset))
  
  (s/put! stream {:asset "EUR/USD" :price 1.10})
  (s/put! stream {:asset "USD/JPY" :price 130.66})
  (s/put! stream {:asset "EUR/USD" :price 1.11})

  (current-summary state)


 ; 
  )