(ns demo.env.task
  (:require
   [taoensso.timbre :refer [trace debug info warnf error]]
   [reval.task :refer [nbeval]]
   ;[ta.gann.gann :refer [gann-symbols]]
   [ta.gann.chartmaker :refer [make-boxes-all-individual]]
   [demo.data-import.create-random :as rr]
   [demo.goldly.reval] ; side-effects
   ))

;; tasks (for cli use)

(defn run  [{:keys [task symbols provider]}]
  (case task

    :shuffle
    (rr/create-crypto-shuffled)

;:gann
;(let [dt-start "2000-01-01"
;      dt-end "2022-04-01"
;      s (gann-symbols)]
;  (info "making gann boxes from " dt-start " to " dt-end " for: " (pr-str s))
;  (make-boxes-all-individual dt-start dt-end))

    :nbeval
    (do
      (info "evaluating notebooks")
      (nbeval))

    :dummy
    (info "dummy task!!")

    (error "task not found: " task)))


(comment
  (run {:task :alphavantage-import
        :symbol "test"})

  (run {:task :alphavantage-import
        :symbol "currency-spot"})

;
  )
