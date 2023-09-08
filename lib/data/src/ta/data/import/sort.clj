(ns ta.data.import.sort
  (:require
   [taoensso.timbre :refer [warn]]
   [tablecloth.api :as tc]
   [tick.core :as t]
   [ta.helper.date :refer [parse-date]]))

(defn acc-sort [{:keys [begin sorted dt-last] :as acc} dt]
  (if sorted
    {:sorted (if begin 
               true
               (t/> dt dt-last))
     :dt-last dt
     :first false}
    acc))

(defn is-col-sorted? [col-date]
  (let [r (reduce acc-sort {:begin true 
                            :sorted true}
                  col-date)]
     (:sorted r)))

(defn is-ds-sorted? [ds]
  (is-col-sorted? (:date ds)))

(defn sort-ds [ds]
   (tc/order-by ds [:date] [:asc]))

(defn ensure-sorted [ds]
  (let [sorted? (is-ds-sorted? ds)]
    (if sorted?
      ds
      (do 
        (warn "imported bars-series is not sorted. sorting now.")
        (sort-ds ds)))))


(comment 

   (def ds1
    (tc/dataset [{:date (parse-date "2023-01-01") :b 2}
                 {:date (parse-date "2023-01-03") :b 5}]))
  
   (def ds2
     (tc/dataset [{:date (parse-date "2023-01-03") :b 2}
                  {:date (parse-date "2023-01-01") :b 5}]))
  
  (is-ds-sorted? ds1)
  (is-ds-sorted? ds2)
  (sort-ds ds1)
  (sort-ds ds2)
  (ensure-sorted ds1)
  (ensure-sorted ds2)

  (-> ds1 tc/last :date)
  (get-in (tc/last ds1) [:date 0])
 
;
  )



