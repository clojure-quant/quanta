(ns ta.trade.nav.grouped
  (:require
   [clojure.set]
   [tablecloth.api :as tc]
   [tech.v3.datatype.functional :as dfn]
   [ta.indicator.date :as dt]
   [ta.indicator.drawdown :refer [trailing-sum drawdowns-from-value]]))

(defn- aggregate-roundtrips [ds-study group-by]
  (-> ds-study
      (tc/group-by group-by)
      (tc/aggregate {:trades (fn [ds]
                               (tc/row-count ds))
                     :ret-log (fn [ds]
                                (dfn/sum (:ret-log ds)))})))

(defn grouped-nav [roundtrip-perf-ds]
  (assert (:ret-log roundtrip-perf-ds) "to calc grouped-nav :ret-log column needs to be present!")
  (assert (:nav  roundtrip-perf-ds) "to calc nav-metrics :nav column needs to be present!")
  (let [;{:keys [date close position]} backtest-ds
        ;_ (println "GROUPED NAV : add columns ...")
        ;_ (println roundtrip-perf-ds)
        ds (-> roundtrip-perf-ds
               (tc/rename-columns {:exit-date :date})
               ;dt/add-year
               ;dt/add-month
               ;dt/add-year-month
               dt/add-year-month-day)
        ;_ (println "CALC NAV-STATS *************")
        ds-by-month (aggregate-roundtrips ds [:year-month-day #_:year-month #_:year #_:month])
        ;_ (println "ds-grouped: " ds-by-month)
        ;_ (println "CALC trailing sum *************")
        cum-ret-log (trailing-sum (:ret-log ds-by-month))
        nav (dfn/+ cum-ret-log 2.0)
        nav-px (dfn/pow 10 nav)]
    (-> ds-by-month
        (tc/add-columns
         {:cum-ret-log cum-ret-log
          :nav nav-px
          :drawdown (drawdowns-from-value cum-ret-log)})
        (tc/select-columns [:year-month-day ; :year-month ; :year :month
                            :ret-log :trades
                            :cum-ret-log :nav :drawdown]))))

