(ns ta.viz.trade-metrics.nav-chart
  (:require
   [ui.vega :refer [vegalite]]))

(def date-axes
  {:field "date" :type "temporal"
                       ;  :axis {:tickCount 8
                       ;         :labelAlign "left"
                       ;         :labelExpr "[timeFormat(datum.value, '%b'), timeFormat(datum.value, '%m') == '01' ? timeFormat(datum.value, '%Y') : '']"
   :labelOffset 4
   :labelPadding -24
   :tickSize 30
   :gridDash {:condition {:test {:field "value" :timeUnit "month", :equal 1}
                          :value []}
              :value [2,2]}
   :tickDash {:condition {:test {:field "value", :timeUnit "month", :equal 1}
                          :value []}
              :value [2,2]}})

(defn nav-chart [data]
  ^:R
  [:div.w-full.h-full
   [vegalite
    {:box :fl ; :lg
     :spec {;:width "1000"
            :width "500" ;"100%"
            :height "500" ;"100%"
            :description "NAV Plot"
            :data {:values data} ;data
            :mark "line"
            :encoding  {;:x "ordinal" ;{:field "index" :type "quantitative"}
                        :x {:field :index
                            :type "ordinal"}
                       ;:x date-axes
                        :y {:field "nav", :type "quantitative"}
                       ;:color "blue"
                        }}}]])

