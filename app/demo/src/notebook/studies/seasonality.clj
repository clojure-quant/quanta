(ns notebook.studies.seasonality
  (:require 
    [tick.core :as t]
    [tablecloth.api :as tc]
    [ta.helper.date :refer [parse-date-only]]
    [ta.data.api-ds.kibot :refer [symbol-list]]
    [ta.data.import :refer [import-series import-list]]
   )
  )

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


;;; AGG has :size-mb 72.00 :date-start 2003-09-29 
;;; AFG has :size-mb 4.89  :date-start 2008-07-14
;;; This size-mb difference is huge; better check if
;;; both symbols have sufficient daily bars 

(import-series :kibot {:symbol "AFK" :frequency "D"}  :full) ; 3818 bars
(import-series :kibot {:symbol "AGG" :frequency "D"} :full) ; 5028 bars
;;; 5 years of data = 200*5 = 1000. 
;;; this explains difference between AFK and AGG


;;; we save timeseries for the seasonality statistics 
;;; into a the dedicated warehouse :seasonality

(import-list :kibot
             (:symbol ds-etf-since)
             {:frequency "D"
              :warehouse :seasonal}
             :full)
