(ns ta.trade.roundtrip.metrics
  (:require
   [clojure.set]
   [tablecloth.api :as tc]
   [tech.v3.dataset :as tds]
   [ta.math.stats :refer [mean]]
   [ta.indicator.drawdown :refer [max-drawdown]]))

(defn calc-roundtrip-stats [roundtrips-ds group-by]
  (-> roundtrips-ds
      (tc/group-by group-by)
      (tc/aggregate {:bars (fn [ds]
                             (->> ds
                                  :bars
                                  (apply +)))
                     :trades (fn [ds]
                               (tc/row-count ds))
                     ; log
                     :pl-log-cum (fn [ds]
                                   (->> ds
                                        :ret-log
                                        (apply +)))

                     :pl-log-mean (fn [ds]
                                    (->> ds
                                         :ret-log
                                         mean))

                     :pl-log-max-dd (fn [ds]
                                      (-> ds
                                          (tc/->array :ret-log)
                                          max-drawdown))} {:drop-missing? false})
      (tc/set-dataset-name (tc/dataset-name roundtrips-ds))))

(defn side-stats [roundtrips-ds]
  (as-> roundtrips-ds x
    (calc-roundtrip-stats x [:side])
    (tds/mapseq-reader x)
    (map (juxt :side identity) x)
    (into {} x)))

(defn win-loss-stats [roundtrips-ds]
  (as-> roundtrips-ds x
    (calc-roundtrip-stats x [:win?])
    (tds/mapseq-reader x)
    (map (juxt :win? identity) x)
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

(defn calc-roundtrip-metrics [roundtrips-ds]
  (assert (:ret-log roundtrips-ds) "to calc metrics :ret-log column needs to be present!")
  (assert (:bars  roundtrips-ds) "to calc metrics :bars column needs to be present!")
  (assert (:win?  roundtrips-ds) "to calc metrics :win? column needs to be present!")
  ;(println (tc/select-columns roundtrips-ds [:win? :ret-log :bars]))
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
(comment
  (def ds
    (tc/dataset
     {:side [:long :short :long :short]
      :ret-log [-0.30103000  0.17609126  -0.12493874 0.09691001]
      :win? [true false true false]
      :bars [1 1 1 1]}))

  (win-loss-stats ds)
    ;; => {:win? {:win? true, :bars 2, :trades 2, :pl-log-cum -0.42596874, :pl-log-mean -0.21298437, :pl-log-max-dd 0.42596874},
    ;;     :loss {:win? false, :bars 2, :trades 2, :pl-log-cum 0.27300127, :pl-log-mean 0.136500635, :pl-log-max-dd 0.0}}

  (side-stats ds)
  (calc-roundtrip-metrics ds)
   ;;    
   ;;    entry-price | :exit-price | :ret-abs |    :ret-prct |    :ret-log | :win? | :bars | :cum-ret-log |       :nav |
   ;;   ------------:|------------:|---------:|-------------:|------------:|-------|------:|-------------:|-----------:|
   ;;              1 |           2 |        1 | 100.00000000 |  |  true |     1 |  -0.30103000 | 1.69897000 |
   ;;              2 |           3 |       -1 | -50.00000000 | | false |     1 |  -0.12493874 | 1.87506126 |
   ;;              3 |           4 |        1 |  33.33333333 | |  true |     1 |  -0.24987747 | 1.75012253 |
   ;;              4 |           5 |       -1 | -25.00000000 |   | false |     1 |  -0.15296746 | 1.84703254 |

; 
  )
