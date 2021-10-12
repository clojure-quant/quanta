(ns ta.dataset.supertrend
  (:require
   [taoensso.timbre :refer [trace debug info error]]
   [tick.alpha.api :as tick]
   [tech.v3.dataset :as tds]
   [tech.v3.dataset :as dataset]
   [tech.v3.datatype.functional :as fun]
   [tech.v3.datatype :as dtype]
   [tech.v3.dataset.print :refer [print-range]]
   [tablecloth.api :as tablecloth]
   [fastmath.core :as math]
   [fastmath.stats :as stats]
   [ta.series.ta4j :as ta4j]
   [ta.dataset.backtest :as backtest]
   [ta.warehouse :as wh]
   [ta.data.date :as dt]
   [ta.dataset.transducer :refer [row-major into-dataset]]
   [ta.xf.ago :refer [xf-ago xf-future]]
   [ta.dataset.trading :refer [signal->trade signal->position  trade->trade-no]]))

(defn calc-atr
  "calculates ATR for the price series in dataset"
  [ds atr-length]
  (let [; input data needed for ta4j indicators
        bars (ta4j/ds->ta4j-ohlcv ds)
        close (ta4j/ds->ta4j-close ds)
        ; setup the ta4j indicators
        atr (ta4j/ind :ATR bars atr-length)
        atr-values  (ta4j/ind-values atr)]
    atr-values))

(defn calc-supertrend-signal [close upper lower]
  (if close
    (cond
      (and upper (> close upper)) :buy
      (and lower (< close lower)) :sell
      :else :hold)
    :hold))

(defn *-const [c]
  (fn [v]
    (when v
      (* v c))))

(comment
  (calc-supertrend-signal 12 11 9) ; :long   (close above upper)
  (calc-supertrend-signal 10 11 9) ; nil     (between the bands)
  (calc-supertrend-signal 9 11 9)  ; nil     (right on the lower band)
  (calc-supertrend-signal 8 11 9)  ; :short  (close belwo lower)

  (map calc-supertrend-signal [12 10 9 8] [11 11 11 11] [9 9 9 9]) ; (:long nil nil :short)

  (into [] xf-ago [10 11 12 13])

  ((*-const 2) 20)
  (map (*-const 2) [1 2 3])
  (map (*-const 2) [nil 2 3])
  (map calc-supertrend-signal [12 10 9 8 1] [11 nil 11 11] [9 9 9 9]) ; (:long nil nil :short)

 ; 
  )
(defn study-supertrend [ds {:keys [atr-length atr-mult] :as options}]
  (let [atr (calc-atr ds atr-length)
        close (:close ds)
        ; Lower  = close - atr * atr-mult
        ; upper = close + atr * atr-mult
        ; this is the ocrrect formula, but it requires past values
        ; so we stay with the simpler formula above.
        ; upper = (min (+ close (* atr multp))
        ;               upper-1
        ;               (- upper-1 (* multp * (atr-1 atr))))
        atr-v (into [] atr)
        atr-scaled (into [] (map (*-const atr-mult) atr-v))
        upper (fun/+ close atr-scaled)
        lower (fun/- close atr-scaled)
        close-f1  (into [] xf-future close)
        chg (fun/- close-f1 close)
        chg-p (fun// chg close)
        chg-p (into [] (map (*-const 100.0) chg-p))
        upper-1  (into [] xf-ago upper)
        lower-1  (into [] xf-ago lower)
        signal (into [] (map calc-supertrend-signal close upper-1 lower-1))
        trade (signal->trade signal)
        trade-no (trade->trade-no trade)
        position (signal->position signal)]
    (tablecloth/add-columns ds {:index (backtest/running-index-vec ds)
                                :signal signal
                                :trade trade
                                :trade-no trade-no
                                :position position
                                :chg chg
                                :chg-p chg-p
                                :lower-1 lower-1
                                :upper-1 upper-1
                                :atr atr
                                :atr-scaled atr-scaled
                                :lower lower
                                :upper upper})))

(comment

  (signal->trade [:hold :buy :buy])

  (-> (tds/->dataset {:date [(tick/now) (tick/now) (tick/now)]
                      :open [1 2 3]
                      :high [1 2 3]
                      :low [1 2 3]
                      :close [1 2 3]
                      :volume [0 0 0]})
      (study-supertrend {:atr-length 10
                         :atr-mult 0.5}))

;  
  )
(defn add-bar-indicator [ds add-col-kw indicator]
  (tablecloth/add-column ds add-col-kw indicator))


