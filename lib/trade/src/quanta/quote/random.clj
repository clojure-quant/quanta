(ns quanta.quote.random
  (:require
   [tick.core :as t]
   [missionary.core :as m])
  (:import [missionary Cancelled]))

(defn initial-price []
  ;(rand 10000)
  10000.0
  )

(defn update-price [p]
  (let [i (rand-int 5)]
    (case i
      0 (/ p 1.03) ; strong down
      1 (/ p 1.01) ; weak down
      2  p ; unchanged
      3 (* p 1.01) ; weak up
      4 (* p 1.03) ; strong up
      )))

(defonce subscription-a (atom #{}))

(defn generate-quotes [asset]
  (m/ap
   ; startup
   (println "start generating quotes for: " asset)
   (swap! subscription-a conj asset)
   (loop [p (initial-price)]
     (m/amb {:asset asset
             :bid p
             :ask p
             :last p
             :date (t/instant)
             :feed :random}
            (let [recur? (try
                           (m/? (m/sleep 5000 true))
                           (catch Cancelled ex
                             ; shutdown
                             (println "stop generating quotes for: " asset)
                             (swap! subscription-a disj asset)
                             false))]
              (if recur?
                (recur (update-price p))
                :unsubscribed
                ))))))

(def get-quote (memoize generate-quotes))

(comment
  (initial-price)
  (update-price 100.0)

  (m/? (m/reduce
        println nil (generate-quotes "BTC")))

  (m/? (m/reduce
        println nil (get-quote "BTC")))

  @subscription-a

(require '[clojure.pprint :refer [print-table]])  

(defn print-quotes [& quotes]
  (print-table [:asset :last :date] quotes))
  
(let [assets ["BTC" "ETH" "EURUSD" "QQQ" "EURUSD"]
      quotes (map get-quote assets)]
  (m/? (m/reduce (constantly nil)
         (apply m/latest print-quotes quotes))))
 ; prints entire quote-table whenever one of the quotes updates.


 ; 
  )


