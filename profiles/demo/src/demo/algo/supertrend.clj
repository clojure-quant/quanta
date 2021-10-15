(ns demo.algo.supertrend
  (:require
   [tech.v3.datatype.functional :as fun]
   [tablecloth.api :as tablecloth]
   [ta.series.ta4j :as ta4j]
   [ta.helper.ago :refer [xf-ago]]))

(defn calc-atr
  "calculates ATR for the price series in dataset"
  [ds atr-length]
  (let [; input data needed for ta4j indicators
        bars (ta4j/ds->ta4j-ohlcv ds)
        ;close (ta4j/ds->ta4j-close ds)
        ; setup the ta4j indicators
        atr (ta4j/ind :ATR bars atr-length)
        atr-values  (ta4j/ind-values atr)]
    atr-values))

(defn calc-supertrend-signal [close upper-1 lower-1]
  (if close
    (cond
      (and upper-1 (> close upper-1)) :buy
      (and lower-1 (< close lower-1)) :sell
      :else :hold)
    :hold))

(comment
  (calc-supertrend-signal nil 11 9)
  (calc-supertrend-signal 12 11 9) ; :long   (close above upper)
  (calc-supertrend-signal 10 11 9) ; nil     (between the bands)
  (calc-supertrend-signal 9 11 9)  ; nil     (right on the lower band)
  (calc-supertrend-signal 8 11 9)  ; :short  (close belwo lower)

  (map calc-supertrend-signal [12 10 9 8] [11 11 11 11] [9 9 9 9]) ; (:long nil nil :short)
  (map calc-supertrend-signal [12 10 9 8 1] [11 nil 11 11] [9 9 9 9]) ; (:long nil nil :short)

 ; 
  )

(defn supertrend-signal [ds {:keys [atr-length atr-mult] #_:as #_options}]
  (let [atr (calc-atr ds atr-length)
        close (:close ds)
        ; Lower  = close - atr * atr-mult
        ; upper = close + atr * atr-mult
        ; this is the ocrrect formula, but it requires past values
        ; so we stay with the simpler formula above.
        ; upper = (min (+ close (* atr multp))
        ;               upper-1
        ;               (- upper-1 (* multp * (atr-1 atr))))
        ;  (-> (c* atr mult)
        ;       (+ close))
        atr-v (into [] atr)
        atr-scaled  (fun/* atr-v atr-mult) ; (into [] (map (*-const atr-mult) atr-v))
        upper (fun/+ close atr-scaled)
        ; upper-cached (dtype/clone upper)
        lower (fun/- close atr-scaled)
        upper-1  (into [] xf-ago upper)
        lower-1  (into [] xf-ago lower)
        signal (into [] (map calc-supertrend-signal close upper-1 lower-1))
        ;signal  (dtype/emap calc-supertrend-signal :object close upper-1 lower-1)
        ]
    (tablecloth/add-columns ds {:signal signal
                                :lower lower
                                :upper upper
                                ; debugging
                                :atr atr
                                :atr-scaled atr-scaled
                                :lower-1 lower-1
                                :upper-1 upper-1})))

