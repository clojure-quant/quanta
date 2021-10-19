(ns ta.backtest.nav
  (:require
   [clojure.set]
   [tablecloth.api :as tc]
   [tech.v3.datatype.functional :as dfn]
   [ta.backtest.drawdown :refer [trailing-sum drawdowns-from-value]]
   [ta.backtest.date :as d]
   [ta.backtest.position-pl :refer [position-pl]]))

(defn nav-stats [ds-study group-by]
  (-> ds-study
      (tc/group-by group-by)
      (tc/aggregate {;:bars (fn [ds]
                     ;        (->> ds
                     ;             :bars
                     ;             (apply +)))
                     :trades (fn [ds]
                               (->> ds
                                    :trade
                                    (remove nil?)
                                    count))
                     ; log
                     :pl-log-cum (fn [ds]
                                   (->> ds
                                        :pl-log
                                        (apply +)))} {:drop-missing? false})))

(defn nav [backtest-result]
  (let [ds-study (:ds-study backtest-result)
        ds-study-pl (tc/add-column ds-study
                                   :pl-log
                                   (position-pl (:close ds-study) (:position ds-study)))
        ds-by-month (-> ds-study-pl
                        (tc/add-columns
                         {:year  (d/year (:date ds-study))
                          :month (d/month (:date ds-study))})
                        (nav-stats [:year :month]))
        cum-pl-t (trailing-sum (:pl-log-cum ds-by-month))
        nav (dfn/+ cum-pl-t 2.0)
        nav-px (dfn/pow 10 nav)]
    (-> ds-by-month
        (tc/add-columns
         {:cum-pl-t cum-pl-t
          :drawdown (drawdowns-from-value cum-pl-t)
          :nav nav-px})
        (tc/select-columns [:year :month :pl-log-cum :cum-pl-t :drawdown :nav :trades]))))

(defn nav-metrics [backtest-result]
  (let [ds-study (:ds-study backtest-result)
        pl-log (position-pl (:close ds-study) (:position ds-study))
        cum-pl (trailing-sum pl-log)
        dd (drawdowns-from-value cum-pl)
        max-dd (apply max dd)
        cum-pl-last (last cum-pl)
          ;ds-study-pl (tc/add-columns ds-study
          ;                            {:pl-log pl-log
          ;                             :pl-cum cum-pl
          ;                             :dd dd})
        ]
    {:cum-pl cum-pl-last
     :max-dd max-dd}))