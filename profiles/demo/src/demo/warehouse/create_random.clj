(ns demo.warehouse.create-random
  (:require
   [taoensso.timbre :refer [trace debug info infof warn error]]
   [ta.warehouse.random :as r]
     [ta.warehouse :as wh]
    [tablecloth.api :as tablecloth]
   [demo.env.config :refer [w-random w-shuffled w-crypto log-config!]]))

(def bybit-symbols ["BTCUSD" "ETHUSD"])

(defn create-crypto-random []
  (r/create-random-datasets w-random bybit-symbols "D" 3000)
  (r/create-random-datasets w-random bybit-symbols "15" 60000))


(defn create-crypto-shuffled []
  (r/create-shuffled-datasets w-crypto w-shuffled bybit-symbols "D")
  (r/create-shuffled-datasets w-crypto w-shuffled bybit-symbols "15")
  )

(defn task-create-random [& _]
  (log-config!)
  (create-crypto-random))

(comment
  (create-crypto-random)
  (create-crypto-shuffled)

  (-> (wh/load-symbol w-shuffled "15" "ETHUSD")
      (tablecloth/shuffle)
      ;(tablecloth/select-rows (range 1000 1050))
   )
  
;  
  )


