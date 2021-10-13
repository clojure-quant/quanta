(ns demo.warehouse.create-random
  (:require
   [taoensso.timbre :refer [trace debug info infof warn error]]
   [ta.warehouse.random :as r]
   [demo.env.config :refer [w-random log-config!]]))

(def bybit-symbols ["BTCUSD" "ETHUSD"])

(defn create-crypto-random []
  (r/create-random-datasets w-random bybit-symbols "D" 3000)
  (r/create-random-datasets w-random bybit-symbols "15" 60000))

(defn task-create-random [& _]
  (log-config!)
  (create-crypto-random))

(comment
  (create-crypto-random)

;  
  )


