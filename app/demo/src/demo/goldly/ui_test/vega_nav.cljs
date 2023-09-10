(ns demo.goldly.ui-test.vega-nav
  (:require
   [demo.goldly.view.vega-nav :refer [vega-nav-plot]]))


(def vega-nav-plot-test-data
  [{:nav 100.0 :index 1}
   {:nav 120.0 :index 2}
   {:nav 150.0 :index 3}])

(vega-nav-plot vega-nav-plot-test-data)