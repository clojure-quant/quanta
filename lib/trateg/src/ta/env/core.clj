(ns ta.env.core
  "environment functions that are predominantly used by algos."
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [tick.core :as t]
   [tablecloth.api :as tc]
   [ta.calendar.core :as cal]
   [ta.calendar.align :as align]
   [ta.db.bars.protocol :as bardb]))

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

(defn- hack-at-time [date]
  ; TODO: remove this hack
  ;(info "hacking date: " date)
  (-> date
      (t/date)
      (t/at (t/time "17:00:00"))
      (t/in "America/New_York")
      (t/instant)))

(defn- hack-time [ds-bars]
  ; TODO: remove this hack
  (tc/add-column ds-bars :date (map hack-at-time (:date ds-bars))))

(defn get-bars-aligned-filled [env opts calendar-seq]
  (let [window (cal/calendar-seq->range calendar-seq)
        ;_ (info "window: " window)
        bars-ds (get-bars env opts window)
        bars-ds (hack-time bars-ds)
        calendar-ds (tc/dataset {:date (reverse (map t/instant calendar-seq))})
        bars-aligned-ds (align/align-to-calendar calendar-ds bars-ds)
        bars-aligned-filled-ds (align/fill-missing-close bars-aligned-ds)
        ]
    ;(info "bars-aligned-ds: " bars-aligned-ds)
    ;(info "calendar-ds count: " (tc/row-count calendar-ds))
    ;(info "bars-aligned-ds count: " (tc/row-count bars-aligned-ds))
    ;(info "bars-aligned-filled-ds count: " (tc/row-count bars-aligned-filled-ds))
    bars-aligned-filled-ds
    ))


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

  
