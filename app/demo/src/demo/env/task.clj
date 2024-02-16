(ns demo.env.task
  (:require
   [taoensso.timbre :refer [trace debug info warnf error]]
   [reval.task :refer [nbeval]]
   [ta.helper.print :refer [print-all]]
   [ta.db.asset.symbollist :refer [load-list]]
   [ta.warehouse.overview :refer [warehouse-overview]]
   ;[ta.gann.gann :refer [gann-symbols]]
   [ta.gann.chartmaker :refer [make-boxes-all-individual]]
   [ta.data.import :refer [import-series import-list]]
   [demo.data-import.create-random :as rr]
   [demo.goldly.reval] ; side-effects
   ))

;; tasks (for cli use)

(defn run  [{:keys [task symbols provider]}]
  (case task

    :import-series
    (let [provider (or provider :alphavantage)
          symbols (or symbols "MSFT")]
      (info "import symbol: " symbols)
      (import-series provider symbols "D" :full))

    :import
    (let [symbols (or symbols "fidelity-select")]
      (info "import list: " symbols)
      (import-list symbols "D" :full))

    :import-15
    (let [symbols (or symbols "crypto")
          _ (info "import 15 min bars for list: " symbols)]
      (import-list symbols "15" :full))

    :append
    (let [symbols (or symbols "crypto")
          _ (info "bybit-append list: " symbols)]
      (import-list symbols "D" :full))

    :warehouse
    (do (info "warehouse summary:")
        (-> (warehouse-overview :stocks "D") print-all info)
        (->  (warehouse-overview :crypto "D") print-all info)
        (-> (warehouse-overview :crypto "15") print-all info))

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
