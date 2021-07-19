
(ns waves-experiment
  (:require
   [pinkgorilla.ui.vega :refer [vega! module]]
    [pinkgorilla.ui.gorilla-plot.core :as plot :refer [list-plot bar-chart compose histogram plot]]   ; http://gorilla-repl.org/plotting.html
   [clojure.pprint]
   [import.bybit :refer [history-recent history-recent-extended]]  ; data for BTCUSD ETHUSD EOSUSD
   [backtest.random :refer [random-series]] ; random series are good to test the algorithm
   [backtest.print :refer [print-table print-map-of-vecs filter-vector chart-filter]] ; utils to print data we want to look at
   [backtest.wave :refer [waves]] ; wave indicator
   ;[backtest.wave-trade :refer [wave-trade ]] ; wave backtest (IN THE PROJECT)
   [wavetrade1 :refer [gorilla-trade ]] ; wave backtest (FROM GORILLA REPL!!)

  ; :reload-all)
  ))
 
;;; helper function to not-show nil values in list-plot

(defn without-nil [s]
  (vec
    (remove #(nil? (get % 1))
	  (map-indexed (fn [idx itm] [idx itm]) s))))

;(def s [-2 3 1 2 3 nil 3 2 1])
;(without-nil s)
;(plot/list-plot (without-nil s) )

;;; Random Data (for testing)

;(def price-series (random-series 50))

;;; BTCUSD history from ByBit



;(def bybit (history-recent "BTCUSD" 200)) ; load the X most recent 15 minute bars
;(def bybit (history-recent-extended "BTCUSD" 14000)) ; multiple requests to bybit if need be.
(def bybit (history-recent-extended "BTCUSD" 800)) ; multiple requests to bybit if need be.
(def price-series (vec (map :close bybit))) ; a random price-series will be overwritten

;print-table params:
;  :first 3
;  :last 5
;  :all
;  :range [10 20]
(print-table bybit [:date :open :high :low :close :volume] :first 10)
;(print-table bybit [:date :open :high :low :close :volume] :range [1000 1020] )

(def params {
  :price-ema 12 ; 30     ; EMA calculated on Price-Series

  ; Oscillator Trend Calculation -> The crossover determines the pivots
  :oscillator-lt 16  ; ema of oscillator
  :oscillator-st 4  ; sma on ema of oscillator

  :pivot-lookback { ; compared are oscillator-lt (= wt2)
    :window 300	;sliding-lookback window to compare current pivot to prior pivots
    :pivot-short {
      :prior-min 50
      :current-min 0
      :current-max 40}
    :pivot-long {
      :prior-max -50
      :current-min -40
      :current-max 0}
  }})

(def result (waves params price-series)) ; calculate wave-indicator
(def result (assoc result :time (vec (map #(:date %) bybit)))) ; add time from bybit to the result

;;; print-result can show different part of the series
;;; :first 3
;;; :last 5
;;; :all
;;; :range [10 20]


(def cols
  [:time
   :price
   :price-ema
   ;:delta-abs
  ; [:oscillator :v]
   ;[:oscillator :lt]
   ;[:oscillator :st]
   ;[:oscillator :trend]   ; previously difference, but to other direction. now pos=uptrend, neg=downtrend
   ;[:pivot-long :b]     ; bool true=pivot-long
   ;[:pivot-short :b]    ; bool true=pivot-short
   ;[:pivot-long :price]  ; if pivot-long then price of series, otherwise nil
   ;[:pivot-short :price]
    [:pivot-long :oscillator-lt]
   ; [:pivot-short :oscillator-lt]
    [:spivot-long :oscillator-lt]
    ;[:spivot-short :oscillator-lt]
   
   ])
;(print-map-of-vecs result cols :last 10)
(print-map-of-vecs result cols :last 250)


;(def r result)
(def r (chart-filter :last 500 result))


(plot/compose
   (plot/list-plot (:price r) :joined true)
   (plot/list-plot (without-nil (get-in r [:pivot-long :price])) :color "green" :opacity 0.25)
   (plot/list-plot (without-nil (get-in r [:spivot-long :price])) :color "green" :opacity 1.0)
   (plot/list-plot (without-nil (get-in r [:pivot-short :price])) :color "red" :opacity 0.25)
   (plot/list-plot (without-nil (get-in r [:spivot-short :price])) :color "red" :opacity 1.0)
 )

"oscillator (individual st lt - lt determines significant pivots)"
(def xy-osc [:all [-100 100]])
(plot/compose
   (plot/list-plot (get-in r [:oscillator :v] ) :joined true :plot-range xy-osc :color "lightgrey")
   (plot/list-plot (get-in r [:oscillator :lt]) :joined true :plot-range xy-osc :color "blue")
   (plot/list-plot (get-in r [:oscillator :st]) :joined true :plot-range xy-osc :color "orange")
   (plot/list-plot (without-nil (get-in r [:pivot-long :oscillator-lt])) :plot-range xy-osc :color "green" :opacity 0.25)
   (plot/list-plot (without-nil (get-in r [:pivot-short :oscillator-lt])) :plot-range xy-osc :color "red" :opacity 0.25)
   (plot/list-plot (without-nil (get-in r [:spivot-long :oscillator-lt])) :plot-range xy-osc  :color "green" :opacity 1.0)
 (plot/list-plot (without-nil (get-in r [:spivot-short :oscillator-lt])) :plot-range xy-osc  :color "red" :opacity 1.0)
 )


"oscillator trend"
(def xy-oscd [:all [-40 40]])
(plot/compose
   (plot/list-plot (get-in r [:oscillator :trend]) :plot-range xy-oscd :joined true)
   (plot/list-plot (without-nil (get-in r [:pivot-long :oscillator-trend])) :plot-range xy-oscd :color "green" :opacity 0.25)
   (plot/list-plot (without-nil (get-in r [:pivot-short :oscillator-trend])) :plot-range xy-oscd :color "red" :opacity 0.25)
 )

;;; ## Test of the trading function



;;; **Ideas Florian**
;;; 1. The crossover can happen abruptly; therefore most of the energy could already be gone. Do statistic by Crossover Oscillator Value.


;(def trades (wave-trade result))
(def trades-raw (gorilla-trade result))

(defn big-pl? [trade]
  (> (Math/abs (:pl trade)) 100))
  
(def trades-small (wavetrade1/calc-pl (remove big-pl? trades-raw)))
(def trades trades-raw)

"small trades"
(clojure.pprint/print-table trades-small)

"all trades"
(clojure.pprint/print-table trades)

;; SMALL TRADES ARE LOOSING ON AVERAGE => FOCUS ON THE BIG WINNERS.


(def cum-pl (map :cum-pl trades))
(plot/list-plot cum-pl :joined true)

(plot/histogram (map :pl trades)
             :bins 15)
 
;:normalise   :probability-density)

(defn profit?
  "true if trade is a profit, false otherwise"
  [trade]
  (>= (:pl trade) 0))

(defn calc-cum-pl [trades]
  (reduce + (map :pl trades)))


(defn profit-factor [trades]
  (let [trades-profit (filter profit? trades)
        trades-loss (remove profit? trades)
        pl-profit (calc-cum-pl trades-profit)
        pl-loss (- 0 (calc-cum-pl trades-loss))
        ;_ (println "profit: " pl-profit " loss:" pl-loss)
        ]

    (if (> pl-loss 0)
      (/ pl-profit pl-loss)
      nil)))


(defn trading-stats
  "calculates summary statistics from a list of trades"
  [trades]
  (let [
        nr (count trades)
        cum-pl (calc-cum-pl trades)
        ]
     {:nr nr
      :cum-pl cum-pl
      :avg (/ cum-pl nr)
      :profit-factor (profit-factor trades)
     }))

(defn long? [trade]
  (= (:direction trade) :long))

(defn by-direction [trades]
  (let [trades-long (filter long? trades)
        trades-short (remove long? trades)
        stats-long (trading-stats trades-long)
        stats-short (trading-stats trades-short)]
	{:long stats-long
     :short stats-short}))
    


(defn run-modified-parameter
  "modifies specified parameters and runs the simulation
   example:
   (run-modified-parameter :pivot-long -50 :price-ema 10)
  "
  [& modifications]
  (let [modified-params (apply assoc-in params modifications )]
    (-> (waves modified-params price-series)
        (gorilla-trade)  ; wave-trade
        (trading-stats)
      	(assoc :p modifications)
      )))

;(run-modified-parameter :price-ema 15)

(by-direction trades)

;;; ## Iterate over one Parameter

(clojure.pprint/print-table [:p :nr :cum-pl :avg :profit-factor]
  (map #(run-modified-parameter [:price-ema] %) (range 2 30 2)))


;;; ## Iterate over multiple Parameters

;;; threshold levels

(clojure.pprint/print-table [:p :nr :cum-pl :avg :profit-factor]
  (map #(run-modified-parameter [:pivot-lookback :pivot-short :prior-min] %) (range 10 90 5)))

(clojure.pprint/print-table [:p :nr :cum-pl :avg :profit-factor]
  (map #(run-modified-parameter [:pivot-lookback :pivot-long :prior-max] %) (range -10 -90 -5)))

; SHOWS CLEARLY THAT THE PRIOR LIMITS SHOULD BE MORE EXTREME.


(clojure.pprint/print-table [:p :nr :cum-pl :avg :profit-factor]
  (map #(run-modified-parameter [:pivot-lookback :pivot-short :current-max] %) (range 10 90 5)))

(clojure.pprint/print-table [:p :nr :cum-pl :avg :profit-factor]
  (map #(run-modified-parameter [:pivot-lookback :pivot-long :current-min] %) (range -10 -90 -5)))

; NO TREND ON THIS ONE


(clojure.pprint/print-table [:p :nr :cum-pl :avg :profit-factor]
  (map #(run-modified-parameter [:pivot-lookback :window] %) (range 50 400 10)))

; lookback window should be bigger to have more trades.
; does not have huge impact on PL
; lookback-window better 300; 50 definitely too small.

(clojure.pprint/print-table [:p :nr :cum-pl :avg :profit-factor]
  (for [price-ema (range 2 30 2)
        oscillator-lt (range 4 20 2)]
    (run-modified-parameter :price-ema price-ema :oscillator-lt oscillator-lt)))
