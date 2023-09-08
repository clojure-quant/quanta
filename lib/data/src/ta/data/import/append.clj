(ns ta.data.import.append
  (:require
   [taoensso.timbre :refer [info warn error]]
   [tick.core :as t]
   [tablecloth.api :as tc]
   [ta.data.import.warehouse :refer [save-symbol load-symbol has-symbol]]
   ))

; append symbol - add missing bars at the end.

(defn remove-first-row-if-date-equals
  [ds-bars dt]
  (let [date-first-row (get-in (tc/first ds-bars) [:date 0])]
    ;(info "first-row-date: " date-first-row)
    (if (t/= date-first-row dt)
      (tc/drop-rows ds-bars [0])
       ds-bars)))


(comment 
   (require '[tick.core :as t])

   (remove-first-row-if-date-equals
    (tc/dataset [{:date (t/instant "1999-12-31T00:00:00Z")}
                 {:date (t/instant "2000-12-31T00:00:00Z")}])
    (t/instant "1999-12-31T00:00:00Z"))
  
   (remove-first-row-if-date-equals
   (tc/dataset [{:date (t/instant "1999-12-31T00:00:00Z")}
                {:date (t/instant "2000-12-31T00:00:00Z")}])
   (t/instant "2001-12-31T00:00:00Z"))


  )
 




(defn append-symbol [get-series symbol interval opts]
  (if (has-symbol symbol interval)
    (let [ds-old (load-symbol symbol interval)
          last-date (get-in (tc/last ds-old) [:date 0])]
      (if last-date
        (let [_ (info "get-series symbol: " symbol "since: " last-date)
              range {:start last-date :mode :append}
              ds-new (get-series symbol interval range opts)
              ds-new (if ds-new 
                       (remove-first-row-if-date-equals ds-new last-date)
                       nil)
              count-new (if ds-new 
                          (tc/row-count ds-new)
                          0)]
          (if (> count-new 0)
            (let [ds-combined (tc/concat ds-old ds-new)]
              (info "adding " count-new "bars to " symbol interval "since:" last-date " total: " (tc/row-count ds-combined))
              (save-symbol symbol interval ds-combined))
            (warn "no new bars for " symbol interval "since" last-date)))
        (error "no existing series for " symbol interval "SKIPPING APPEND.")))
    (error "no series for " symbol " " interval " .. skipping append")
    ))

