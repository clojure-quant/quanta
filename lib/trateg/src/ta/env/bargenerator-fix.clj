(ns juan.fix
  (:require
   [clojure.pprint :refer [print-table]]
   [fix-engine.api.core :as fix-api]
   [fix-engine.connection.protocol :as p]
   [ta.tickerplant.bar-generator :refer [process-tick]]))

1. load-series env
2. bar-events


bar_generator
rt session
rt subscription
bargenerator subscriptionn.


(defn make-on-quote [bar-generator]
  (fn [msg]
    ;  {:msg-type :quote-data-full, 
    ;   :symbol EUR/JPY, :md-number-entries 2, :md-sub-entry-type 1, 
    ;   :md-entry-price 154.097}
    (let [tick {:symbol (:symbol msg)
                :price (:md-entry-price msg)
                :size 100}]
    ;(println "on-tick: " tick)
      (process-tick bar-generator tick)
      msg)))

(defn start-harvesting [bar-generator]
  (let [client (fix-api/connect :ctrader-tradeviewmarkets-quote)]
    ; subscribe quotes
    ;(p/subscribe client {:symbol "1"})
    ;(p/subscribe client {:symbol "2"})
    (doall (map #(fix-api/subscribe client {:symbol %}) symbols))
    ; tickerplant
    (fix-api/on-quote client (make-on-quote bar-generator))
    ;(fix-api/snapshot client)    
    client))

