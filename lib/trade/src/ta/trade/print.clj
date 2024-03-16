(ns ta.trade.print
  (:require
   [tablecloth.api :as tc]
   [ta.trade.metrics.roundtrip-stats :refer [calc-roundtrip-stats]]
   [ta.trade.metrics.nav :refer [nav]]
   [ta.helper.print :refer [print-all]]))

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

(defn print-roundtrips [roundtrip-ds]
  (-> roundtrip-ds
      (roundtrips-view)
      (print-all)))

(defn print-roundtrips-pl-desc [roundtrip-ds]
  (-> roundtrip-ds
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