(ns ta.trade.nav.grouped
  (:require
   [clojure.set]
   [tablecloth.api :as tc]
   [tech.v3.datatype.functional :as fun]
   [ta.indicator.date :as dt]
   [ta.indicator.drawdown :refer [trailing-sum drawdowns-from-value]]
 ))

(defn- aggregate-roundtrips [ds-study group-by]
  (-> ds-study
      (tc/group-by group-by)
      (tc/aggregate {:trades (fn [ds]
                               (tc/row-count ds))
                     :ret-log (fn [ds]
                                (fun/sum (:ret-log ds)))})))

(defn grouped-nav [roundtrip-perf-ds]
  (assert (:ret-log roundtrip-perf-ds) "to calc grouped-nav :ret-log column needs to be present!")
  (assert (:nav  roundtrip-perf-ds) "to calc nav-metrics :nav column needs to be present!")
  (let [;{:keys [date close position]} backtest-ds
        ;_ (println "GROUPED NAV : add columns ...")
        ;_ (println roundtrip-perf-ds)
        ds (-> roundtrip-perf-ds
               (tc/rename-columns {:exit-date :date})
               dt/add-year
               dt/add-month)
        ;_ (println "CALC NAV-STATS *************")
        ds-by-month (aggregate-roundtrips ds [:year :month])
        ;_ (println "ds-grouped: " ds-by-month)
        ;_ (println "CALC trailing sum *************")
        cum-ret-log (trailing-sum (:ret-log ds-by-month))
        nav (fun/+ cum-ret-log 2.0)
        nav-px (fun/pow 10 nav)]
    (-> ds-by-month
        (tc/add-columns
         {:cum-ret-log cum-ret-log
          :nav nav-px
          :drawdown (drawdowns-from-value cum-ret-log)
          })
        (tc/select-columns [:year :month
                            :ret-log :trades
                            :cum-ret-log :nav :drawdown ]))))

