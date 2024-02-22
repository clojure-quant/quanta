(ns juan.algo-config
  (:require
   [ta.algo.permutate :refer [->assets]]))

(def settings
  {:atr-n 20 ; number days used for atr calculation
   :sentiment-treshold 70.0
   :spike-atr-min-prct 30.0 ; 75 (currently set lower for testing)
   :future "23Z" ; used ?
   :future-year 23 ; future year for volume pivots
   :future-month 12 ; future month for volume pivots
   :pivot-max-pip-distance 5.0})

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