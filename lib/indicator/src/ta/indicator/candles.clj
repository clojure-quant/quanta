(ns ta.indicator.candles
  (:require
   [tech.v3.datatype.functional :as dfn]
   [ta.indicator :as ind]))

(defn doji
  "A candle is considered Doji if its body height is lower than the average
   multiplied by a factor.
   http://stockcharts.com/school/doku.php?id=chart_school:chart_analysis:introduction_to_candlesticks#doji</a>
   input: options + bar-ds
   output: column with doji-signal (boolean)"
  [n k bar-ds]
  (assert n "doji needs n parameter")
  (assert k "doji needs k parameter")
  (let [open-close (dfn/- (:close bar-ds) (:open bar-ds))
        avg-open-close (ind/sma {:n n} open-close)
        avg-open-close-k (dfn/* avg-open-close k)
        prior-avg-open-close-k (ind/prior avg-open-close-k)]
    (dfn/< open-close prior-avg-open-close-k)))

(defn doji-absolute
  "doji is a bar with  Big range + small movement.
   input: options + bar-ds
   output: column with doji-signal"
  [max-open-close-over-low-high bar-ds]
  (assert max-open-close-over-low-high "doji needs max-open-close-over-low-high parameter")
  (let [low-high (dfn/- (:high bar-ds) (:low bar-ds))
        open-close (dfn/- (:close bar-ds) (:open bar-ds))
        open-close-over-low-high (dfn// open-close low-high)]
    (dfn/< open-close-over-low-high max-open-close-over-low-high)))


