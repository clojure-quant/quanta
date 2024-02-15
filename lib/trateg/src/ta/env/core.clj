(ns ta.env.core
   "environment functions that are predominantly used by algos."
  )


(defn get-bars 
  "returns bars for asset/calendar/window"
  [env asset calendar window]
  (let [get-series (:get-series env)]
    (assert get-series "environment does not provide get-series!")
    (assert asset "cannot get-bars for unknown asset!")
    (assert calendar "cannot get-bars for unknown calendar!")
    (assert window "cannot get-bars for unknown window!")
    (get-series asset calendar window)))


(defn get-calendar-time [env calendar]
  (let [calendar-time (:calendar-time env)]
    (assert calendar-time "environment does not provide calendar-time!")
    (get @calendar-time calendar)))

  
