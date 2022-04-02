(ns demo.env.cli
  (:require
   [taoensso.timbre :refer [trace debug info warnf error]]
   [modular.log :refer [timbre-config!]]
   [modular.config :refer [load-config!]]
   [reval.task :refer [nbeval]]
   [ta.helper.print :refer [print-all]]
   [ta.warehouse :refer [load-list]]
   [ta.warehouse.overview :refer [warehouse-overview]]
   [ta.gann.gann :refer [gann-symbols]]
   [ta.gann.tradingview :refer [make-boxes-all-individual]]
   [demo.env.config] ; side-effects
   [demo.data-import.import-alphavantage :as av]
   [demo.data-import.import-bybit :as bybit]
   [demo.data-import.create-random :as rr]
   [demo.goldly.reval] ; side-effects
   [demo.data-import.demo-bybit]
   )
  (:gen-class))

;; tasks (for cli use)

(defn run  [{:keys [config task symbol] :as config}]
  (timbre-config!
   {:timbre-loglevel
    [[#{"*"} :info]]})
  (info "loading config: " config)
  (load-config! config)
  (timbre-config!
   {:timbre-loglevel
    [[#{"*"} :info]]})
  (case task

    :bybit-test
    (demo.data-import.demo-bybit/demo-bybit)

    :alphavantage-import
    (let [symbol (or symbol "fidelity-select")
          _ (info "alphavantage-import list: " symbol)
          symbols (load-list symbol)
          _ (info "symbols: " (pr-str symbols))
          _ (info "alphavantage-import list: " symbol "nr symbols: " (count symbols))]
      (av/get-alphavantage-daily symbols))

    :bybit-import
    (let [symbol (or symbol "crypto")
          _ (info "bybit-import list: " symbol)
          symbols (load-list symbol)
           _ (info "symbols: " (pr-str symbols))
          _ (info "bybit-import list: " symbol "nr symbols: " (count symbols))]
      (bybit/init-bybit-daily symbols)
      (bybit/init-bybit-15 symbols))

    :bybit-append
    (let [symbol (or symbol "crypto")
          _ (info "bybit-append list: " symbol)
          symbols (load-list symbol)
          _ (info "bybit-append list: " symbol "nr symbols: " (count symbols))]
      (bybit/append-bybit-daily symbols)
      (bybit/append-bybit-15 symbols))

    :warehouse
    (do (info "warehouse summary:")
        (-> (warehouse-overview :stocks "D") print-all info)
        (->  (warehouse-overview :crypto "D") print-all info)
        (-> (warehouse-overview :crypto "15") print-all info))

    :shuffle
    (rr/create-crypto-shuffled)

    :gann
    (let [dt-start "2000-01-01"
          dt-end "2022-04-01"
          s (gann-symbols)]
      (info "making gann boxes from " dt-start " to " dt-end " for: " (pr-str s))
      (make-boxes-all-individual dt-start dt-end))

    :nbeval
    (do
      (info "evaluating notebooks")
      (nbeval))


    (error "task not found: " task)))

(defn -main
  ([]
   (println "printing default list: currency")))

;  (-main "currency")

;  (-main "fidelity-select")

(comment
  (run {:task :alphavantage-import
        :symbol "test"})

  (run {:task :alphavantage-import
        :symbol "currency-spot"})

;  
  )