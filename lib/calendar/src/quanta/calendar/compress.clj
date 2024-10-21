(ns quanta.calendar.compress
  (:require
   [tick.core :as t]
   [tech.v3.datatype :as dtype]
   [tablecloth.api :as tc]
   [quanta.calendar.core :refer [current-close]]))

(defn add-date-group-calendar [ds calendar]
  (let [group-fn (partial current-close calendar)
        date-group-col (dtype/emap group-fn :zoned-date-time (:date ds))]
    (tc/add-column ds :date-group date-group-col)))

;; COMPRESS

(defn compress-ds [grouped-ds]
  (->
   grouped-ds
   (tc/group-by [:date-group])
   (tc/aggregate {:open (fn [ds]
                          (-> ds :open first))
                  :high (fn [ds]
                          (->> ds
                               :high
                               (apply max)))
                  :low (fn [ds]
                         (->> ds
                              :low
                              (apply min)))
                  :close (fn [ds]
                           (-> ds :close last))
                  :volume (fn [ds]
                            (->> ds
                                 :volume
                                 (apply +)))
                  :count (fn [ds]
                           (->> ds
                                :close
                                (count)))})
   (tc/rename-columns {:date-group :date})))

(defn compress-to-calendar [ds calendar]
  (->  ds
       (add-date-group-calendar calendar)
       compress-ds
       (tc/map-columns :date t/instant)))