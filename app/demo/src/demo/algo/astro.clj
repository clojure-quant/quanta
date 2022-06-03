(ns demo.algo.astro
  (:require
   [ta.algo.manager :refer [add-algo]]
   [astro.marks :refer [astro-marks]]))

(add-algo
 {:name "astro"
  :comment "astrological aspects - viz only"
  :algo (fn [ds-bars _options] ds-bars)
  :charts [nil ; nothing to add in price pane
           {:volume "column"}]
  :options {:show-moon false}
  :marks astro-marks})
