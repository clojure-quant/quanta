(ns juan.fix
  (:require
   [clojure.pprint :refer [print-table]]
   [fix-engine.api.core :as fix-api]
   [fix-engine.connection.protocol :as p]
   [ta.tickerplant.bar-generator]
   ))

(defn set-interval [callback ms]
  (future (while true (do (Thread/sleep ms) (callback)))))

;(def job (set-interval #(println "hello") 1000))

; (future-cancel job)

(defn print-quotes [client]
  (fn []
    (let [t (fix-api/snapshot client)]
      (println "quote table:")
      (print-table t))))

(def symbols ["EUR/USD" "GBP/USD" "EUR/JPY"
              "USD/JPY" "AUD/USD" "USD/CHF"
              "GBP/JPY" "USD/CAD" "EUR/GBP"
              "EUR/CHF" "NZD/USD" "USD/NOK"
              "USD/ZAR" "USD/SEK" "USD/MXN"])

(defn make-on-quote [{:keys [db] :as state}]
  (fn [msg]
    ;  {:msg-type :quote-data-full, 
    ;   :symbol EUR/JPY, :md-number-entries 2, :md-sub-entry-type 1, 
    ;   :md-entry-price 154.097}
    (let [tick {:symbol (:symbol msg) 
                 :price (:md-entry-price msg)
                 :size 100}]
    ;(println "on-tick: " tick)
    (ta.tickerplant.bar-generator/process-tick state tick) 
    msg)))

(defn start-harvesting [& _]
  (let [client (fix-api/connect :ctrader-tradeviewmarkets-quote)
        bar-generator (ta.tickerplant.bar-generator/bargenerator-start
                       {} ta.tickerplant.bar-generator/print-finished-bars)]
    ; subscribe quotes
    ;(p/subscribe client {:symbol "1"})
    ;(p/subscribe client {:symbol "2"})
   (doall (map #(fix-api/subscribe client {:symbol %}) symbols))
    ; tickerplant
   (fix-api/on-quote client (make-on-quote bar-generator))
    ;(fix-api/snapshot client)    
    (println "will print current quote table every 5 seconds..")
    (set-interval (print-quotes client) 5000)))





