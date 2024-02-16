(ns ta.env.core
  "environment functions that are predominantly used by algos.")


(defn get-bars
  "returns bars for asset/calendar/window"
  [env {:keys [asset calendar] :as opts} window]
  (let [get-bars (:get-bars env)]
    (assert get-bars "environment does not provide get-bars!")
    (assert asset "cannot get-bars for unknown asset!")
    (assert calendar "cannot get-bars for unknown calendar!")
    (assert window "cannot get-bars for unknown window!")
    (get-bars opts window)))


(defn get-calendar-time [env calendar]
  (let [calendar-time (:calendar-time env)]
    (assert calendar-time "environment does not provide calendar-time!")
    (get @calendar-time calendar)))

  
