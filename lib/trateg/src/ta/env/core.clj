(ns ta.env.core
  "environment functions that are predominantly used by algos."
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [ta.db.bars.protocol :as bardb]
   [ta.db.bars.aligned :as aligned]
   ))

(defn set-env-bardb
  "creates environment to load series via duckdb"
  [env bar-db]
  (assoc env :bar-db bar-db))

(defn get-bars
  "returns bars for asset/calendar/window"
  [{:keys [bar-db] :as env} {:keys [asset calendar] :as opts} window]
  (assert bar-db "environment does not provide bar-db!")
  (assert asset "cannot get-bars for unknown asset!")
  (assert calendar "cannot get-bars for unknown calendar!")
  (assert window "cannot get-bars for unknown window!")
  (bardb/get-bars bar-db opts window))

(defn get-bars-aligned-filled 
  "returns bars for asset/calendar/window"
  [{:keys [bar-db] :as env} {:keys [asset calendar] :as opts} calendar-seq]
  (assert bar-db "environment does not provide bar-db!")
  (assert asset "cannot get-bars for unknown asset!")
  (assert calendar "cannot get-bars for unknown calendar!")
  (assert calendar-seq "cannot get-bars-aligned for unknown window!")
  (aligned/get-bars-aligned-filled bar-db opts calendar-seq))


(defn add-bars
  "returns bars for asset/calendar/window"
  [{:keys [bar-db] :as env} {:keys [calendar] :as opts} ds-bars]
  (assert bar-db "environment does not provide bar-db!")
  (assert calendar "can not execute add-bars - needs calendar parameter.")
  (assert ds-bars "can not execute add-bars - needs ds-bars parameter.")
  (bardb/append-bars env opts ds-bars))


(defn get-calendar-time [env calendar]
  (let [calendar-time (:calendar-time env)]
    (assert calendar-time "environment does not provide calendar-time!")
    (get @calendar-time calendar)))

  
