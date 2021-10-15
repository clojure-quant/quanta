(ns demo.warehouse.overview
  (:require
   [ta.warehouse.overview :refer [warehouse-overview]]
   [demo.env.config :as c]))

(warehouse-overview c/w-stocks "D")

(warehouse-overview c/w-crypto "D")
(warehouse-overview c/w-crypto "15")

(warehouse-overview c/w-random "D")
(warehouse-overview c/w-random "15")




