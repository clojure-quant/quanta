(ns wave.core
  (:require
   [backtest.random :refer [random-series]]
   [backtest.indicator :refer [sma ema]]
   [backtest.series :refer :all]))

(defn nil-to-false [x]
  (if (nil? x) false x))

(defn bigger [p l c]
  (if (or (nil? p) (nil? l))
    false
    (and (> p l) (> p c))))

(defn past-higher?
  [c s]
  (let [[all l] (split-at (- (count s) 1) s)  ; all= all pivots except the last
        l (first l)]                        ; l = only the last pivot.
    (if (nil? l)
      false
      (nil-to-false (some #(bigger % l c) all)))
    ;l
    ))

(defn lower [p l c]
  (if (or (nil? p) (nil? l))
    false
    (and (< p l) (< p c))))

(defn past-lower?
  "true if:  series[i] < const
             series[i] < series[0]
  "
  [const series]
  (let [[prior current] (split-at (- (count series) 1) series)  ; all= all pivots except the last
        current (first current)]                        ; l = only the last pivot.
    (if (nil? current)
      false
      (nil-to-false (some #(lower % current const) prior)))))

(defn significant-pivot-short? [pivot-oscillator-lt params]
  (let [lookback-window (get-in params [:pivot-lookback :window])
        past-const (get-in params [:pivot-lookback :pivot-short :prior-min])
        current-min (get-in params [:pivot-lookback :pivot-short :current-min])
        current-max (get-in params [:pivot-lookback :pivot-short :current-max])
        ; match are boolean series
        match-past (series-roll (partial past-higher? past-const) lookback-window pivot-oscillator-lt)
        match-current (series-inrange current-min current-max pivot-oscillator-lt)
        match-both (series-and match-past match-current)]
    match-both))

(defn significant-pivot-long? [pivot-oscillator-lt params]
  (let [lookback-window (get-in params [:pivot-lookback :window])
        past-const (get-in params [:pivot-lookback :pivot-long :prior-max])
        current-min (get-in params [:pivot-lookback :pivot-long :current-min])
        current-max (get-in params [:pivot-lookback :pivot-long :current-max])
        ; match are boolean series
        match-past (series-roll (partial past-lower? past-const) lookback-window pivot-oscillator-lt)
        match-current (series-inrange current-min current-max pivot-oscillator-lt)
        match-both (series-and match-past match-current)]
    match-both))

(defn waves [params price-series]
  (let [price-ema  (ema (:price-ema params) price-series)
        delta (series-subtract price-series price-ema)
        delta-abs (series-abs delta)
        delta-abs-ema (ema (:price-ema params) delta-abs)
        oscillator (series-divide delta delta-abs-ema)
        oscillator (series-mult-c oscillator 66.0)
        oscillator-st (ema (:oscillator-lt params) oscillator)
        oscillator-lt (sma (:oscillator-st params) oscillator-st)
        oscillator-trend (start-at (series-subtract oscillator-st oscillator-lt) (:price-ema params))

        pivot-long? (cross-up oscillator-trend) ; (series-and (cross-up oscillator-trend) (series>0 oscillator-lt) )
        pivot-long-price (doall (series? price-series pivot-long?))
        pivot-long-oscillator-lt (doall (series? oscillator-lt pivot-long?))
        pivot-long-oscillator-trend (doall (series? oscillator-trend pivot-long?))

        pivot-short? (cross-down oscillator-trend) ; (series-and (cross-down oscillator-trend) (series<0 oscillator-lt) )
        pivot-short-price (doall (series? price-series pivot-short?))
        pivot-short-oscillator-lt (doall (series? oscillator-lt pivot-short?))
        pivot-short-oscillator-trend (doall (series? oscillator-trend pivot-short?))

        spivot-short?  (significant-pivot-short? pivot-short-oscillator-lt params)
        spivot-short-oscillator-lt (doall (series? oscillator-lt spivot-short?))
        spivot-short-price (doall (series? price-series spivot-short?))

        spivot-long?  (significant-pivot-long? pivot-long-oscillator-lt params)
        spivot-long-oscillator-lt (doall (series? oscillator-lt spivot-long?))
        spivot-long-price (doall (series? price-series spivot-long?))]

    {:price (vec price-series)
     :price-ema price-ema
     :delta delta
     :delta-abs (vec delta-abs)
     :oscillator {:v oscillator
                  :lt oscillator-lt
                  :st oscillator-st
                  :trend oscillator-trend}
     :pivot-long {:b pivot-long?
                  :price pivot-long-price
                  :oscillator-lt pivot-long-oscillator-lt
                  :oscillator-trend pivot-long-oscillator-trend}
     :pivot-short {:b pivot-short?
                   :price pivot-short-price
                   :oscillator-lt pivot-short-oscillator-lt
                   :oscillator-trend pivot-short-oscillator-trend}

     :spivot-long {:oscillator-lt spivot-long-oscillator-lt
                   :price spivot-long-price}

     :spivot-short {:oscillator-lt spivot-short-oscillator-lt
                    :price spivot-short-price}

;(series-roll higher-than-last? (:pivot-trend-lookback params) price-pivot-long )
     }))
; d = ema(abs(ap - esa), n1)      EMA of the Absolute Difference from EMA  [Channel Length]  “Avg Move Intensity”
  ; ci = (ap - esa) / (0.015 * d)       Oscillator of Current Deviation from EMA  66=same deviation as average
  ; 			 	  Current Difference from EMA, normalized with Average Difference.
  ; ci = (ap-esa) / d * 66.6
  ; tci = ema(ci, n2)                      EMA of Oscillator [Average Length]
  ; wt1 = tci
  ; wt2 = sma(wt1,4)                    SMA of EMA of Oscillator [4]

(comment

  (higher-than-last? [0 1 2 3 4 5 6 7 8 9])
  (higher-than-last? [-1 -3 -2])

  (def test-params {:price-ema 2   ; EMA on price-series

      ; Oscillator Trend Calculation -> The crossover determines the pivots
                    :oscillator-lt 8
                    :oscillator-st 4

                    :pivot-lookback {; compared are oscillator-lt (= wt2)
                                     :window 20	;sliding-lookback window to compare current pivot to prior pivots
                                     :pivot-short {:prior-min 40
                                                   :current-min 0
                                                   :current-max 40}
                                     :pivot-long {:prior-max 40
                                                  :current-min 0
                                                  :current-max 40}}})

  (backtest.print/print-map-of-vecs
   (waves test-params (random-series 300))
   [:price
    :price-ema
   ;:pivot-long
   ;[:pivot-long :price]
   ;[:pivot-long :oscillator-lt]
    [:spivot-long :price]
    [:spivot-short :price]]
   :all)

  (series-inrange  4 7 [nil 1 2 3 4 5 6 7 8 9 nil]))
