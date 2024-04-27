(ns ta.warehouse.tml
  (:require
   [taoensso.timbre :refer [debug info warnf error]]
   [tick.core :as t]
   [tablecloth.api :as tc]
   [ta.warehouse :refer [load-symbol]]))

(defn range-filter [dt-begin dt-end]
  (fn [dt]
    (and (t/>= dt dt-begin)
         (t/<= dt dt-end))))

(comment
  (let [rf (range-filter (t/date-time "2021-01-01T00:00:00")
                         (t/date-time "2021-12-31T00:00:00"))]
    [(rf (t/date-time "2021-06-01T00:00:00")) ; inside
     (rf (t/date-time "2020-06-01T00:00:00")) ; prior
     (rf (t/date-time "2022-06-01T00:00:00")) ; past
     ])
 ; 
  )
(defn filter-date-range
  [df-study dt-start dt-end]
  (let [rf (range-filter dt-start dt-end)]
    (tc/select-rows df-study (fn [{:keys [date]}]
                               (rf date)))))

(comment
  (-> (load-symbol :crypto "D" "ETHUSD")
      (filter-date-range
       (t/date-time "2021-04-01T00:00:00")
       (t/date-time "2021-05-01T00:00:00")))

;
  )
