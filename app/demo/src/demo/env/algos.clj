(ns demo.env.algos
  (:require
   ; algos
   [ta.algo.buy-hold :refer [buy-hold-signal]]
   [ta.gann.algo :refer [algo-gann algo-gann-signal]]
   [demo.algo.moon :refer [moon-signal]]
   [demo.algo.supertrend :refer [supertrend-signal]]
   [demo.algo.sma :refer [sma-signal]]
   [demo.algo.sma-diff :refer [sma-diff-indicator]]))

;; this ns is purely side effects ...