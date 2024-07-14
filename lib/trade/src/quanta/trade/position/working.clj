(ns quanta.trade.position.working
  (:require
   [tick.core :as t]
   [missionary.core :as m]
   [quanta.quote.random :refer [get-quote]]
   [quanta.trade.roundtrip :refer [return-prct return-abs return-log]])
  (:import [missionary Cancelled]))

(defn value-position [position
                      {:keys [date last] :as quote}]
  (println "value-position: " position " quote: " quote)
  (let [roundtrip (assoc position
                         :exit-date date
                         :exit-price last)
        abs-ret (return-abs roundtrip)]
    (if quote
      (assoc roundtrip
             :ret-abs abs-ret
             :ret-prct (return-prct roundtrip)
             :ret-log (return-log roundtrip)
             :win? (> abs-ret 0.0))
      roundtrip)))

(defn working-position [position]
  (m/ap
     ; startup
   (println "start calculating position: " position)
   (let [asset (:asset position)
         quote (get-quote asset)
         current-quote (m/?> quote)]
     (value-position position current-quote))))

(comment
  (require '[tick.core :as t])
  (m/? (m/reduce println
                 (working-position {:asset "BTCUSDT"
                                      :qty 500
                                      :entry-price 1000.0
                                      :entry-date (t/instant)})))

  (def positions [{:asset "BTCUSDT"
                   :side :long
                   :qty 500
                   :entry-price 1000.0
                   :entry-date (t/instant)}
                  {:asset "BTCUSDT"
                   :side :short
                   :qty 500
                   :entry-price 1000.0
                   :entry-date (t/instant)}
                  {:asset "ETHUSDT"
                   :side :long
                   :qty 500
                   :entry-price 1000.0
                   :entry-date (t/instant)}
                  {:asset "ETHUSDT"
                   :side :short
                   :qty 500
                   :entry-price 1000.0
                   :entry-date (t/instant)}
                  ])

  (require '[clojure.pprint :refer [print-table]])
  (defn print-positions [& positions]
    (print-table [:asset :side
                  :exit-price
                  :ret-prct
                  :win?] positions))

  (let [flows (map working-position positions)]
    (m/? 
     (m/reduce (constantly nil)
               (apply m/latest print-positions flows))))

; 
  )

  



