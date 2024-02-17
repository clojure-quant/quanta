(ns ta.env.core
  "environment functions that are predominantly used by algos.")


(defn get-bars
  "returns bars for asset/calendar/window"
  [{:keys [get-bars] :as env} {:keys [asset calendar] :as opts} window]
    (assert get-bars "environment does not provide get-bars!")
    (assert asset "cannot get-bars for unknown asset!")
    (assert calendar "cannot get-bars for unknown calendar!")
    (assert window "cannot get-bars for unknown window!")
    (get-bars opts window))

(defn add-bars
  "returns bars for asset/calendar/window"
  [{:keys [add-bars] :as env} {:keys [calendar] :as opts} ds-bars]
    (assert add-bars "environment does not provide add-bars!")
    (assert calendar "can not execute add-bars - needs calendar parameter.")
    (assert ds-bars "can not execute add-bars - needs ds-bars parameter.")
    (add-bars env opts ds-bars))


(defn get-calendar-time [env calendar]
  (let [calendar-time (:calendar-time env)]
    (assert calendar-time "environment does not provide calendar-time!")
    (get @calendar-time calendar)))

  
