(ns ta.backtest.print
  (:require
   [tablecloth.api :as tablecloth]
   [ta.backtest.roundtrip-stats :refer [calc-roundtrip-stats]]
   [ta.helper.print :refer [print-all]]))

;; PRINT

;; PRINT ROUNDTRIPS

(defn- print-roundtrips-view [ds-rt]
  (->  ds-rt
       (tablecloth/select-columns [:rt-no ; :$group-name
                                   :index-open :index-close
                                   :bars
                                   :trade
                                   :date-open :date-close
                                   :price-open :price-close
                                   :pl-log
                                   :pl-prct
                                   :win])
       (print-all)))

(defn print-roundtrips [backtest-result]
  (-> (:ds-roundtrips backtest-result)
      (print-roundtrips-view)))

(defn print-roundtrips-pl-desc [backtest-result]
  (-> (:ds-roundtrips backtest-result)
      (tablecloth/order-by :pl-log)
      (print-roundtrips-view)))

;; PRINT ROUNDTRIP-STATS

(defn- calc-roundtrip-stats-print [backtest-result group-by]
  (-> backtest-result
      (calc-roundtrip-stats group-by)
      (print-all)
      println))

(defn print-overview-stats [backtest-result]
  (calc-roundtrip-stats-print backtest-result :position))

(defn print-roundtrip-stats [backtest-result]
  (calc-roundtrip-stats-print backtest-result :position)
  (calc-roundtrip-stats-print backtest-result [:position :win]))

