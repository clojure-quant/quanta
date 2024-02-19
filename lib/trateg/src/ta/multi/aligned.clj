(ns ta.multi.aligned
  (:require
   [taoensso.timbre :refer [trace debug info warn error]]
   [tick.core :as t]
   [tablecloth.api :as tc]
   [tech.v3.tensor :as dtt]
   [ta.calendar.align :as align]
   [ta.warehouse :as wh]
   ))

(defn filter-range [ds-bars {:keys [start end]}]
  (tc/select-rows
   ds-bars
   (fn [row]
     (let [date (:date row)]
       (and
        (or (not start) (t/>= date start))
        (or (not end) (t/<= date end)))))))



(defn load-symbol-window [opts date-start date-end]
  (let [ds-bars (wh/load-series opts)]
    (-> ds-bars
        (filter-range {:start date-start :end date-end}))))


(defn load-series [symbol warehouse {:keys [interval start end calendar]}]
  (let [load-opts {:symbol symbol
                   :warehouse warehouse
                   :frequency interval}
        has-series? (wh/exists-series? load-opts)]
    (when has-series?
      (let [ds-bars (load-symbol-window load-opts start end)
            ds-bars (tc/drop-columns ds-bars [:symbol])
            ds-bars-aligned (align/align-to-calendar calendar ds-bars)]
        ds-bars-aligned))))


(defn load-aligned-filled [symbol warehouse calendar]
  (info "load-aligned-filled" symbol " " warehouse)
  (when-let [ds-bars (load-series symbol warehouse calendar)]
    (align/fill-missing-close ds-bars)))



(comment
  (require '[ta.helper.date :refer [parse-date]])
  (require '[tech.v3.dataset.print :refer [print-range]])
  (require '[ta.multi.calendar :refer [daily-calendar]])

  (wh/load-series {:symbol "GOOGL" :frequency "D"})
  (meta  (wh/load-series {:symbol "GOOGL" :frequency "D"}))

  (def calendar
    (daily-calendar (parse-date "2023-09-01")
                    (parse-date "2023-10-01")))

  (load-symbol-window {:symbol "GOOGL" :frequency "D"}
                      (parse-date "2023-09-01")
                      (parse-date "2023-10-01"))

  (load-series "GOOGL" calendar)

  (load-series "DAX0" calendar)


  (-> (load-series "GOOGL" calendar)
      :GOOGL.date
      ;meta
      )

 ;:left-outer-join.date

  (def ds (-> (load-series "GOOGL" calendar)
              fill-missing-close))

  (keys (tc/columns ds :as-map))

  (print-range ds :all)

  (set-col! ds :close [1 11] 9.9)
  (set-col! ds :close [10 11] 9.9)
  (print-range ds :all)

  (dtt/select (:close ds) [0 1 2 3 4 5 19])

  (dtt/mset! (dtt/select (col ds) idx) [val])

  (dtt/mget (:close ds) [10])

  (-> (load-series "GOOGL" calendar)
      fill-missing-close
      ;meta
      ;(:close )
     ; (:GOOGL.date)
      (print-range :all))

  (load-aligned-filled "GOOGL" nil calendar)
  (load-aligned-filled "GOOGL" :seasonal calendar)



 ; 
  )
