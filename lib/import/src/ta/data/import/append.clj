(ns ta.data.import.append
  (:require
   [taoensso.timbre :refer [info warn error]]
   [tick.core :as t]
   [tablecloth.api :as tc]
   [ta.warehouse :refer [load-series exists-series?]]
   [ta.data.import.warehouse :refer [save-series]]
   ))

; append symbol - add missing bars at the end.

(defn remove-first-row-if-date-equals
  [ds-bars dt]
  (if (> (tc/row-count ds-bars) 0)
    (let [date-first-row (get-in (tc/first ds-bars) [:date 0])]
      ;(info "first-row-date: " date-first-row)
      (if (t/= date-first-row dt)
        (tc/drop-rows ds-bars [0])
         ds-bars))
    ds-bars))


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
  
   (remove-first-row-if-date-equals
     (tc/dataset [])
     (t/instant "2001-12-31T00:00:00Z"))

;
  )
 

(defn append-series [get-bars series-opts]
  (if (exists-series? series-opts)
    (let [ds-old (load-series series-opts)
          last-date (get-in (tc/last ds-old) [:date 0])]
      (if last-date
        (let [_ (info "get-bars symbol: " (:symbol series-opts) "since: " last-date)
              range {:start last-date :mode :append}
              ds-new (get-bars series-opts range)
              ds-new (if ds-new 
                       (remove-first-row-if-date-equals ds-new last-date)
                       nil)
              count-new (if ds-new 
                          (tc/row-count ds-new)
                          0)]
          (if (> count-new 0)
            (let [ds-combined (tc/concat ds-old ds-new)]
              (info "adding " count-new "bars to " (:symbol series-opts) " " (:frequency series-opts) "since:" last-date " total: " (tc/row-count ds-combined))
              (save-series series-opts ds-combined))
            (warn "no new bars for " (:symbol series-opts) " " (:frequency series-opts) "since" last-date)))
        (error "no existing series for " (:symbol series-opts) " " (:frequency series-opts) "SKIPPING APPEND.")))
    (error "no series for " (:symbol series-opts) " " (:frequency series-opts) " .. skipping append")
    ))

(comment 
  
  (require '[ta.data.api-ds.alphavantage :as av])
  (append-series av/get-bars {:symbol "FMCDX"  :frequency "D"} {})
    
  (load-series {:symbol "FMCDX"  :frequency "D"})

  (get-in (tc/last (load-series {:symbol "FMCDX"  :frequency "D"})) [:date 0])
   (av/get-bars {:symbol "FMCDX"  :frequency "D"} 
                  {:start 4 :mode :append}
                    {})

 ; 
  )


