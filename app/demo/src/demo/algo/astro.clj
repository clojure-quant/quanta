(ns demo.algo.astro
  (:require
   [tablecloth.api :as tc]
   [tech.v3.datatype :as dtype]
   [ta.algo.manager :refer [add-algo]]
   [astro.marks :refer [astro-marks]]))

(add-algo
 {:name "astro"
  :comment "astrological aspects - viz only"
  :algo (fn [ds-bars options] ds-bars)
  :charts [nil ; nothing to add in price pane
           {:volume "column"}]
  :options {:show-moon false}
  :marks astro-marks})
