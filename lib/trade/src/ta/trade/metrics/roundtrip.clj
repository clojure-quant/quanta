(ns ta.trade.metrics.roundtrip
  (:require
   [clojure.set]
   [tablecloth.api :as tc]
   [tech.v3.dataset :as tds]
   [ta.math.stats :refer [mean]]
   [ta.trade.drawdown :refer [max-drawdown]]))

(defn calc-roundtrip-stats [roundtrips-ds group-by]
  (-> roundtrips-ds
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
                                          max-drawdown))} {:drop-missing? false})
      (tc/set-dataset-name (tc/dataset-name roundtrips-ds))))

(defn position-stats [roundtrips-ds]
  (as-> roundtrips-ds x
    (calc-roundtrip-stats x [:position])
    (tds/mapseq-reader x)
    (map (juxt :position identity) x)
    (into {} x)))

(defn win-loss-stats [roundtrips-ds]
  (as-> roundtrips-ds x
    (calc-roundtrip-stats x [:win])
    (tds/mapseq-reader x)
    (map (juxt :win identity) x)
    (into {} x)
    (clojure.set/rename-keys x {true :win
                                false :loss})))

(defn win-loss-performance-metrics [win-loss-stats]
  (let [win (:win win-loss-stats)
        loss (:loss win-loss-stats)
        pl-log-cum (+ (:pl-log-cum win)
                      (:pl-log-cum loss)) ; loss is negative, so add
        trade-count-all (+ (:trades win) (:trades loss))
        win-prct  (/ (:trades win) trade-count-all)
        loss-prct (- 1.0 win-prct)]
    {:trades trade-count-all
     :pl-log-cum pl-log-cum
     :pf (/  (* win-prct (:pl-log-mean win))
             (* loss-prct (- 0 (:pl-log-mean loss))))
     :avg-log (/ pl-log-cum  (float trade-count-all))
     :avg-win-log (:pl-log-mean win)
     :avg-loss-log (:pl-log-mean loss)
     :win-nr-prct (* 100.0 win-prct)
     :avg-bars-win  (if (> (:trades win) 0.0)
                      (* 1.0 (/ (:bars win) (:trades win)))
                      0.0)
     :avg-bars-loss (if (> (:trades loss) 0.0)
                      (* 1.0 (/ (:bars loss) (:trades loss)))
                      0.0)}))

(defn roundtrip-metrics [roundtrips-ds]
  (let [wl-stats (win-loss-stats roundtrips-ds)
        metrics (win-loss-performance-metrics wl-stats)]
    metrics))

; {:drop-missing? false} as an option

(def cols-rt-stats [:p
                    :trades
                    :pf :pl-log-cum :avg-log
                    :win-nr-prct
                    :avg-win-log :avg-loss-log
                    :avg-bars-win :avg-bars-loss])

;; used in trateg.demo:

(defn backtests->performance-metrics [backtest-results]
  (as-> (map roundtrip-metrics backtest-results) x
    (apply tc/concat x)
    (tc/select-columns x cols-rt-stats)
       ;(reduce tc/append (tc/dataset {}))
    ))

