(ns ta.data.date
  (:require
   [cljc.java-time.instant :as ti]
   [cljc.java-time.local-date :as ld]
   [cljc.java-time.format.date-time-formatter :as fmt :refer [of-pattern iso-date]]
   [cljc.java-time.local-date-time :as ldt]
   [cljc.java-time.zone-offset :refer [utc]]
   [tick.alpha.api :as t]
   ;[tick.timezone]
   ;[tick.locale-en-us]
   ))

; #time/date       java.time.LocalDate
; #time/date-time  java.time.LocalDate
; #time/instant    java.time.Instant

(defn dt-now []
  (t/date (t/now)))

;; parsing
(def date-fmt (of-pattern "yyyy-MM-dd"))

(defn parse-date [date]
  (try
    (ld/parse date date-fmt)
    (catch Exception _
      nil)))

;; epoch conversion

(defn- epoch-ldt [dt]
  (ldt/to-epoch-second dt utc))

(defn- epoch-ld [dt]
  (epoch-ldt (t/at dt (t/time "13:00:06")) ))

 ;(t/at (t/date "2021-06-20") (t/time "13:00:06"))
(defn ->epoch [dt]
  ;(println "->epoch " dt (type dt))
  (let [t (type dt)]
  (cond
     (= t java.time.LocalDate)  (epoch-ld dt)
     (= t java.time.LocalDateTime)  (epoch-ldt dt)  
     (= t java.time.Instant) (ti/get-epoch-second dt)
     :else  99 
        )
  ))

(comment

  ; ; java.time.LocalDate
  (-> (t/date "2021-06-20")
      ;(epoch-ld)
      (->epoch)
      )

   ; java.time.LocalDateTime  (only seconds)
  (-> (t/date "2021-06-20")
      (t/at  (t/time "13:30:06"))
      ;(epoch-ldt)
      (->epoch)
   )

  ; java.time.Instant  (milliseconds)
  (-> (t/now)
      ;(ti/get-epoch-second)
      ;(ti/to-epoch-milli)
      (->epoch)
   )
   (-> (ti/now) ti/get-epoch-second) ; 1624140369
   (ti/of-epoch-second 1624140369)
   (-> (ti/now)) ; 1624141864204 
   (ti/of-epoch-milli 1624141864204)
  
  ;; comparison
   (ld/is-after (ld/now)  (parse-date "2021-05-16"))

  ;; duration
   (t/new-duration 80 :days)
;  
)
