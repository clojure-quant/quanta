(ns ta.import.helper.daily
  (:require
    [tick.core :as t]
    [tablecloth.api :as tc]
   [ta.calendar.calendars :refer [get-calendar]]))

(defn make-time-setter [time tz]
  (fn [dt]
    (t/in (t/at dt time) tz)))

(defn date-col-to-exchange-close [bar-ds calendar-kw]
  (let [{:keys [close timezone]} (get-calendar calendar-kw)
        set-exchange-time (make-time-setter close timezone)]
   (tc/convert-types bar-ds :date [[:zoned-date-time set-exchange-time]])    
    ))

(comment 
  (def ds (tc/dataset [{:date (t/date "2023-01-01")
                :close 100.0}
               {:date (t/date "2025-01-01")
                :close 105.0}
               ]))
  
  (-> ds 
      (date-col-to-exchange-close :us))

  
  ;
  )

 
