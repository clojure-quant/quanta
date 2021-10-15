(ns demo.warehouse.overview
  (:require
   [net.cgrand.xforms :as x]
   [tech.v3.dataset :as tds]
   [tech.v3.datatype.functional :as dfn]
   [tablecloth.api :as tablecloth]
   [ta.warehouse :as wh]
   [ta.warehouse.overview :refer [warehouse-overview]]
   [demo.env.config :as c]))

(warehouse-overview c/w-stocks "D")

(warehouse-overview c/w-crypto "D")
(warehouse-overview c/w-crypto "15")

(warehouse-overview c/w-random "D")
(warehouse-overview c/w-random "15")




