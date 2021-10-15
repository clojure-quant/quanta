(ns ta.backtest.roundtrip-stats
  (:require
   [tablecloth.api :as tc]
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


