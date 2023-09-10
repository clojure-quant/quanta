(ns ta.viz.nav-vega
  (:require
   [ui.vega :refer [vegalite]]))

; :date
; :open# :long$ :short$ :net$ 
; :pl-u :pl-r :pl-r-cum

(defn nav-vega [data]
[vegalite
 {:width "full"
  :height 400
  :spec {:description "Portfolio eval result."
         :data {:values data}
         :width "800"
         :vconcat [{:width "full"
                    :height 300
                    :mark "line"
                    :encoding {:x {:field "date" :type "temporal"}
                               :y {:field "pl-r-cum", :type "quantitative"}}}
                   {:width "full"
                    :height 100
                    :mark "bar"
                    :encoding {:x {:field "date" :type "temporal"
                                   :axis {:labels false :description ""}}
                               :y {:field "pl-u", :type "quantitative"}}}
                    {:width "full"
                    :height 100
                    :mark "bar"
                    :encoding {:x {:field "date" :type "temporal"
                                   :axis {:labels false}}
                               :y {:field "net$", :type "quantitative"}}}
                   {:width "full"
                    :height 100
                    :mark "bar"
                    :encoding {:x {:field "date" :type "temporal"
                                   :axis {:labels false}  }
                               :y {:field "open#", :type "quantitative"}}}
                    ]
         
         
         }}])


;:color {:field "symbol", :type "nominal"}

    ;:axis {;:tickCount 8
                                ;:labelAlign "left"
                                ;:labelExpr "[timeFormat(datum.value, '%b'), timeFormat(datum.value, '%m') == '01' ? timeFormat(datum.value, '%Y') : '']"
                                ;:labelOffset 4
                                ;:labelPadding -24
                                ;:tickSize 30
                                ;:gridDash {:condition {:test {:field "value" :timeUnit "month", :equal 1}, :value []} :value [2,2]}
                                ;:tickDash {:condition {:test {:field "value", :timeUnit "month", :equal 1}, :value []} :value [2,2]}
                          ;      }