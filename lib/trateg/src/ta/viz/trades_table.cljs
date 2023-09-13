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
  [aggrid {:box :fl
           :data trades
           :columns [{:field :symbol}
                     {:field :side :headerName "side"}
                     {:field :qty}
                     {:field :entry-date :format fmt-yyyymmdd :headerName "dt-E"}
                     {:field :entry-vol :format #(round-number-digits 0 %) :headerName "dt-E"}
                     {:field :exit-date :format fmt-yyyymmdd :headerName "dt-X"}
                     {:field :entry-price :headerName "px-E"}
                     {:field :exit-price :headerName "px-X"}
                     {:field :pl :format #(round-number-digits 0 %) :type "rightAligned"}]
           :pagination :false
           :paginationAutoPageSize true}])

(defn trades-table-live [trades]
  [aggrid {:box :fl
           :data trades
           :columns [{:field :account}
                     {:field :symbol}
                     {:field :side :headerName "side"}
                     {:field :qty}
                     {:field :entry-date :format fmt-yyyymmdd :headerName "dt-E"}
                     {:field :entry-price :headerName "px-E"}
                     {:field :entry-vol :format #(round-number-digits 0 %) :headerName "dt-E"}
                                      
                     {:field :current-price :type "rightAligned" }
                     {:field :current-pl :type "rightAligned"}
                     ]
           :pagination :false
           :paginationAutoPageSize true}])