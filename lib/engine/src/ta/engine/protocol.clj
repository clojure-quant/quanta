(ns ta.engine.protocol)

(defprotocol eng
  ; cell
  (calendar-cell [this time-fn calendar])
  (formula-cell [this formula-fn cell-seq])
  (value-cell [this v])
  ; calendar
  (create-calendar [this calendar])
  (get-calendar [this calendar])
  (set-calendar! [this {:keys [calendar time]}])
  (active-calendars [this]))


