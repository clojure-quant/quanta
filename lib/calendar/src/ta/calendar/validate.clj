(ns ta.calendar.validate
  (:require
   [ta.calendar.calendars :refer [calendar-exists?]]
   [ta.calendar.interval :refer [interval-exists?]]))

(defn validate-calendar [[calendar-kw interval-kw]]
  (assert (calendar-exists? calendar-kw) (str "unknown calendar: " calendar-kw))
  (assert (interval-exists? interval-kw) (str "unknown interval: " interval-kw))
  true)

(defn exchange [calendar]
  (first calendar))

(defn interval [calendar]
  (second calendar))

(comment
  (validate-calendar [:us :h])
  (validate-calendar [:us :m])
  (validate-calendar [:us :d])
  (validate-calendar [:us :m37])

  (validate-calendar [:superlunar-exchange :h])

  (exchange [:us :h])
  (interval [:us :h])

;  
  )


