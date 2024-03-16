(ns ta.db.bars.aligned
  (:require
   [tick.core :as t]
   [de.otto.nom.core :as nom]
   [tablecloth.api :as tc]
   [ta.calendar.core :as cal]
   [ta.calendar.align :as align]
   [ta.db.bars.protocol :as b]))

;; TODO: remove this. 
;; it is only used in algo-env.
;; once our bar-dbs are more stable, we dont need a hack.

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

(defn get-bars-aligned-filled [db opts calendar-seq]
  (let [window (cal/calendar-seq->range calendar-seq)
        bars-ds (b/get-bars db opts window)]
    (if (and bars-ds (not (nom/anomaly? bars-ds)))
      (let [bars-ds (hack-time bars-ds)
            calendar-ds (tc/dataset {:date (reverse (map t/instant calendar-seq))})
            bars-aligned-ds (align/align-to-calendar calendar-ds bars-ds)
            bars-aligned-filled-ds (align/fill-missing-close bars-aligned-ds)]
    ;(info "bars-aligned-ds: " bars-aligned-ds)
    ;(info "calendar-ds count: " (tc/row-count calendar-ds))
    ;(info "bars-aligned-ds count: " (tc/row-count bars-aligned-ds))
    ;(info "bars-aligned-filled-ds count: " (tc/row-count bars-aligned-filled-ds))
        bars-aligned-filled-ds)
      bars-ds)))