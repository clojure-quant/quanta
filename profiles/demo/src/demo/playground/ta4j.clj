(ns demo.playground.ta4j
  (:require
   [ta.series.indicator :as ind]
   [ta.warehouse :as wh]
   [ta.series.ta4j :as ta4j]))

; test our indicators
(ind/sma 2 [1 1 2 2 3 3 4 4 5 5])

    ; test calculating simple indicators
(let [ds (wh/load-symbol :crypto  "D" "ETHUSD")
      bars (ta4j/ds->ta4j-ohlcv ds)
      close (ta4j/ds->ta4j-close ds)]
  (-> (ta4j/ind :ATR bars 14) (ta4j/ind-values))
  (-> (ta4j/ind :SMA close 14) (ta4j/ind-values)))


