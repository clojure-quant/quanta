(ns ta.backtest.roundtrip-backtest
  (:require
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as fun]
   [tablecloth.api :as tc]
   [ta.warehouse :as wh]
   [ta.helper.ago :refer [xf-future]]
   [ta.backtest.signal :refer [trade-signal]]))

(comment

  ; we want to operate on log-10. With them *10 = 1
  (->>  (Math/log10 13)
        (Math/pow 10))

  (defn log10 [a]
    (Math/log10 a))

  (->>  [0.01 0.1 1 10 100 100]
        (map log10))
  ; negative logs mean we have lost money
  ; so log-pl negative=loss positive=profit

  (let [lo (log10 5601.5)
        lc (log10 57159.0)
        d (- lc lo)]
    (Math/pow d 10))
     ; 1.09    1=*10
     ;          0.09 = + a little bit

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

(defn- bar->roundtrip-partial [ds]
  (let [close (:close ds)
        close-f1  (into [] xf-future close)
        ; log
        log-close (fun/log10 close)
        log-close-f1 (fun/log10 close-f1)
        d-log-c-f1 (fun/- log-close-f1 log-close)
        pl-log (dtype/emap roundtrip-pl :float64 (:position ds) d-log-c-f1)
        ; prct 
        d-c-f1 (fun/- close-f1 close) ; delta close future-1
        prct-c-f1  (->> (fun// d-c-f1 close)
                        (fun/* 100.0))
        pl-prct (dtype/emap roundtrip-pl :float64 (:position ds) prct-c-f1)
        ;win
        ]
    (->  ds
         (tc/rename-columns {:index :index-open
                             :close :price-open
                             :date :date-open})
         (tc/add-columns  {:price-close close-f1
                           :pl-log pl-log
                           :pl-prct pl-prct}))))

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
                      :index-close (fn [ds] (->> ds :index-open last)) ; plus 1 bar
                      :date-close (fn [ds] (->> ds :date-open last))  ; plus 1 bar
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
                                     (apply +)))
                      :pl-prct (fn [ds]
                                 (->> ds
                                      :pl-prct
                                      (apply +)))})

    (tc/rename-columns ds {:$group-name :rt-no})
    (tc/add-column ds :win
                   (dtype/emap win :bool (:pl-log ds)))))

(defn run-backtest
  "algo has to create :position column
   creates roundtrips based on this column"

  [algo {:keys [w symbol frequency] :as options}]
  (let [algo-options (dissoc options :w :symbol :frequency)
        ds-bars  (wh/load-symbol w frequency symbol)
        ds-study (-> ds-bars
                     (algo algo-options)
                     trade-signal)
        ds-roundtrips (calc-roundtrips ds-study)]
    {:ds-study ds-study
     :ds-roundtrips ds-roundtrips}))

(defn run-backtest-parameter-range
  [algo base-options
   prop-to-change prop-range
   printer]
  (for [m prop-range]
    (let [options (assoc base-options prop-to-change m)
          r (run-backtest algo options)
          r (-> r
                (assoc :ds-roundtrips (tc/set-dataset-name (:ds-roundtrips r) m)))]
      (printer r)
      r)))