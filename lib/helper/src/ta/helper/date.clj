(ns ta.helper.date
  (:require
   [clojure.edn]
   [tick.core :as t]
   ;[tick.timezone]
   ;[tick.locale-en-us]
   [cljc.java-time.instant :as ti]
   [cljc.java-time.local-date :as ld]
   [cljc.java-time.local-date-time :as ldt]
   [cljc.java-time.zoned-date-time :as zdt]
   [cljc.java-time.zone-offset :refer [utc]]
   [cljc.java-time.format.date-time-formatter :as fmt :refer [of-pattern
                                                              ;iso-date
                                                              ]]))

; #time/date       java.time.LocalDate
; #time/date-time  java.time.LocalDateTime
; #time/instant    java.time.Instant (milliseconds)

; now

(defn now []
  (t/inst))

(defn now-datetime []
  (-> (t/now) t/date-time))

(defn now-date []
  (-> (t/now) t/date))

;; parsing

(def date-fmt (of-pattern "yyyy-MM-dd"))
(def datetime-fmt (of-pattern "yyyy-MM-dd HH:mm:ss"))

;(def row-date-format-
;  (fmtick/formatter "yyyy-MM-dd")) ; 2019-08-09

(defn parse-date [s]
  (try
    (-> s
        (ld/parse date-fmt)
        (t/at  (t/time "00:00:00")))
    (catch Exception _
      nil)))

(defn parse-datetime [s]
  (try
    (ldt/parse s datetime-fmt)
    (catch Exception _
      nil)))

;; epoch conversion

(defn datetime->epoch-second [dt]
  (ldt/to-epoch-second dt utc))

(defn date->epoch-second [dt]
  (-> dt
      (t/at (t/time "13:00:06"))
      datetime->epoch-second))

 ;(tick/at (tick/date "2021-06-20") (tick/time "13:00:06"))
(defn ->epoch-second [dt]
  ;(println "->epoch " dt (type dt))
  (let [t (type dt)]
    (cond
      (= t java.time.LocalDate)  (date->epoch-second dt)
      (= t java.time.LocalDateTime)  (datetime->epoch-second dt)
      (= t java.time.Instant) (ti/get-epoch-second dt)
      :else  99)))



(defn epoch-second->datetime [es]
  (-> es (ldt/of-epoch-second 1 utc)))


(comment 
  ;FEED [QQQ|1D]: Requesting data: [1960-10-20T00:00:00.000Z ... 1961-12-14T00:00:00.000Z, 300 bars]
   {:from -290304000, :to -254016000, :count-back 300, :first-request? false}
  (epoch-second->datetime -290304000)
  (epoch-second->datetime -254016000)
  (epoch-second->datetime 941414400)
  

  
  )

;; ago

(defn add-days [dt-inst days]
  ; (t/+ due (t/new-period 1 :months)) this does not work
  ; https://github.com/juxt/tick/issues/65
  (-> (t/>> dt-inst (t/new-duration days :days))
      t/inst) ; casting to int is required, otherwise it returns an instance.
  )

(defn subtract-days [dt-inst days]
  ; (t/+ due (t/new-period 1 :months)) this does not work
  ; https://github.com/juxt/tick/issues/65
  (-> (t/<< dt-inst (t/new-duration days :days))
      t/inst) ; casting to int is required, otherwise it returns an instance.
  )
(defn days-ago [n]
  (-> (now-datetime)
      (subtract-days n)
      ;(t/date)
      ))

; *****************************************************************************
(comment

  (-> (t/now)
      (t/in "UTC")
      ;(tick/date)
      )
  (t/date-time)

  ; create
  (t/instant "1999-12-31T00:00:00Z")
  (t/date "2021-06-20")
  (t/date-time "2021-06-20T12:00:01")

  ; now
  (now-date)
  (-> (now-datetime)
      class)

; parse
  (parse-date "2021-06-05")
  (parse-datetime "2021-06-05 11:30:01")

  (=   (parse-date "2021-06-05")  (parse-date "2021-06-05"))
  (=   (parse-date "2021-06-05")   (parse-date "2021-06-06"))

  (=   (parse-datetime "2021-06-05 12:30:01")   (parse-datetime "2021-06-05 12:30:01"))
  (=   (parse-datetime "2021-06-05 12:30:01")   (parse-datetime "2021-06-05 12:30:02"))

  ; epoch-second
  (-> (now-date) ->epoch-second)
  (-> (now-datetime) ->epoch-second)
  (-> (now-datetime) ->epoch-second epoch-second->datetime)

  (-> (now-datetime))
  (-> (now-datetime) ->epoch-second (ldt/of-epoch-second 1 utc))

  (days-ago 2)

  ;; experiment

  (require '[clojure.repl])
  (clojure.repl/doc t/date-time)

  (t/+ (t/date "2000-01-01")
       (t/new-period 1 :months))

  (t/+ (t/date-time)
       (t/new-period 1 :months))

  ; java.time.LocalDateTime  (only seconds)
  (-> (t/date "2021-06-20")
      (t/at  (t/time "13:30:06"))
      ;(epoch-ldt)
      ;(->epoch-second)
      )

  ;; comparison
  (ldt/is-after
   (ldt/now)
   (parse-date "2021-05-16"))

  (ldt/is-after
   (ldt/now)
   (parse-date "2022-05-16"))

;; duration
  (tick/new-duration 80 :days)

; start (-> (* bars 15) tick/minutes tick/ago)
  ; (tick/- now (tick/minutes (* 15 (:position %))))
  ; (-> 2 tick/hours tick/ago)
  ;(tc/to-long (-> 2 tick/hours tick/ago))
  ;(-> 2 tick/hours tick/ago)

  (->
   ;(tick/instant "1999-12-31T00:59:59Z")
   (now-datetime)
   (t/in "UTC")
   ;(tick/date)
   ;class
   )

; (ZonedDateTime/of (LocalDate/parse date date-fmt)
  ;   (LocalTime/parse time)
  ;                EST)

  (-> (now-date)
      pr-str
      ;(clojure.edn/read-string {:readers {'time/date tick/date}})
      )
  (clojure.edn/read-string "#inst \"1985-04-12T23:20:50.52Z\"")

  java.time.LocalDate

  (clojure.edn/read-string
   "#object[java.time.LocalDateTime
         \"0x43788de\"
         \"2021-11-03T00:00:00.000000001\"]")

;java.time.LocalDateTime

;
  )
