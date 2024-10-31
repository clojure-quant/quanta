(ns demo.compress
 (:require 
  [tick.core :as t]
  [tablecloth.api :as tc]
  [tech.v3.datatype :as dtype]
  [quanta.calendar.core :refer [calendar-seq current-close next-close]]))

(def dt (t/instant))

dt
;; => #time/instant "2024-10-31T02:08:06.260421480Z"


(take 15 (calendar-seq [:crypto :m] dt))
;; => (#time/zoned-date-time "2024-10-31T02:08Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:09Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:10Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:11Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:12Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:13Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:14Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:15Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:16Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:17Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:18Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:19Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:20Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:21Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:22Z[UTC]")

(def ds (tc/dataset {:date (take 15 (calendar-seq [:crypto :m] dt))}))

ds
;; => _unnamed [15 1]:
;;    
;;    |                  :date |
;;    |------------------------|
;;    | 2024-10-31T02:08Z[UTC] |
;;    | 2024-10-31T02:09Z[UTC] |
;;    | 2024-10-31T02:10Z[UTC] |
;;    | 2024-10-31T02:11Z[UTC] |
;;    | 2024-10-31T02:12Z[UTC] |
;;    | 2024-10-31T02:13Z[UTC] |
;;    | 2024-10-31T02:14Z[UTC] |
;;    | 2024-10-31T02:15Z[UTC] |
;;    | 2024-10-31T02:16Z[UTC] |
;;    | 2024-10-31T02:17Z[UTC] |
;;    | 2024-10-31T02:18Z[UTC] |
;;    | 2024-10-31T02:19Z[UTC] |
;;    | 2024-10-31T02:20Z[UTC] |
;;    | 2024-10-31T02:21Z[UTC] |
;;    | 2024-10-31T02:22Z[UTC] |
 
(map #(current-close [:crypto :m5] %) (:date ds))
;; => (#time/zoned-date-time "2024-10-31T02:05Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:05Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:10Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:10Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:10Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:10Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:10Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:15Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:15Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:15Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:15Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:15Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:20Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:20Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:20Z[UTC]")

(->> (:date ds)
     (map #(current-close [:crypto :m5] %))
     (map #(next-close [:crypto :m5] %))
 )
;; => (#time/zoned-date-time "2024-10-31T02:10Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:10Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:15Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:15Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:15Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:15Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:15Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:20Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:20Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:20Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:20Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:20Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:25Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:25Z[UTC]"
;;     #time/zoned-date-time "2024-10-31T02:25Z[UTC]")


(->> ((:date ds) 0)
      (current-close [:crypto :m5]))

(defn other-cal-link-fast
  "returns a vector of the difference between subsequent values.
   returns NaN if diff cannot be calculated."
  [dt-col calendar]
  (let [next-dt-a (atom (->> (dt-col 0)
                        (current-close calendar)))]
     (map
      (fn [row-dt]
      (let [next-dt @next-dt-a]
         (if (t/<= row-dt next-dt)
           next-dt
           (let [next-dt (next-close calendar next-dt)]
             (reset! next-dt-a next-dt)
             next-dt))))              
        dt-col
        )))

(tc/add-column ds
               :dt-next (other-cal-link-fast (:date ds) [:crypto :m5])
               )
;; => _unnamed [15 2]:
;;    
;;    |                  :date |               :dt-next |
;;    |------------------------|------------------------|
;;    | 2024-10-31T02:08Z[UTC] | 2024-10-31T02:10Z[UTC] |
;;    | 2024-10-31T02:09Z[UTC] | 2024-10-31T02:10Z[UTC] |
;;    | 2024-10-31T02:10Z[UTC] | 2024-10-31T02:10Z[UTC] |
;;    | 2024-10-31T02:11Z[UTC] | 2024-10-31T02:15Z[UTC] |
;;    | 2024-10-31T02:12Z[UTC] | 2024-10-31T02:15Z[UTC] |
;;    | 2024-10-31T02:13Z[UTC] | 2024-10-31T02:15Z[UTC] |
;;    | 2024-10-31T02:14Z[UTC] | 2024-10-31T02:15Z[UTC] |
;;    | 2024-10-31T02:15Z[UTC] | 2024-10-31T02:15Z[UTC] |
;;    | 2024-10-31T02:16Z[UTC] | 2024-10-31T02:20Z[UTC] |
;;    | 2024-10-31T02:17Z[UTC] | 2024-10-31T02:20Z[UTC] |
;;    | 2024-10-31T02:18Z[UTC] | 2024-10-31T02:20Z[UTC] |
;;    | 2024-10-31T02:19Z[UTC] | 2024-10-31T02:20Z[UTC] |
;;    | 2024-10-31T02:20Z[UTC] | 2024-10-31T02:20Z[UTC] |
;;    | 2024-10-31T02:21Z[UTC] | 2024-10-31T02:25Z[UTC] |
;;    | 2024-10-31T02:22Z[UTC] | 2024-10-31T02:25Z[UTC] |

