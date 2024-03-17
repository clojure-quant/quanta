(ns ta.viz.trade.trades-table
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

(defn trades-table-impl [trades size]
  [aggrid {:box size
           :data trades
           :columns [{:field :symbol :width 50}
                     {:field :side :headerName "side" :width 50}
                     {:field :qty :width 50}
                     {:field :entry-date :format fmt-yyyymmdd :headerName "dt-E" :width 50}
                     {:field :entry-vol :format #(round-number-digits 0 %) :headerName "dt-E" :width 50}
                     {:field :exit-date :format fmt-yyyymmdd :headerName "dt-X" :width 50}
                     {:field :entry-price :headerName "px-E" :width 50}
                     {:field :exit-price :headerName "px-X" :width 50}
                     {:field :pl :format #(round-number-digits 0 %) :type "rightAligned" :width 50}]
           :pagination :false
           :paginationAutoPageSize true}])

(defn trades-table [trades]
  [trades-table-impl trades :fl])

(defn trades-table-lg [trades]
  [trades-table-impl trades :lg])

(def class-rules
  {"bg-green-500" (fn [params] (> (.-value params) 0.0))
   "bg-red-500" (fn [params] (< (.-value params) 0.0))})

(def class-rules-js (clj->js class-rules))

(defn trades-table-live [trades]
  [aggrid {:box :fl
           :data trades
           :columns [{:field :account :width 80}
                     {:field :symbol :width 80}
                     {:field :side :headerName "side" :width 70}
                     {:field :qty :width 70 :type "rightAligned"}
                     {:field :entry-date :format fmt-yyyymmdd :headerName "dt-E" :width 100}
                     {:field :entry-price :headerName "px-E" :width 80 :type "rightAligned"}
                     {:field :entry-vol :format #(round-number-digits 0 %) :headerName "dt-E" :width 80 :type "rightAligned"}

                     {:field :current-price :type "rightAligned" :width 80}
                     {:field :current-pl :type "rightAligned" :width 80 :cellClassRules class-rules-js}]
           :pagination :false
           :paginationAutoPageSize true}])