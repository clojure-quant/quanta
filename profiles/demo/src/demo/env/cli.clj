(ns demo.env.cli
  (:require
   [modular.log :refer [timbre-config!]]
   [taoensso.timbre :refer [trace debug info warnf error]]
   [ta.warehouse :refer [load-list]]
   [demo.env.config] ; side-effects
   [demo.data-import.import-alphavantage :as av]
   [demo.data-import.import-bybit :as bybit]
   [demo.data-import.create-random :as rr])
  (:gen-class))

(defn log-config! []
  (timbre-config!
   {:timbre-loglevel
    [[#{"pinkgorilla.nrepl.client.connection"} :info]
     [#{"org.eclipse.jetty.*"} :info]
     [#{"webly.*"} :info]
     [#{"*"} :info]]}))

;; tasks (for cli use)

(defn run  [{:keys [task list] :as config}]
  (log-config!)

  (case task
    :bybit-initial (do (bybit/init-bybit-daily)
                       (bybit/init-bybit-15))

    :bybit-append (do (bybit/append-bybit-daily)
                      (bybit/append-bybit-15))

    :alphavantage-import
    (if list
      (let [_ (info "alphavantage-import list: " list)
            symbols (load-list list)]
        (av/get-alphavantage-daily symbols))
      (av/get-alphavantage-daily av/tradingview-symbols)
          ;(av/get-alphavantage-daily av/alphavantage-test-symbols)
          ;(av/get-alphavantage-daily av/fidelity-symbols)
      )
    :shuffle  (rr/create-crypto-shuffled)

    (error "task not found: " task)))

(defn -main
  ([]
   (println "printing default list: currency")))

;  (-main "currency")

;  (-main "fidelity-select")

(comment
  (run {:task :alphavantage-import
        :list "test"}))