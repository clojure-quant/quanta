(ns ta.viz.trades-table
  (:require
   [goldly.js :refer [to-fixed]]
   [tick.goldly]
   [tick.core :as tick]
   [ui.aggrid :refer [aggrid]]))

(defn fmt-yyyymmdd [dt]
  (if dt
    (tick/format (tick/formatter "YYYY-MM-dd") dt)
    ""))

(defn round-number-digits
  [digits number] ; digits is first parameter, so it can easily be applied (data last)
  (if (nil? number) "" (to-fixed number digits)))


(defn trades-table [trades]
  [aggrid {:data trades
           :columns [{:field :symbol}
                     {:field :direction}
                     {:field :qty}
                     {:field :entry-date :format fmt-yyyymmdd}
                     {:field :exit-date :format fmt-yyyymmdd}
                     {:field :entry-price}
                     {:field :exit-price}
                     {:field :pl :format #(round-number-digits 0 %)}]
           :box :fl
           :pagination :false
           :paginationAutoPageSize true}])