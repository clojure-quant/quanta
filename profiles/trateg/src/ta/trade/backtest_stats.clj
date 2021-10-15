(ns ta.trade.backtest-stats
  (:require
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as fun]
   [tablecloth.api :as tablecloth]
   [ta.dataset.helper :as helper]
   [ta.trade.drawdown :refer [max-drawdown]]
   [ta.xf.ago :refer [xf-future]]))

(comment
  (- (Math/log10 101) (Math/log10 100)) ; 1% 0.004

  (- (Math/log10 120) (Math/log10 100)) ; 20% 0.08
  (- (Math/log10 1200) (Math/log10 1000)) ; 20% 0.08
  (- (Math/log10 1000) (Math/log10 2000)) ; -0.3
  (- (Math/log10 2000) (Math/log10 1000)) ; +0.3

;   
  )

(defn roundtrip-pl [position chg-p]
  (if (= position :short)
    (- 0 chg-p)
    chg-p))

(defn win [chg-p]
  (> chg-p 0))

(defn- trade->roundtrip [ds]
  (let [close (:close ds)
        close-f1  (into [] xf-future close)
        chg (fun/- close-f1 close)
        chg-p (fun// chg close)
        chg-p (fun/* 100.0 chg-p)  ;  (into [] (map (*-const 100.0) chg-p))
        chg-p (dtype/emap roundtrip-pl :float64 (:position ds) chg-p)
        win (dtype/emap win :bool chg-p)]
    (->  ds
         (tablecloth/rename-columns {:index :index-open
                                     :close :price-open
                                     :date :date-open})
         (tablecloth/add-columns  {:price-close close-f1
                                   :chg chg
                                   :chg-p chg-p
                                   :win win}))))

(defn calc-roundtrips [ds-study]
  (-> ds-study
      ; (tablecloth/select-rows (fn [{:keys [trade] :as row}]
      ;                           (not (nil? trade))))
      (trade->roundtrip)
      (tablecloth/group-by :trade-no)
      (tablecloth/aggregate {:trade (fn [ds] (->> ds :trade first))
                             :position (fn [ds] (->> ds :position first))
                             :win (fn [ds] (->> ds :win first))
                             ; open
                             :index-open (fn [ds] (->> ds :index-open first))
                             :date-open (fn [ds] (->> ds :date-open first))
                             :price-open (fn [ds] (->> ds :price-open first))
                             ;close
                             :index-close (fn [ds] (->> ds :index-open last)) ; plus 1 bar
                             :date-close (fn [ds] (->> ds :date-open last))  ; plus 1 bar
                             :price-close (fn [ds] (->> ds :price-close last))

                             :bars (fn [ds] (->> ds :index-open count))
                             :trades (fn [ds]
                                       (->> ds
                                            :trade
                                            (remove nil?)
                                            count))
                             :points (fn [ds]
                                       (->> ds
                                            :chg
                                            (apply +)))
                             :prct (fn [ds]
                                     (->> ds
                                          :chg-p
                                          (apply +)))})))

(defn- print-roundtrips-view [ds-rt]
  (->  ds-rt
       (tablecloth/select-columns [:$group-name
                                   :index-open :index-close
                                   :bars
                                   :trade
                                   ; :date-open :date-close
                                  ; :price-open :price-close
                                 ; :signal
                                  ; :position
                                   ;:trade
                             ;:chg
                                   :prct
                                   :win])
       (helper/print-all)))

(defn print-roundtrips [backtest-result]
  (-> (:ds-roundtrips backtest-result)
      (print-roundtrips-view)))

(defn print-roundtrips-pl-desc [backtest-result]
  (-> (:ds-roundtrips backtest-result)
      (tablecloth/order-by :prct)
      (print-roundtrips-view)))

(defn mean [coll]
  (/ (reduce + coll) (count coll)))

;;for sample (not population)
(defn standard-deviation [coll]
  (let [avg     (mean coll)
        squares (map #(Math/pow (- % avg) 2) coll)]
    (-> (reduce + squares)
        (/ (dec (count coll)))
        Math/sqrt)))

(defn calc-roundtrip-stats [backtest-result group-by]
  (let [ds-roundtrips (:ds-roundtrips backtest-result)]
    (-> ds-roundtrips
        (tablecloth/group-by group-by)
        (tablecloth/aggregate {:bars (fn [ds]
                                       (->> ds
                                            :bars
                                            (apply +)))
                               :trades (fn [ds]
                                         (->> ds
                                              :trade
                                              (remove nil?)
                                              count))
                               :pl-points-cum (fn [ds]
                                                (->> ds
                                                     :points
                                                     (apply +)))
                               :pl-prct-cum (fn [ds]
                                              (->> ds
                                                   :prct
                                                   (apply +)))
                               :pl-prct-mean (fn [ds]
                                               (->> ds
                                                    :prct
                                                    mean))
                               :pl-prct-max-dd (fn [ds]
                                                 (-> ds
                                            ;:prct
                                                     (tablecloth/->array :prct)
                                                     max-drawdown))})
        (tablecloth/set-dataset-name (tablecloth/dataset-name ds-roundtrips)))))

(defn- calc-roundtrip-stats-print [backtest-result group-by]
  (-> backtest-result
      (calc-roundtrip-stats group-by)
      (helper/print-all)
      println))


(defn print-overview-stats [backtest-result]
  (calc-roundtrip-stats-print backtest-result :position))

(defn print-roundtrip-stats [backtest-result]
  (calc-roundtrip-stats-print backtest-result :position)
  (calc-roundtrip-stats-print backtest-result [:position :win]))

