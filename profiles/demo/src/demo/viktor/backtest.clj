(ns demo.viktor.backtest
  (:require
   [taoensso.timbre :refer [trace debug info error]]
   [tick.alpha.api :as t]
   [tech.v3.dataset.print :as print]
   [tech.v3.dataset :as tds]
   [tech.v3.datatype.datetime :as datetime]
   [tablecloth.api :as tablecloth]
   [ta.dataset.helper :as helper]
   [ta.series.indicator :as ind]
   [ta.series.ta4j :as ta4j]
   [ta.warehouse :as wh]
   [demo.env.warehouse :refer [w]]
   [demo.studies.helper.experiments-helpers :as experiments-helpers]))

(defn make-filename [frequency symbol]
  (str symbol "-" frequency))

(defn study-bollinger [ds {:keys [sma-length stddev-length mult-up mult-down] :as options}]
  "adds bollinger indicator to dataset
   * Middle Band = 20-day simple moving average (SMA)
   * Upper Band = 20-day SMA + (20-day standard deviation of price x 2) 
   * Lower Band = 20-day SMA - (20-day standard deviation of price x 2) 
   "
  (let [; input data needed for ta4j indicators
        bars (ta4j/ds->ta4j-ohlcv2 ds)
        close (ta4j/ds->ta4j-close ds)
      ; setup the ta4j indicators
        sma (ta4j/ind :SMA close sma-length)
        stddev (ta4j/ind :statistics/StandardDeviation close stddev-length)
        bb-middle (ta4j/ind :bollinger/BollingerBandsMiddle sma)
        bb-upper (ta4j/ind :bollinger/BollingerBandsUpper bb-middle stddev (ta4j/num-decimal mult-up))
        bb-lower (ta4j/ind :bollinger/BollingerBandsLower bb-middle stddev (ta4j/num-decimal mult-down))
      ; calculate the indicators
        bb-upper-values  (ta4j/ind-values bb-upper)
        bb-lower-values  (ta4j/ind-values bb-lower)]
    (-> ds
        (tablecloth/add-column :bb-lower bb-lower-values)
        (tablecloth/add-column :bb-upper bb-upper-values))))

; study runner (will go to ta library)

(defn make-study-filename [study-name frequency symbol]
  (str "study-" study-name "-" symbol "-" frequency))

(defn run-study [symbol frequency algo options study-name]
  (let [ds (wh/load-ts w (make-filename frequency symbol))
        ds-study (algo ds options)]
    (wh/save-ts w ds-study (make-study-filename study-name frequency symbol))
    ds-study))

(defn load-study [symbol frequency study-name]
  (let [ds (wh/load-ts w (make-study-filename study-name frequency symbol))]
    ds))

; bollinger band (moving average + std dev) upper-band lower-band
; event finden: close>upper oder close<lower => seqence of index.
; filter events, sodass nur alle 30 bars ein event stattfindet
; walk-forward window anlegen
; max/min innerhalb des walk-forward window finden
; max/min relativ zur range normalisieren
; ist der move ge-skewed (up fuer cross lower-channel, down fuer cross upper-channel)
; optimizer fuer beste parameter.

; event bollinger cross     ==> liste of event-bollinger-cross
; cross-type #{:up :down}
; up
; down
; up%
; down%
; diff   (up-down)
; diff%  (up% - down%)

; target funktion
; for cross-type-up: average diff% 
; for cross-type-down: (-average diff%)

(defn find-cross-up
  [ds]
  "returns a seq of vecs
   [idx date close bollinger-up bollinger-down]")

(api/dataset [[:A [1 2 3 4 5 6]] [:B "X"] [:C :a]])

(defn find-cross-down)

(defn make-window
  “Takes a look-forward window out of a dataframe.
  Returns nil if full window not possible
  Start is after the event”
  [df idx forward-size]
  (assert (and (df? df) (int? Idx) (int? forward-size))
          (when (< (size df) (+ idx forward-size))
            (select-cols df idx (+ idx forward-size)))))

(defn calc-channel-turningpoint-event
  [df idx dir-label forward-size]
  (assert (df-has-cols df #{:close :high :low :chan-up :chan-down})
          (when-let [w (window df idx forward-size)] ; at end of series window might be nil. 
            (let [;[p band-up band-down]  (cols df idx [:close :chan-up :chan-down])
                  r (- band-up band-down)
                  h (high (:high w))
                  l (low (:low w))]))))

; (let [df  :close

(defn events-goodness
  [df bollinger-events]
  "input: sequence of bollinger events
   output: value that can be put to the optimizer (average difference of range)")

(comment

   ; test our indicators
  (ind/sma 2 [1 1 2 2 3 3 4 4 5 5])

    ; test calculating simple indicators
  (let [ds (wh/load-ts w (make-filename "D" "ETHUSD"))
        bars (ta4j/ds->ta4j-ohlcv2 ds)
        close (ta4j/ds->ta4j-close ds)]
    (-> (ta4j/ind :ATR bars 14) (ta4j/ind-values))
    (-> (ta4j/ind :SMA close 14) (ta4j/ind-values)))

  ; calculate bollinger strategy
  (let [ds (wh/load-ts w (make-filename "D" "ETHUSD"))]
    (study-bollinger ds {:sma-length 20
                         :stddev-length 20
                         :mult-up 1.5
                         :mult-down 1.5}))

; study interface
  (run-study "ETHUSD" "D" study-bollinger {:sma-length 20
                                           :stddev-length 20
                                           :mult-up 1.5
                                           :mult-down 1.5} "bollinger")

  (load-study  "ETHUSD" "D" "bollinger")

;  
  )

