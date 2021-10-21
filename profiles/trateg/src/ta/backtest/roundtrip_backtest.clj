(ns ta.backtest.roundtrip-backtest
  (:require
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as fun]
   [tablecloth.api :as tc]
   [ta.warehouse :as wh]
   [ta.helper.ago :refer [xf-future]]
   [ta.backtest.signal :refer [trade-signal]]
   [ta.backtest.position-pl :refer [position-pl]]))

(defn- bar->roundtrip-partial [ds]
  (let [close (:close ds)
        close-f1  (into [] xf-future close)
        date-open (:date ds)
        index-open (:index ds)
        date-close (into [] xf-future date-open)
        index-close (fun/+ index-open 1)
        pl-log (position-pl close (:position ds))]
    (->  ds
         (tc/rename-columns {:index :index-open
                             :close :price-open
                             :date :date-open})
         (tc/add-columns  {:price-close close-f1
                           :date-close date-close
                           :index-close index-close
                           :pl-log pl-log
                           ;:pl-prct pl-prct
                           }))))

(defn win [chg-p]
  (> chg-p 0))

(defn calc-roundtrips [ds-study]
  (as-> ds-study ds
      ; (tc/select-rows (fn [{:keys [trade] :as row}]
      ;                           (not (nil? trade))))
    (bar->roundtrip-partial ds)
    (tc/group-by ds :trade-no)
    (tc/aggregate ds {:position (fn [ds] (->> ds :position first))
                      :bars (fn [ds] (->> ds :index-open count))
                      ; open
                      :index-open (fn [ds] (->> ds :index-open first))
                      :date-open (fn [ds] (->> ds :date-open first))
                      :price-open (fn [ds] (->> ds :price-open first))
                      ; close
                      :index-close (fn [ds] (->> ds :index-close last))
                      :date-close (fn [ds] (->> ds :date-close last))
                      :price-close (fn [ds] (->> ds :price-close last))
                      ; trade
                      :trade (fn [ds] (->> ds :trade first))
                      :trades (fn [ds]
                                (->> ds
                                     :trade
                                     (remove nil?)
                                     count))
                      ; pl
                      :pl-log (fn [ds]
                                (->> ds
                                     :pl-log
                                     (apply +)))})

    (tc/rename-columns ds {:$group-name :rt-no})
    (tc/add-column ds :win
                   (dtype/emap win :bool (:pl-log ds)))))

(defn backtest-ds
  "algo has to create :position column
   creates roundtrips based on this column"
  [ds-bars algo algo-options]
  (let [ds-study (-> ds-bars
                     (algo algo-options)
                     trade-signal)
        ds-roundtrips (when (:signal ds-study)
                        (calc-roundtrips ds-study))]
    {:ds-study ds-study
     :ds-roundtrips ds-roundtrips}))

(defn run-backtest
  "algo has to create :position column
   creates roundtrips based on this column"
  [algo {:keys [w symbol frequency] :as options}]
  (let [algo-options (dissoc options :w :symbol :frequency)
        ds-bars  (wh/load-symbol w frequency symbol)]
    (backtest-ds ds-bars algo algo-options)))

(defn run-backtest-parameter-range
  [algo base-options
   prop-to-change prop-range]
  (for [m prop-range]
    (let [options (assoc base-options prop-to-change m)
          r (run-backtest algo options)
          r (-> r
                (assoc :ds-roundtrips (tc/set-dataset-name (:ds-roundtrips r) m)))]
      r)))

