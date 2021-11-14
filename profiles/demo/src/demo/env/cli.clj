(ns demo.env.cli
  (:require
   [modular.log :refer [timbre-config!]]
   [modular.config :refer [load-config!]]
   [taoensso.timbre :refer [trace debug info warnf error]]
   [ta.helper.print :refer [print-all]]
   [ta.warehouse :refer [load-list]]
   [ta.warehouse.overview :refer [warehouse-overview]]
   [demo.env.config] ; side-effects
   [demo.data-import.import-alphavantage :as av]
   [demo.data-import.import-bybit :as bybit]
   [demo.data-import.create-random :as rr])
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

    :alphavantage-import
    (let [symbol (or symbol "fidelity-select")
          _ (info "alphavantage-import list: " symbol)
          symbols (load-list symbol)
          _ (info "alphavantage-import list: " symbol "nr symbols: " (count symbols))]
      (av/get-alphavantage-daily symbols))

    :bybit-import
    (let [symbol (or symbol "crypto")
          _ (info "bybit-import list: " symbol)
          symbols (load-list symbol)
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