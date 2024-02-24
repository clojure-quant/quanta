(ns ta.engine.javelin
  (:require
   [modular.system]
   [ta.engine.protocol :refer [env]]
   [ta.engine.javelin.algo :as j-algo]
   [ta.engine.javelin.calendar :as j-cal]))

(defrecord env-javelin [bar-db calendars]
  env
  ; bar-db
  (get-bar-db [this]
    (:bar-db this))
  ; algo
  (add-algo [this spec]
    (j-algo/add-algo this spec))
  (add-algos [this spec-seq]
    (j-algo/add-algos this spec-seq))
  ; calendar
  (create-calendar [this calendar]
    (j-cal/create-calendar this calendar))
  (get-calendar [this calendar]
    (j-cal/get-calendar this calendar))
  (set-calendar! [this data]
    (j-cal/set-calendar! this data))
  (active-calendars [this]
    (j-cal/active-calendars this)))

(defn create-env [bar-db-kw]
  (let [bar-db (bar-db-kw modular.system/system)
        calendars (atom {})]
    (env-javelin. bar-db calendars)))