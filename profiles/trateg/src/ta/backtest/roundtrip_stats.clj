(ns ta.backtest.roundtrip-stats
  (:require
   [clojure.set]
   [tablecloth.api :as tc]
   [tech.v3.dataset :as tds]
   [ta.helper.stats :refer [mean]]
   [ta.backtest.drawdown :refer [max-drawdown]]))

(defn calc-roundtrip-stats [backtest-result group-by]
  (let [ds-roundtrips (:ds-roundtrips backtest-result)]
    (-> ds-roundtrips
        (tc/group-by group-by)
        (tc/aggregate {:bars (fn [ds]
                               (->> ds
                                    :bars
                                    (apply +)))
                       :trades (fn [ds]
                                 (->> ds
                                      :trade
                                      (remove nil?)
                                      count))
                               ; log
                       :pl-log-cum (fn [ds]
                                     (->> ds
                                          :pl-log
                                          (apply +)))

                       :pl-log-mean (fn [ds]
                                      (->> ds
                                           :pl-log
                                           mean))

                       :pl-log-max-dd (fn [ds]
                                        (-> ds
                                            (tc/->array :pl-log)
                                            max-drawdown))

                               ; prct 
                       :pl-prct-cum (fn [ds]
                                      (->> ds
                                           :pl-prct
                                           (apply +)))
                       :pl-prct-mean (fn [ds]
                                       (->> ds
                                            :pl-prct
                                            mean))
                       :pl-prct-max-dd (fn [ds]
                                         (-> ds
                                             (tc/->array :pl-prct)
                                             max-drawdown))})
        (tc/set-dataset-name (tc/dataset-name ds-roundtrips)))))

(defn win-loss-stats [backtest-result]
  (as-> backtest-result x
    (calc-roundtrip-stats x [:win])
    (tds/mapseq-reader x)
    (map (juxt :win identity) x)
    (into {} x)
    (clojure.set/rename-keys x {true :win
                                false :loss})))

(defn win-loss-performance-metrics [win-loss-stats]
  (let [win (:win win-loss-stats)
        loss (:loss win-loss-stats)

        pf-log-diff (+ (:pl-log-cum win)
                       (:pl-log-cum loss))] ; loss is negative
    {:pf-log-diff pf-log-diff
     :pf (Math/pow pf-log-diff 10)
     :avg-win-log (:pl-log-mean win)
     :avg-loss-log (:pl-log-mean loss)
     :win-nr-prct (* 100.0 (/ (:trades win) (:trades loss)))
     :avg-bars-win  (* 1.0 (/ (:bars win) (:trades win)))
     :avg-bars-loss  (* 1.0 (/ (:bars loss) (:trades loss)))}))

(defn roundtrip-performance-metrics [backtest-result]
  (let [wl-stats (win-loss-stats backtest-result)
        metrics (win-loss-performance-metrics wl-stats)]
    metrics))