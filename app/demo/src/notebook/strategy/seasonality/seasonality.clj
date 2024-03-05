(ns notebook.studies.seasonality
  (:require 
   [clojure.string :as str]
   [tick.core :as t]
   [tablecloth.api :as tc]
   [tech.v3.dataset :as tds]
   [tech.v3.datatype.functional :as dfn]
   [modular.persist.protocol :as storage]
   [ta.helper.date :refer [parse-date-only]]
   [ta.helper.ds :refer [ds->map]]
   [ta.data.api-ds.kibot :refer [symbol-list]]
   [ta.data.import :refer [import-series import-list]]
   [ta.warehouse :as wh]
   [ta.calendar.compress :as compress]
   [ta.helper.date-ds :as h]
   [ta.nippy :as nippy]
   [ta.trade.metrics.nav-trades :refer [portfolio]]
   [demo.math :as math]
   [tech.v3.dataset.print :refer [print-range]]
   ))

;;; we will calculate seasonality by month.
;;; to calculate seasonality statistics we need a lot of years, since
;;; each year only gives one value for each month. So the bare minimum
;;; lookback window is 10 years, which gives 10 values.
;;; 


(def ds-etf (symbol-list :etf))
(def start-date (parse-date-only "2009-01-01"))

(defn filter-since [ds-data start-date]
  (tc/select-rows ds-data #(t/>= start-date (:date-start %))))

(def ds-etf-since (filter-since ds-etf start-date))

ds-etf-since

(defn ultra? [row]
  (.contains (or (:desc row) "") "Ultra"))

(defn leveraged? [row]
  (.contains (or (:sector row) "") "Leveraged"))

(defn inverse? [row]
  (.contains (or (:sector row) "") "Inverse"))


(defn filter-sane [ds-data]
  (tc/select-rows ds-data (fn [row] 
                            (and (not (ultra? row))
                                 (not (inverse? row))
                                 (not (leveraged? row))))))

(def ds-etf (filter-sane ds-etf-since))

(-> ds-etf
    (print-range :all)
 )




;;; AGG has :size-mb 72.00 :date-start 2003-09-29 
;;; AFG has :size-mb 4.89  :date-start 2008-07-14
;;; This size-mb difference is huge; better check if
;;; both symbols have sufficient daily bars 

(import-series :kibot {:symbol "AFK" :frequency "D"}  :full) ; 3818 bars
(import-series :kibot {:symbol "AGG" :frequency "D"} :full) ; 5028 bars
;;; 5 years of data = 200*5 = 1000. 
;;; this explains difference between AFK and AGG


(def symbols (:symbol ds-etf))
(first symbols)
;; => "AAXJ"
(last symbols)
;; => "ZSL"


;;; we save timeseries for the seasonality statistics 
;;; into a the dedicated warehouse :seasonality
(import-list :kibot
             symbols
             {:frequency "D"
              :warehouse :seasonal}
             :full)

(def midnight (t/time "00:00:00"))

;; MONTH GROUP

(defn- year-start [year]
  (->
   (t/new-date year 1 1)
   (t/at midnight)))

(defn- year-end [year]
  (->
   (t/new-date year 12 31)
   (t/at midnight)))

(comment 
  (year-start 2001)
  (year-end 2001)  
  )

(defn select-history-year [ds year]
  (let [start (- year 10)
        end (dec year)]
  (h/select-rows-interval ds (year-start start) (year-end end))))

(defn select-current-year [ds year]
  (h/select-rows-interval ds (year-start year) (year-end year)))
   


(defn returns [ds]
  (let [chg (dfn/- (:close ds) (:open ds))
        chg-p (dfn// chg (:open ds))
        chg-p (dfn/* chg-p 100.0)]
    chg-p))

(defn goodness [ds]
  (let [stddev-n (dfn/* (:stddev ds) 0.25)]
    (dfn/- (:mean ds) stddev-n)))


(defn current-year-rets [ds]
  (-> ds
      (tc/group-by [:month])
      (tc/aggregate {:open (fn [ds]
                             (-> ds :open first)) 
                     :close (fn [ds]
                             (-> ds :close last))})))

(defn calc-seasonal-stats [ds]
  ;trailing 10 year window. Recalculated annually.  
  (-> ds
      (tc/group-by [:month])
      (tc/aggregate {:mean (fn [ds]
                             (-> ds returns math/mean))
                     :stddev (fn [ds]
                               (-> ds returns math/standarddev))
                              })))

(def ds-empty (tc/dataset))

(defn seasonal-stats [symbol ds year]
  ;(println "calculating stats for: " symbol " year: " year)
  (let [ds-history (select-history-year ds year) ; 10 prior years
        ds-current (select-current-year ds year) ; current year
        ;_ (println "calc stats..")
        stats (calc-seasonal-stats ds-history)
        ;_ (println "calc current..")
        current (current-year-rets ds-current)
        ;_ (println "joining..")
        ;_ (println "stats: " stats)
        ;_ (println "current: " current)
        ;_ (println "current rows:" (tc/row-count current))
        joined (if (> (tc/row-count current) 0)
                 (-> (tc/left-join stats current :month)
                     (tc/drop-columns [:right.month])
                     (tc/add-columns {:symbol symbol
                                      :year year}))
                  ds-empty)]  
    ;(println "returning joined..")
    joined
    ))
  

 

(defn calc-stats-symbol [symbol] 
  ;(println "calculating stats for: " symbol)
  (let [ds-full (wh/load-series {:symbol symbol
                                 :frequency "D"
                                 :warehouse :seasonal})
        ds-month (compress/compress-ds compress/add-date-group-col-month ds-full)
        ds-month (h/add-year-and-month-date-as-local-date ds-month)
        ]
    ;(println "ds-month")
    ;(println ds-month)
    ;(println "ds-month :all" )
    ;(println (print-range ds-month :all))
    (as-> ds-month 
          ds
          (for [year (range 2010 2024)]
            (seasonal-stats symbol ds year))
          (apply tc/concat ds)
          (tc/set-dataset-name ds symbol)
          (tc/drop-rows ds (fn [row] (nil? (:open row))))
          (tc/drop-rows ds (fn [row] (nil? (:stddev row))))
          (tc/add-columns ds {:goodness (goodness ds)})
         )))

;;; the stats-table for one symbol contains all the data
;;; required to generate trades later on.
(calc-stats-symbol "AAXJ")
(calc-stats-symbol "BSMP")

(defn ds-concat [ds-agg ds-1]
  (if ds-agg
    (tc/concat ds-agg ds-1)
    ds-1))

(defn calc-stats-all 
  ([symbols]
    (reduce ds-concat (map calc-stats-symbol symbols)))
  ([]
   (calc-stats-all symbols)))
  
  

;;; calculate seasonal stats and save to nippy file.
;;; test with just 2 symbols, then 30 symbols, then all
(-> (calc-stats-all ["AAXJ" "ZSL"])
     (nippy/save-ds "../../output/seasonal/small.nippy"))

(-> (calc-stats-all (take 30 symbols))
    (nippy/save-ds "../../output/seasonal/30.nippy"))

(-> (calc-stats-all symbols)
    (nippy/save-ds "../../output/seasonal/all.nippy"))


(calc-stats-all ["DIA" "TLT"])



;Every month. Rebalance to best 10 seasonalities.

;; roundtrips

(def ds-stats (nippy/load-ds "../../output/seasonal/30.nippy"))

(def ds-stats (nippy/load-ds "../../output/seasonal/all.nippy"))

ds-stats
; :month :open :close :symbol :year :goodness 

(defn stats-year-month [ds-stats year month nr-trades]
 (-> ds-stats
     (tc/select-rows (fn [row]
                       (and (= year (:year row))
                            (= month (:month row)))))
     (tc/select-rows (fn [row]
                       (and (> (:goodness row) 0.0)
                            (< (:goodness row) 20.0))
                       
                       ))
     (tc/order-by :goodness :desc)
     (tc/head nr-trades)
     ))


(defn weekend? [dt]
 (let [weekday (t/day-of-week dt)]
   (or (= t/SATURDAY weekday)
       (= t/SUNDAY weekday))))

(defn- date-entry [year month]
  (let [dt (->
            (t/new-date year month 1)
            (t/at midnight))]
    (if (weekend? dt)
        (.plusDays dt 2)
        dt)))

(defn- date-exit [year month]
  (let [dt (->
            (t/new-date year month 1)
            (.plusMonths 1)
            (.plusDays -1)
            (t/at midnight))]
    (if (weekend? dt)
      (.minusDays dt 2)
      dt)
    ))

(date-entry 2023 04)
(date-entry 2023 05)
(date-exit 2023 04)
(date-exit 2023 05)



(defn row->trade [position-size {:keys [year month symbol open close goodness]}]
  (let [qty (int (/ position-size open))
        entry-vol (* open qty)
        exit-vol  (* close qty)]
  {:symbol symbol
   :side :long
   :qty qty
   :entry-date (date-entry year month)
   :exit-date (date-exit year month)
   :entry-price open
   :exit-price close
   :goodness goodness
   :month month
   :entry-vol entry-vol
   :exit-vol exit-vol
   :pl (int (- exit-vol entry-vol))
   }))

(defn stats->trades [ds-stats year month number-trades]
  (let [position-size (/ 100000.0 (+ 0.0 number-trades))]
  (-> (stats-year-month ds-stats year month number-trades)
      (tc/map-rows (partial row->trade position-size))
      (tc/select-columns [:symbol :side :qty 
                          :entry-date :exit-date 
                          :entry-price :exit-price
                          :goodness :month
                          :entry-vol :exit-vol
                          :pl
                          ]))))

(stats->trades ds-stats 2023 01 10)
    

(defn all-stats->trades [ds-stats number-trades]
  (reduce ds-concat
    (for [year (range 2010 2024)
          month (-> 13 range rest)]
       (stats->trades ds-stats year month number-trades))))

(def trades 
   (all-stats->trades ds-stats 25))

trades


(defn print-trades-by [trades group]
  (-> trades
      (tc/group-by [group])
      (tc/aggregate {:pl (fn [ds]
                           (-> ds :pl math/sum))
                     :nr (fn [ds] (-> ds :pl count))
                     :goodness (fn [ds] (-> ds :goodness math/mean))
                     })
      (tc/order-by :pl :desc)
      (print-range :all)))

(print-trades-by trades :month)

(print-trades-by trades :symbol)



(def trades-edn 
   (into []
    (tds/mapseq-reader trades)))
 
(storage/save :edn "../../output/seasonal/trades.edn" trades-edn)

(def trades-edn (storage/loadr :edn "../../output/seasonal/trades.edn"))

(def nav (portfolio trades-edn {:warehouse :seasonal}))

nav

(def nav-browser 
  {:nav (ds->map (:eff nav))
   :warnings (:warnings nav)})



(with-meta (:nav nav-browser)
  {:render-fn 'ta.viz.nav-vega/nav-vega})


(with-meta 
   trades-edn 
   {:render-fn 'ta.viz.trades-table/trades-table-lg})


(-> (wh/load-series {:symbol "SSG"
                     :frequency "D"
                     :warehouse :seasonal})
    (print-range :all))
 
 

 