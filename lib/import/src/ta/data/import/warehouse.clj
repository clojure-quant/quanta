(ns ta.data.import.warehouse
  (:require
   [ta.warehouse :as wh]
   [ta.data.import.sort :as sort]
))

 (defn save-series [series-opts ds]
   (let [ds-sorted (sort/ensure-sorted ds)]
     (wh/save-series series-opts ds-sorted)))
