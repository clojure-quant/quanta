(ns ta.env.tools.id-bus
  "the algo result monitor manages subscriptions by algo-id "
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [manifold.stream :as s]
   [manifold.bus :as mbus]))

(defn get-algo-id-for-result [msg]
  (get-in msg [:algo-opts :id]))

(defn create-id-bus [result-stream]
  (let [bus (mbus/event-bus)]
    (s/consume (fn [msg]
                 (when-let [id (get-algo-id-for-result msg)]
                   (info "publishing result for algo id: " id)
                   (mbus/publish! bus id (:result msg))))
               result-stream)
    bus))


(comment
  (def result-stream (s/stream))

  (def bus (create-id-bus result-stream))


  (s/consume (fn [msg]
               (info "RESULT FOR SUBSCRIBED ID: " msg))
             (mbus/subscribe bus 1))

  (s/put! result-stream {:algo-opts {:id 1} :result :nothing})
  (s/put! result-stream {:algo-opts {:id 2} :result :nothing-but-not-interested})


   ; with live-bargenerator-env  
  (require '[modular.system])
  (def live (modular.system/system :live))
  (def id-bus (:id-bus live))
  id-bus
  (s/consume (fn [msg]
               (warn "RESULT FOR SUBSCRIBED ID: " msg))
             (mbus/subscribe id-bus "CAs1Ms"))

 ; 
  )

