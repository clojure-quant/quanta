(ns quanta.market.core
  (:require
   [quanta.market.protocol :as p]
    ; default implementations:
   [quanta.market.broker.random]
   [quanta.market.broker.bybit.bybit]))

(defn create-connection [[id opts]]
  (let [{:keys [type]} opts]
    [id {:type type
         :conn (p/connection opts)}]))

(defn start-account-manager [accounts]
  (->> accounts
       (map create-connection)
       (into {})))

(defn get-quote [this {:keys [account asset]}]
  (let [{:keys [type conn]} (get this account)]
    (p/get-quote type conn asset)))

(comment

    (def bybit-test-creds
    (-> (System/getenv "MYVAULT")
        (str "/goldly/quanta.edn")
        slurp
        read-string
        :bybit/test))
  

  (def demo-accounts
    {; quote connections
     :random {:type :random}
     :bybit {:type :bybit
             :mode :main
             :segment :spot}
     :bybit-test {:type :bybit-account
                  :mode :test
                  :segment :trade
                  :account bybit-test-creds}
     ; trade connections
     })

  (def this (start-account-manager demo-accounts))

  this

  (require '[missionary.core :as m])

  (def print-quote (fn [r q] (println q)))

  (m/? (m/reduce
        print-quote nil (get-quote this {:account :random
                                     :asset "BTC"})))

  (m/? (m/reduce
        print-quote nil (get-quote this {:account :bybit
                                      :asset "BTCUSDT"})))

; 
  )







