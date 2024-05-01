(ns ta.viz.trade.format
  (:require
   [goldly.js :refer [to-fixed]]
   [tick.goldly]
   [tick.core :as t]))

(defn fmt-yyyymmdd [dt]
  (if dt
    (t/format (t/formatter "YYYY-MM-dd") dt)
    ""))

(defn round-number-digits
  [digits number] ; digits is first parameter, so it can easily be applied (data last)
  (if (nil? number) "" (to-fixed number digits)))

(def round-digit-0 (partial round-number-digits 0))

(def round-digit-1 (partial round-number-digits 1))

(def round-digit-2 (partial round-number-digits 2))

(defn align-right [_c]
  {:style {:float "right"}})