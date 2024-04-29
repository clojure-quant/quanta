(ns ta.trade.roundtrip.metrics
  (:require
   [clojure.set]
   [tablecloth.api :as tc]
   [tech.v3.dataset :as tds]
   [tech.v3.datatype.functional :as dfn]
   [ta.indicator.drawdown :refer [max-drawdown]]))

(defn calc-roundtrip-stats [roundtrips-ds group-by]
  (-> roundtrips-ds
      (tc/group-by group-by)
      (tc/aggregate {:bars (fn [ds]
                             (dfn/sum (:bars ds)))
                     :trades (fn [ds]
                               (tc/row-count ds))
                     ; log
                     :pl-log-cum (fn [ds]
                                   (dfn/sum (:ret-log ds)))

                     :pl-log-mean (fn [ds]
                                    (dfn/mean (:ret-log ds)))

                     :pl-log-max-dd (fn [ds]
                                      (-> ds
                                          (tc/->array :ret-log)
                                          max-drawdown))}
                    {:drop-missing? false})
      (tc/set-dataset-name (tc/dataset-name roundtrips-ds))))

(defn side-stats [roundtrip-ds]
  (calc-roundtrip-stats roundtrip-ds [:side]))

(defn win-loss-stats [roundtrips-ds]
  (calc-roundtrip-stats roundtrips-ds [:win?]))

(defn get-group-of [ds group-col group-val]
  (let [ds-filtered  (tc/select-rows ds  (fn [ds]
                                           (= group-val (group-col ds))))
        vec (into [] (tds/mapseq-reader ds-filtered))
        row (first vec)]
    row))

(defn win-loss-performance-metrics [win-loss-stats]
  (let [win (get-group-of win-loss-stats :win? true)
        loss (get-group-of win-loss-stats :win? false)
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

(defn calc-roundtrip-metrics [roundtrips-ds]
  (assert (:ret-log roundtrips-ds) "to calc metrics :ret-log column needs to be present!")
  (assert (:bars  roundtrips-ds) "to calc metrics :bars column needs to be present!")
  (assert (:win?  roundtrips-ds) "to calc metrics :win? column needs to be present!")
  (println "calc-roundtrip-metrics ..")
  (let [wl-stats (win-loss-stats roundtrips-ds)
        metrics (win-loss-performance-metrics wl-stats)]
    metrics))
