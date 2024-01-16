(ns juan.fix
  (:require
   [clojure.pprint :refer [print-table]]
   [fix-engine.api.core :as fix-api]
   [fix-engine.connection.protocol :as p]
   [ta.tickerplant.bar-generator :refer [process-tick]]
   ))

(defn set-interval [callback ms]
  (future (while true (do (Thread/sleep ms) (callback)))))

;(def job (set-interval #(println "hello") 1000))

; (future-cancel job)



(def symbols ["EUR/USD" "GBP/USD" "EUR/JPY"
              "USD/JPY" "AUD/USD" "USD/CHF"
              "GBP/JPY" "USD/CAD" "EUR/GBP"
              "EUR/CHF" "NZD/USD" "USD/NOK"
              "USD/ZAR" "USD/SEK" "USD/MXN"])

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


; quote printer

(defn print-quotes [client]
  (fn []
    (let [t (fix-api/snapshot client)]
      (println "quote table:")
      (print-table t))))

(defn start-quote-printing [fix-harvester]
   (println "will print current quote table every 5 seconds..")
   (set-interval (print-quotes fix-harvester) 5000))
  
  



