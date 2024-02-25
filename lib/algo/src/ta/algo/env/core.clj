(ns ta.algo.env.core
  (:require
   [ta.calendar.core :refer [trailing-window get-bar-window]]
   [ta.db.bars.protocol :as bardb]
   [ta.db.bars.aligned :as aligned]
   [ta.algo.env :as algo-env]
   [ta.algo.spec :as s]))

(defn get-bars
  "returns bars for asset/calendar/window"
  [env {:keys [asset calendar] :as opts} window]
  (let [bar-db (algo-env/get-engine env)]
    (assert bar-db "environment does not provide bar-db!")
    (assert asset "cannot get-bars for unknown asset!")
    (assert calendar "cannot get-bars for unknown calendar!")
    (assert window "cannot get-bars for unknown window!")
    (bardb/get-bars bar-db opts window)))

(defn get-bars-aligned-filled
  "returns bars for asset/calendar/window"
  [env {:keys [asset calendar] :as opts} calendar-seq]
  (let [bar-db (algo-env/get-engine env)]
    (assert bar-db "environment does not provide bar-db!")
    (assert asset "cannot get-bars for unknown asset!")
    (assert calendar "cannot get-bars for unknown calendar!")
    (assert calendar-seq "cannot get-bars-aligned for unknown window!")
    (aligned/get-bars-aligned-filled bar-db opts calendar-seq)))


(defn add-bars
  "returns bars for asset/calendar/window"
  [env {:keys [calendar] :as opts} ds-bars]
  (let [bar-db (algo-env/get-engine env)]
    (assert bar-db "environment does not provide bar-db!")
    (assert calendar "can not execute add-bars - needs calendar parameter.")
    (assert ds-bars "can not execute add-bars - needs ds-bars parameter.")
    (bardb/append-bars env opts ds-bars)))


(defn get-calendar-time [env calendar]
  (let [calendar-time (:calendar-time env)]
    (assert calendar-time "environment does not provide calendar-time!")
    (get @calendar-time calendar)))

(defn get-trailing-bars [env spec bar-close-date]
  (let [calendar (s/get-calendar spec)
        asset (s/get-asset spec)
        n (s/get-trailing-n spec)
        window (trailing-window calendar n bar-close-date)]
    (get-bars env {:asset asset
                   :calendar calendar} window)))

(defn get-bars-lower-timeframe [env spec lower-timeframe]
  (let [calendar (s/get-calendar spec)
        market (first calendar)
        calendar-lower [market lower-timeframe]
        asset (s/get-asset spec)
        time (get-calendar-time env calendar)
        window (get-bar-window calendar time)]
    (get-bars env {:asset asset
                   :calendar calendar-lower} window)))


