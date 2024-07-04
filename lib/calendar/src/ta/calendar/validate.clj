(ns ta.calendar.validate
  (:require
   [ta.calendar.calendars :refer [calendar-exists?]]
   [ta.calendar.interval :refer [interval-exists?]]))

(defn calendar-valid? [calendar]
  (let [[calendar-kw interval-kw] calendar]
    (cond
      (not (vector? calendar))
      false

      (< (count calendar) 2) ; need at least two arguments in the vector
      false

      (not (keyword? calendar-kw)) ; calendar-kw needs to be a keyword
      false

      (not (keyword? interval-kw)) ; interval-kw needs to be a keyword
      false

      (not (calendar-exists? calendar-kw))
      false

      (not (interval-exists? interval-kw))
      false

      :else
      true)))

(defn validate-calendar [[calendar-kw interval-kw]]
  (assert (calendar-exists? calendar-kw) (str "calendar-market unknown: " calendar-kw))
  (assert (interval-exists? interval-kw) (str "calendar-interval unknown : " interval-kw))
  true)

(defn exchange [calendar]
  (first calendar))

(defn interval [calendar]
  (second calendar))

(comment
  (validate-calendar [:us :h])
  (validate-calendar [:us :m])
  (validate-calendar [:us :d])

  (validate-calendar [:us :m15])
  (validate-calendar [:us :m30])
  ; unknown calendar
  (validate-calendar [:us :m37])
  (validate-calendar [:superlunar-exchange :h])

  (calendar-valid? [:us :h])
  (calendar-valid? [:us :m])
  (calendar-valid? [:us :m :adsf])
  (calendar-valid? [:us :m15 :adsf])
  (calendar-valid? [:us :m155 :adsf])

  ; not valid:
  (calendar-valid? [:us :m3 :adsf])

  (exchange [:us :h])
  (interval [:us :h])

;  
  )


