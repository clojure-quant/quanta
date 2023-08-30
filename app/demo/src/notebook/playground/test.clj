(ns notebook.playground.test
  (:require
    [clojure.pprint :refer [print-table]]
   [ta.algo.manager :as am]
   
   )
  
  )


(defn specs-for [algo-name]
  (->> (am/tradingview-algo-chart-specs)
       (filter #(= (:name % name) algo-name))
       first
       :charts))


(specs-for "bollinger")



