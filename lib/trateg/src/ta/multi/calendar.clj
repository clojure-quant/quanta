(ns ta.multi.calendar
  (:require
   [tick.core :as t]
   [tablecloth.api :as tc]))

(def day (t/new-duration 1 :days))
(def days2 (t/new-duration 2 :days))
(def days3 (t/new-duration 3 :days))

(defn next-date [dt-current]
   (let [weekday (t/day-of-week dt-current)]
     (t/>> dt-current
        (cond ; case does not work
          (= t/FRIDAY weekday) days3
          (= t/SATURDAY weekday) days2
          :else  day))))

(defn counter [start]  
  (let [dt (atom start)]
    #(swap! dt next-date)))


(defn weekend? [dt]
   (let [weekday (t/day-of-week dt)]
     (or (= t/SATURDAY weekday)
         (= t/SUNDAY weekday))))

(defn weekday? [dt]
  (not (weekend? dt)))

(defn daily-calendar [start end]
  (let [start (t/<< start days3)
        end (t/>> end days3)
        tick (counter start)
        dt-seq (repeatedly tick)
        dt-seq (if (weekday? start)
                 (conj dt-seq start)
                 dt-seq)]
    {:interval "D"
     :start start
     :end end
     :calendar (tc/dataset
                 {:date (take-while #(t/< % end) dt-seq)})
     }))
     

(defn next-date-sunday-included [dt-current]
  (let [weekday (t/day-of-week dt-current)]
    (t/>> dt-current
          (cond ; case does not work
            (= t/FRIDAY weekday) days2
            ;(= t/SATURDAY weekday) day
            :else  day))))

(defn counter-sunday-included [start]
  (let [dt (atom start)]
    #(swap! dt next-date-sunday-included)))

(defn daily-calendar-sunday-included [start end]
  (let [start (t/<< start days3)
        end (t/>> end days3)
        tick (counter-sunday-included start)
        dt-seq (repeatedly tick)
        dt-seq (if (weekday? start)
                 (conj dt-seq start)
                 dt-seq)]
    {:interval "D"
     :start start
     :end end
     :calendar (tc/dataset
                {:date (take-while #(t/< % end) dt-seq)})}))

(comment 
  (require '[ta.helper.date :refer [parse-date]])
  (require '[tech.v3.dataset.print :refer [print-range]])

  (weekday? (parse-date "2023-09-10"))

  (daily-calendar (parse-date "2022-01-01")
                  (parse-date "2022-01-07"))
  
  (-> (daily-calendar (parse-date "2023-09-01")
                      (parse-date "2023-10-01"))
      :calendar
      (print-range :all))

  
  
   
    
  
 ; 
  )