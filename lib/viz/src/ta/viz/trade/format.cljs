(ns ta.viz.trade.format
  (:require
   [tick.core :as t]))

(defn to-fixed [n d]
  (.toFixed n d))

(defn fmt-yyyymmdd [dt]
  (if dt
    (t/format (t/formatter "YYYY-MM-dd")  (t/zoned-date-time dt))
    ""))

(defn round-number-digits
  [digits number] ; digits is first parameter, so it can easily be applied (data last)
  (if (nil? number) "" (to-fixed number digits)))

(def round-digit-0 (partial round-number-digits 0))

(def round-digit-1 (partial round-number-digits 1))

(def round-digit-2 (partial round-number-digits 2))

(defn align-right [_c]
  {:style {:float "right"}})