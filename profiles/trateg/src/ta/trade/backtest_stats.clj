(ns ta.trade.backtest-stats
  (:require
   [taoensso.timbre :refer [trace debug info error]]
   [tech.v3.datatype.functional :as fun]
   [tablecloth.api :as tablecloth]
   [ta.dataset.backtest :as backtest]
   [ta.dataset.helper :as helper]
   [ta.xf.ago :refer [xf-future]]))

(defn- trade->roundtrip [ds]
  (let [close (:close ds)
        close-f1  (into [] xf-future close)
        chg (fun/- close-f1 close)
        chg-p (fun// chg close)
        chg-p (fun/* 100.0 chg-p)  ;  (into [] (map (*-const 100.0) chg-p))
        ]
    (->  ds
         (tablecloth/rename-columns {:index :index-open
                                     :close :price-open
                                     :date :date-open})
         (tablecloth/add-columns  {:price-close close-f1
                                   :chg chg
                                   :chg-p chg-p}))))

(defn calc-roundtrips [ds-study]
  (-> ds-study
      ; (tablecloth/select-rows (fn [{:keys [trade] :as row}]
      ;                           (not (nil? trade))))
      (trade->roundtrip)
      (tablecloth/group-by :trade-no)
      (tablecloth/aggregate {:trade (fn [ds] (->> ds :trade first))
                             :position (fn [ds] (->> ds :position first))
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
                                          (apply +)))})

     ))

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
                                       :prct])
     (helper/print-all)))


(defn print-roundtrips [backtest-result]
  (-> (:ds-roundtrips backtest-result)
      (print-roundtrips-view)))

(defn print-roundtrips-pl-desc [backtest-result]
  (-> (:ds-roundtrips backtest-result)
      (tablecloth/order-by :prct)
      (print-roundtrips-view)))


(defn roundtrip-stats [backtest-result]
  (let [ds-roundtrips (:ds-roundtrips backtest-result)]
  (-> ds-roundtrips
      (tablecloth/group-by :position)
      (tablecloth/aggregate {:bars (fn [ds]
                                     (->> ds
                                          :bars
                                          (apply +)))
                             :trades (fn [ds]
                                       (->> ds
                                            :trade
                                            (remove nil?)
                                            count))
                             :points (fn [ds]
                                       (->> ds
                                            :points
                                            (apply +)))
                             :prct (fn [ds]
                                     (->> ds
                                          :prct
                                          (apply +)))})
      (tablecloth/set-dataset-name (tablecloth/dataset-name ds-roundtrips)))))



