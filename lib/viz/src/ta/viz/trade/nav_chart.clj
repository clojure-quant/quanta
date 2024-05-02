(ns ta.viz.trade.nav-chart
  (:require
   [ta.viz.ds.vega :refer [vega-render-spec]]))

(defn nav-chart [roundtrip-ds]
  (let [cols [:exit-date :cum-ret-volume]
        spec {:width "700"
              :height "550"
              :description "NAV"
              :mark "line"
              :encoding  {:y {:field :cum-ret-volume
                              :type "quantitative"
                              :color "blue"}
                          :x {:type "temporal"
                              :field :exit-date}}}]
    (vega-render-spec
     {:spec spec
      :cols cols}
     roundtrip-ds)))