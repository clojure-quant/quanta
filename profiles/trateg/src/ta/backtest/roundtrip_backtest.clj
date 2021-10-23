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
                           :pl-log pl-log}))))

(defn win [chg-p]
  (if chg-p
    (> chg-p 0)
    false))

(defn aggregate-bars-to-roundtrip [{:keys [entry-cols]
                                    :or {entry-cols []}} ds]
  ;(println "agg with rows: " (tc/row-count ds))
  (let [rt-entry {; open
                  :index-open  (->> ds :index-open first)
                  :date-open  (->> ds :date-open first)
                  :price-open  (->> ds :price-open first)
                  ; close
                  :index-close  (->> ds :index-close last)
                  :date-close  (->> ds :date-close last)
                  :price-close  (->> ds :price-close last)
                  ; trade
                  :position (->> ds :position first)
                  :bars  (->> ds :index-open count)
                  :trade  (->> ds :trade first)
                  :trades
                  (->> ds
                       :trade
                       (remove nil?)
                       count)
                  ;pl
                  :pl-log
                  (->> ds
                       :pl-log
                       (apply +))}]
    (reduce (fn [rt c]
              (assoc rt c (->> ds c first)))
            rt-entry
            entry-cols)))

(defn calc-roundtrips [ds-study options]
  (as-> ds-study ds
      ; (tc/select-rows (fn [{:keys [trade] :as row}]
      ;                           (not (nil? trade))))
    (bar->roundtrip-partial ds)
    (tc/group-by ds :trade-no)
    (tc/aggregate ds (partial aggregate-bars-to-roundtrip options) {:default-column-name-prefix "V2-value"})
    (tc/rename-columns ds {:$group-name :rt-no
                           ; below should not be here. Bug in tc
                           :summary-bars :bars
                           :summary-trades :trades
                           :summary-trade :trade
                           :summary-position :position
                           :summary-pl-log :pl-log
                           :summary-index-open :index-open
                           :summary-date-open :date-open
                           :summary-price-open :price-open
                           :summary-index-close :index-close
                           :summary-date-close :date-close
                           :summary-price-close :price-close
                           ; below is needed by extra fields in other stufies
                           :summary-symbol :symbol
                           :summary-sma-r :sma-r
                           :summary-year :year
                           :summary-month  :month
                           :summary-year-month  :year-month})
    (tc/add-column ds :win
                   (dtype/emap win :bool (:pl-log ds)))))

(comment
  (-> (tc/dataset {:l [:x :x :y :y :y]
                   :a [1 2 3 4 5]
                   :b [5 5 5 5 5]})
      (tc/group-by :l)
      (tc/aggregate (fn [ds]
                      {:sum-of-b (reduce + (ds :b))})
                    {:default-column-name-prefix "xxx"}))

;  
  )

(defn backtest-ds
  "algo has to create :position column
   creates roundtrips based on this column"
  [ds-bars algo options]
  (let [algo-options (dissoc options :w :symbol :frequency :entry-cols)
        ds-study (-> ds-bars
                     (algo algo-options)
                     trade-signal)
        ds-roundtrips (when (:signal ds-study)
                        (calc-roundtrips ds-study options))]
    {:ds-study ds-study
     :ds-roundtrips ds-roundtrips}))

(defn run-backtest
  "algo has to create :position column
   creates roundtrips based on this column"
  [algo {:keys [w symbol frequency] :as options}]
  (let [ds-bars  (wh/load-symbol w frequency symbol)]
    (backtest-ds ds-bars algo options)))

(defn run-backtest-parameter-range
  [algo base-options
   prop-to-change prop-range]
  (for [m prop-range]
    (let [options (assoc base-options prop-to-change m)
          r (run-backtest algo options)
          r (-> r
                (assoc :ds-roundtrips (tc/set-dataset-name (:ds-roundtrips r) m)))]
      r)))

