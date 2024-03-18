(ns ta.viz.trade.roundtrip-table
  (:require
    [goldly.js :refer [to-fixed]]
    [tick.goldly]
    [tick.core :as tick]
    [ui.aggrid :refer [aggrid]]))

(defn roundtrip-table [roundtrips]
  [aggrid {:box :fl ; :lg
           :data roundtrips
           :columns [{:field "id" :headerName "rt-no" :resizable true :sortable true :filter true}
                       {:field "side" :headerName "side" :resizable true :sortable true :filter true}
                       {:field "win?" :headerName "win?" :resizable true :sortable true :filter true}
                       {:field "bars" :headerName "#bars" :resizable true :sortable true :filter true}
                       {:field "ret-log" :headerName "ret log" :resizable true :sortable true :filter true}
                       {:field "ret-prct" :headerName "ret prct" :resizable true :sortable true :filter true}
                       {:field "nav" :headerName "nav" :resizable true :sortable true :filter true}
                       {:field "entry-date" :headerName "entry-dt" :resizeable true}
                       {:field "entry-price" :headerName "entry-px" :resizable true :sortable true :filter true}
                       {:field "exit-date" :headerName "exit-dt" :resizable true :sortable true :filter true}
                       {:field "exit-price" :headerName "exit-px" :resizable true :sortable true :filter true}
                       {:field "pl-log" :headerName "pl" :resizable true :sortable true :filter true}
                       {:field "entry-idx" :headerName "entry-idx" :resizable true :sortable true :filter true}
                       {:field "exit-idx" :headerName "exit-idx" :resizable true :sortable true :filter true}]
           :pagination :false
           :paginationAutoPageSize true
          }])
