(ns demo.env.config
  (:require
   [clojure.pprint]
   [clojure.edn :as edn]
   [taoensso.timbre :refer [info warn]]
   [modular.config :refer [get-in-config]]
   [ta.data.alphavantage :as av]))

(info "loading secrets ...")

(let [secret (-> "creds.edn" slurp edn/read-string :alphavantage)]
  ; secrets should not be saved in a notebook
  (warn "alphavantage secret: " secret)
  (av/set-key! secret))


