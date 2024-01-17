(ns ta.calendar.scheduler
  (:require
   [taoensso.timbre :as timbre :refer [info warn error]]
   [chime.core :as chime]
   [ta.calendar.core :refer [calendar-seq]]))

(defn- log-finished []
  (warn "scheduler chime Schedule finished!"))

(defn- log-error [ex]
  (error "scheduler chime exception: " ex)
  true)


(defn start [calendar-kw interval-kw f]
  (info "calendar start: " calendar-kw interval-kw)
  (let [date-seq (calendar-seq calendar-kw interval-kw)]
    (chime/chime-at 
      date-seq f
      {:on-finished log-finished 
       :error-handler log-error})))

(comment 
   (defn print [title] 
     (fn [dt]
       (info "calendar event " title  " @: " dt)))
 
    (start :us :m (print "*minute*"))
    (start :us :h (print "*hour*"))
    (start :us :day (print "*day*"))

 ; 
  )