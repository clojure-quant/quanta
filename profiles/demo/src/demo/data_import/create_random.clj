(ns demo.data-import.create-random
  (:require
   [tablecloth.api :as tablecloth]
   [ta.warehouse.random :as r]
   [ta.warehouse :as wh]))

(def bybit-symbols ["BTCUSD" "ETHUSD"])

(defn create-crypto-random []
  (r/create-random-datasets :random bybit-symbols "D" 3000)
  (r/create-random-datasets :random bybit-symbols "15" 60000))

(defn create-crypto-shuffled []
  (r/create-shuffled-datasets :crypto :shuffled bybit-symbols "D")
  (r/create-shuffled-datasets :crypto :shuffled bybit-symbols "15"))

(comment
  (create-crypto-random)
  (create-crypto-shuffled)

  (-> (wh/load-symbol :shuffled "15" "ETHUSD")
      (tablecloth/shuffle)
      ;(tablecloth/select-rows (range 1000 1050))
      )

;  
  )


