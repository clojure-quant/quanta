(ns wavetrade1
  (:require
   [backtest.random :refer [random-series]]
   [backtest.indicator :refer [sma ema]]))

;;; ### Some Helper Functions

(defn trade-pl [trade]
  (case (:direction trade)
      :long (- (:exit-price trade) (:entry-price trade))
      :short (- (:entry-price trade) (:exit-price trade))
  ))

(defn duration [trade]
    (- (:exit-index trade) (:entry-index trade)))


(defn calc-pl [trades]
  (let [cum-pl (atom 0)]
    (map #(let [pl (trade-pl %)]
      (swap! cum-pl + pl)
      (assoc % :pl pl :cum-pl @cum-pl :duration (duration %))) trades)))

(defn gorilla-trade 
  "takes the data structure that calculates waves-analytics and generates trades
   open is on a significant-pivot
   close is on the open of the next significant pivot"  
  [waves-data]
  (let [position (atom nil)
        trades (atom [])
        open-position (fn [index price direction]
          (do ;(println "opening position " index " " price " " direction)
              (reset! position {:direction direction :entry-price price :entry-index index})))
        close-position (fn [index price]
            (do (swap! trades conj (assoc @position :exit-price price :exit-index index))
                (reset! position nil)))
        open-position-close-prior (fn [index price direction]
            (do (if (not (nil? @position)) (close-position index price))
                (open-position index price direction)))
        ]
  (doall (for [i (range (count (:price waves-data)))]
      (let [p (nth (:price waves-data) i)
            lp (nth (get-in waves-data [:spivot-long :price]) i)
            sp (nth (get-in waves-data [:spivot-short :price]) i)]
       (do ;(println "price:" p)
           (if (not (nil? lp)) (open-position-close-prior i p :long))
           (if (not (nil? sp)) (open-position-close-prior i p :short))
            ))))
    ;(println "done.");
    (calc-pl @trades)
   ))

(comment 
 
 ; since wavetrade1 is a helper function, we cannot EVALUATE stuff here -- otherwise this 
 ; gets evaluated on REQUIRING this library.
 
(clojure.pprint/print-table 
   (gorilla-trade {:price [1 2 3 2 1 2 3 4 5 5]
             :spivot-long {:price [nil 2 nil nil nil 2 nil nil 1 nil]}
             :spivot-short {:price [1 nil 3 nil nil nil nil nil nil 1]}}))

)

