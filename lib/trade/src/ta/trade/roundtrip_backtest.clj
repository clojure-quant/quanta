(ns ta.trade.roundtrip-backtest
  (:require
   [tech.v3.datatype :as dtype]
   [tech.v3.datatype.functional :as fun]
   [tablecloth.api :as tc]
   [ta.helper.ago :refer [xf-future]]
   [ta.trade.signal :refer [trade-signal]]
   [ta.trade.position-pl :refer [position-pl]]))

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

(defn isNaN [x]
  (== ##NaN x))

(defn get-last-not-nil [ds kw]
  (let [vec (get ds kw)
        vec-no-nil (remove nil? vec)]
    (last vec-no-nil)))

; (get-last-nan {:close [1 2 3 nil]} :close)

(defn aggregate-bars-to-roundtrip [{:keys [entry-cols]
                                    :or {entry-cols []}} ds]
  ;(println "agg with rows: " (tc/row-count ds))
  (let [rt-entry {; open
                  :index-open  (->> ds :index-open first)
                  :date-open  (->> ds :date-open first)
                  :price-open  (->> ds :price-open first)
                  ; close
                  :index-close  (get-last-not-nil ds :index-close)
                  :date-close  (get-last-not-nil ds :date-close)
                  :price-close  (get-last-not-nil ds :price-close)
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
    (tc/aggregate ds (partial aggregate-bars-to-roundtrip options) {:default-column-name-prefix "summary"})
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



(defn signal-ds->roundtrips
  "algo has to create :position column
   creates roundtrips based on this column"
  [signal-ds]
  (let [options {}]
    (assert (:signal signal-ds) "to create roundtrips :signal column needs to be present!")
    (assert (:date  signal-ds) "to create roundtrips :date column needs to be present!")
    (assert (:close  signal-ds) "to create roundtrips :close column needs to be present!")
    (let [trade-ds (trade-signal signal-ds)
          roundtrip-ds (calc-roundtrips trade-ds options)]
      {:trade-ds trade-ds
       :roundtrip-ds roundtrip-ds})))

(comment
  (-> (tc/dataset {:l [:x :x :y :y :y]
                   :a [1 2 3 4 5]
                   :b [5 5 5 5 5]})
      (tc/group-by :l)
      (tc/aggregate (fn [ds]
                      {:sum-of-b (reduce + (ds :b))})
                    {:default-column-name-prefix "xxx"}))

  (require '[tick.core :as t])

  (def signal-ds (tc/dataset {:date [(t/instant "2020-01-01T00:00:00Z")
                                     (t/instant "2020-01-12T00:00:00Z")
                                     (t/instant "2020-01-17T00:00:00Z")
                                     (t/instant "2020-01-20T00:00:00Z")
                                     (t/instant "2020-01-22T00:00:00Z")
                                     (t/instant "2020-01-23T00:00:00Z")
                                     (t/instant "2020-01-24T00:00:00Z")
                                     ]
                              :close [1 2 3 4 5 6 7]
                              :signal [:buy :hold :flat :buy :hold :hold :flat]}))
  
  signal-ds

  (trade-signal signal-ds)


  (require '[ta.trade.print :as p])
  (-> (signal-ds->roundtrips signal-ds)
      (p/print-roundtrips))
  
  
  


;  
  )

