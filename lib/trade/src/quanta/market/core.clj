(ns quanta.market.core
  (:require
   [quanta.market.protocol :as p]
    ; default implementations:
   [quanta.market.broker.random]
   [quanta.market.broker.bybit]))

(defn create-connection [[id opts]]
  (let [{:keys [type]} opts]
    [id {:type type
         :conn (p/connection opts)}]))

(defn start-account-manager [accounts]
  (->> accounts
       (map create-connection)
       (into {})))

(defn get-quote2 [this {:keys [account asset]}]
  (let [{:keys [type conn]} (get this account)]
    (p/get-quote type conn asset)))

(comment

  (def demo-accounts
    {; quote connections
     :random {:type :random}
     :bybit {:type :bybit
             :mode :main
             :segment :spot}
     ; trade connections
     })

  (def this (start-account-manager demo-accounts))
  this

  (require '[missionary.core :as m])

  (m/? (m/reduce
        println nil (get-quote2 this {:account :random 
                                   :asset "BTC"
                                   })))
  
(m/? (m/reduce
      println nil (get-quote2 this {:account :bybit
                                    :asset "BTCUSDT"})))
  





; 
  )







