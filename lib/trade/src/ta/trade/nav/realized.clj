(ns ta.trade.nav.realized
  (:require
   [clojure.set]
   [tablecloth.api :as tc]
   [tech.v3.datatype.functional :as dfn]
   [ta.indicator.date :as dt]
   [ta.trade.drawdown :refer [trailing-sum drawdowns-from-value]]
   [ta.trade.position-pl :refer [position-pl]]))

(defn- nav-stats [ds-study group-by]
  (-> ds-study
      (tc/group-by group-by)
      (tc/aggregate {:trades (fn [ds]
                               (->> ds
                                    :trade
                                    (remove nil?)
                                    count))
                     :pl-log-cum (fn [ds]
                                   (->> ds
                                        :pl-log
                                        (apply +)))} {:drop-missing? false})))

(defn realized-nav [backtest-ds]
  (let [;{:keys [date close position]} backtest-ds
        date (:date backtest-ds)
        close (:position backtest-ds)
        position (:position backtest-ds)
        _ (println " add columns ...")
        _ (println backtest-ds)
        ds (-> backtest-ds
               dt/add-year
               dt/add-month
              ;(tc/add-column :pl-log (position-pl close position))
            )
        _ (println "*************")
        ds-by-month (nav-stats ds [:year :month])
        cum-pl-t (trailing-sum (:pl-log-cum ds-by-month))
        nav (dfn/+ cum-pl-t 2.0)
        nav-px (dfn/pow 10 nav)]
    (-> ds-by-month
        (tc/add-columns
         {:cum-pl-t cum-pl-t
          :drawdown (drawdowns-from-value cum-pl-t)
          :nav nav-px})
        (tc/select-columns [:year :month :pl-log-cum :cum-pl-t :drawdown :nav :trades]))))
