(ns ta.trade.print
  (:require
   [tablecloth.api :as tc]
   [ta.trade.metrics.roundtrip-stats :refer [calc-roundtrip-stats]]
   [ta.trade.metrics.nav :refer [nav]]
   [ta.helper.print :refer [print-all]]
   [ta.viz.table :as viz]))

;; ROUNDTRIPS

(def cols-rt
  [:rt-no
   :trade
   :pl-log :win
   :date-open :date-close :bars
   :price-open :price-close
   ;:index-open :index-close
   ])

(defn- roundtrips-view [ds-rt]
  (tc/select-columns ds-rt cols-rt))

(defn print-roundtrips [backtest-result]
  (-> (:ds-roundtrips backtest-result)
      (roundtrips-view)
      (print-all)))

(defn print-roundtrips-pl-desc [backtest-result]
  (-> (:ds-roundtrips backtest-result)
      (tc/order-by :pl-log)
      (roundtrips-view)
      (print-all)))

;; PRINT ROUNDTRIP-STATS

(defn- calc-roundtrip-stats-print [backtest-result group-by]
  (-> backtest-result
      (calc-roundtrip-stats group-by)
      (print-all)
      println))

(defn print-overview-stats [backtest-result]
  (calc-roundtrip-stats-print (:ds-roundtrips backtest-result) :position))

(defn print-roundtrip-stats [backtest-result]
  (calc-roundtrip-stats-print (:ds-roundtrips backtest-result) :position)
  (calc-roundtrip-stats-print (:ds-roundtrips backtest-result) [:position :win]))

(defn print-nav [backtest-result]
  (-> (nav backtest-result)
      (print-all)))

(defn viz-roundtrips [backtest-result]
  (let [ds-rt (:ds-roundtrips backtest-result)
        ds-view (roundtrips-view ds-rt)]
    ^:R [:p/aggrid
         {:box :lg
          :rowData (viz/ds->table ds-view)
          :columnDefs [{:field "rt-no" :headerName "rt-no" :resizable true :sortable true :filter true}
                       {:field "trade" :headerName "dir" :resizable true :sortable true :filter true}
                       {:field "date-open" :headerName "dt-open" :resizeable true}
                       {:field "price-open" :headerName "px-open" :resizable true :sortable true :filter true}
                       {:field "date-close" :headerName "dt-close" :resizable true :sortable true :filter true}
                       {:field "price-close" :headerName "px-close" :resizable true :sortable true :filter true}
                       {:field "pl-log" :headerName "pl" :resizable true :sortable true :filter true}
                       {:field "index-open" :headerName "idx-open" :resizable true :sortable true :filter true}
                       {:field "index-close" :headerName "idx-close" :resizable true :sortable true :filter true}]}]))

