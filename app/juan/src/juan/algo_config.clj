(ns juan.algo-config
   (:require
   [ta.algo.permutate :refer [->assets]]))

(def juan-algo-base
  [{:asset "EUR/USD"
    :feed :fx}
   :us :d [{:trailing-n 100
            :binning-level 0.0001
            :take-top-prct 30}
           'trailing-window-bars :d
           'juan/volume-pivot-build]
   :us :m [{:trailing-n 60
            :doji-diff 20}
           'trailing-window-bars :m
           'juan/juan-algo]])

(def fx-assets
  ["EUR/USD" "GBP/USD" "EUR/JPY"
   "USD/JPY" "AUD/USD" "USD/CHF"
   "GBP/JPY" "USD/CAD" "EUR/GBP"
   "EUR/CHF" "NZD/USD" "USD/NOK"
   "USD/ZAR" "USD/SEK" "USD/MXN"])

;; TODO: this permutation does not work, as it is INSIDE a VECTOR!
(def algos-fx (->assets juan-algo-base fx-assets))