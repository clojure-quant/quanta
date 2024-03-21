(ns ta.trade.roundtrip.metrics
  (:require
   [clojure.set]
   [tablecloth.api :as tc]
   [tech.v3.dataset :as tds]
   [tech.v3.datatype.functional :as fun]
   [ta.indicator.drawdown :refer [max-drawdown]]))

(defn calc-roundtrip-stats [roundtrips-ds group-by]
  (-> roundtrips-ds
      (tc/group-by group-by)
      (tc/aggregate {:bars (fn [ds]
                             (fun/sum (:bars ds)))
                     :trades (fn [ds]
                               (tc/row-count ds))
                     ; log
                     :pl-log-cum (fn [ds]
                                   (fun/sum (:ret-log ds)))

                     :pl-log-mean (fn [ds]
                                    (fun/mean (:ret-log ds)))

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
  ;(println (tc/select-columns roundtrips-ds [:win? :ret-log :bars]))
  (println "calc-roundtrip-metrics " roundtrips-ds)
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
  (as-> (map calc-roundtrip-metrics backtest-results) x
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

  (calc-roundtrip-stats ds [:win?])
   ;; => _unnamed [2 6]:
   ;;    
   ;;    | :win? | :bars | :trades | :pl-log-cum | :pl-log-mean | :pl-log-max-dd |
   ;;    |-------|------:|--------:|------------:|-------------:|---------------:|
   ;;    |  true |   2.0 |       2 | -0.42596874 |  -0.21298437 |     0.42596874 |
   ;;    | false |   2.0 |       2 |  0.27300127 |   0.13650064 |     0.00000000 |

  (win-loss-stats ds)
  ;; => _unnamed [2 6]:
  ;;    
  ;;    | :win? | :bars | :trades | :pl-log-cum | :pl-log-mean | :pl-log-max-dd |
  ;;    |-------|------:|--------:|------------:|-------------:|---------------:|
  ;;    |  true |   2.0 |       2 | -0.42596874 |  -0.21298437 |     0.42596874 |
  ;;    | false |   2.0 |       2 |  0.27300127 |   0.13650064 |     0.00000000 |

  (side-stats ds)
  ;; => _unnamed [2 6]:
  ;;    
  ;;    |  :side | :bars | :trades | :pl-log-cum | :pl-log-mean | :pl-log-max-dd |
  ;;    |--------|------:|--------:|------------:|-------------:|---------------:|
  ;;    |  :long |   2.0 |       2 | -0.42596874 |  -0.21298437 |     0.42596874 |
  ;;    | :short |   2.0 |       2 |  0.27300127 |   0.13650064 |     0.00000000 |

  (def side-s (side-stats ds))

  side-s

  (get-group-of side-s :side :long)
  (get-group-of side-s :side :short)
  (get-group-of side-s :side :parrot)

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
