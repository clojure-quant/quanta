(ns ta.engine.javelin
  (:require
   [modular.system]
   [ta.engine.protocol :refer [eng]]
   [ta.engine.javelin.cell :as j-cell]
   [ta.engine.javelin.calendar :as j-cal]))

(defrecord engine-javelin [calendars]
  eng
  ; cell
  (calendar-cell [this time-fn calendar]
    (j-cell/calendar-cell this time-fn calendar))
  (formula-cell [this formula-fn cell-seq]
    (j-cell/formula-cell this formula-fn cell-seq))
  (value-cell [this v]
    (j-cell/value-cell this v))
  ; calendar
  (create-calendar [this calendar]
    (j-cal/create-calendar this calendar))
  (get-calendar [this calendar]
    (j-cal/get-calendar this calendar))
  (set-calendar! [this data]
    (j-cal/set-calendar! this data))
  (active-calendars [this]
    (j-cal/active-calendars this)))

(defn create-engine-javelin []
  (let [calendars (atom {})]
    (engine-javelin. calendars)))
