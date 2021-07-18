(ns wave.trade
  (:require
    [backtest.random :refer [random-series]]
    [backtest.indicator :refer [sma ema]])
  )


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

(defn wave-trade [waves-data]
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
  (wave-trade {:price [100 111 133 155 144 122 133 132 199 150]
               :spivot-long {:price [nil 2 nil nil 4 nil nil nil 3 nil]}
               :spivot-short {:price [nil nil 3 nil nil 5 nil nil nil nil]}})
  )
