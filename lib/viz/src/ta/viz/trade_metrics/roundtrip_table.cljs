(ns ta.viz.trade-metrics.roundtrip-table
  (:require
    [goldly.js :refer [to-fixed]]
    [tick.goldly]
    [tick.core :as tick]
    [ui.aggrid :refer [aggrid]]))

(defn roundtrip-table [roundtrips]
  [aggrid {:box :lg
           :data roundtrips
           :columns [{:field "rt-no" :headerName "rt-no" :resizable true :sortable true :filter true}
                       {:field "trade" :headerName "dir" :resizable true :sortable true :filter true}
                       {:field "date-open" :headerName "dt-open" :resizeable true}
                       {:field "price-open" :headerName "px-open" :resizable true :sortable true :filter true}
                       {:field "date-close" :headerName "dt-close" :resizable true :sortable true :filter true}
                       {:field "price-close" :headerName "px-close" :resizable true :sortable true :filter true}
                       {:field "pl-log" :headerName "pl" :resizable true :sortable true :filter true}
                       {:field "index-open" :headerName "idx-open" :resizable true :sortable true :filter true}
                       {:field "index-close" :headerName "idx-close" :resizable true :sortable true :filter true}]
           :pagination :false
           :paginationAutoPageSize true
          }])

