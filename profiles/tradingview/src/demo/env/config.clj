(ns demo.env.config
  (:require
   [clojure.pprint]
   [clojure.edn :as edn]
   [taoensso.timbre :refer [info warn]]
   [ta.warehouse :as wh]))


(warn "configuring warehouse..")

(wh/init {:list "../resources/etf/"
          :series  {:crypto "../db/crypto/"
                    :stocks "../db/stocks/"
                    :random "../db/random/"
                    :shuffled  "../db/shuffled/"}})
