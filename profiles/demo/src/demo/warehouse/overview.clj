(ns demo.warehouse.overview
  (:require
   [ta.warehouse.overview :refer [warehouse-overview]]))

(warehouse-overview :stocks "D")

(warehouse-overview :crypto "D")
(warehouse-overview :crypto "15")

(warehouse-overview :shuffled "D")
(warehouse-overview :shuffled "15")

(warehouse-overview :random "D")
(warehouse-overview :random "15")




