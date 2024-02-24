(ns ta.engine.protocol)

(defprotocol env
  (get-bar-db [this])
  ; algo
  (add-algo [this spec])
  (add-algos [this spec-seq])
  ; calendar
  (create-calendar [this calendar])
  (get-calendar [this calendar])
  (set-calendar! [this {:keys [calendar time]}])
  (active-calendars [this]))

