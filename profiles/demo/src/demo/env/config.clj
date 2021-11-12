(ns demo.env.config
  (:require
   [clojure.pprint]
   [clojure.edn :as edn]
   [taoensso.timbre :refer [info warn]]
   [ta.warehouse :as wh]
   [ta.data.alphavantage :as av]))

(let [secret (-> "creds.edn" slurp edn/read-string :alphavantage)]
  ; secrets should not be saved in a notebook
  (warn "alphavantage secret: " secret)
  (av/set-key! secret))

(info "initializing warehouse..")

(wh/init {:list "../resources/etf/"
          :series  {:crypto "../db/crypto/"
                    :stocks "../db/stocks/"
                    :random "../db/random/"
                    :shuffled  "../db/shuffled/"}})


