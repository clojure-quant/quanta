(ns notebook.data.warehouse-overview
  (:require
   [ta.db.bars.overview :refer [warehouse-overview]]))

(warehouse-overview :stocks "D")

(warehouse-overview :crypto "D")
(warehouse-overview :crypto "15")

(warehouse-overview :shuffled "D")
(warehouse-overview :shuffled "15")

(warehouse-overview :random "D")
(warehouse-overview :random "15")

(comment

  (require '[tablecloth.api :as tc])
  (require '[ta.helper.ds :refer [ds->map]])

  (->> (warehouse-overview :stocks "D")
       (tc/columns)
       (map meta)
       (map (juxt :name :datatype)))

  (->> (warehouse-overview :stocks "D")
       ds->map)

;  
  )



