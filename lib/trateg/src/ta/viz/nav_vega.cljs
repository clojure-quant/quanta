(ns ta.viz.nav-vega
  (:require
   [ui.vega :refer [vegalite]]))

; :date
; :open# :long$ :short$ :net$ 
; :pl-u :pl-r :pl-r-cum

(def width 500)

(defn nav-vega [data]
[vegalite
 {:box :fl
  :spec {:description "Portfolio eval result."
         :data {:values data}
         :width width
         :height "500"
         :overflow true
         :box :fl
         :vconcat [{:height 300
                    :width width
                    :layer [{:mark "line"
                             :encoding {:x {:field "date" :type "temporal"}
                                        :y {:field "pl-cum", :type "quantitative"}
                                        :color {:value "red"}}}
                            {:mark "line"
                             :encoding {:x {:field "date" :type "temporal"}
                                        :y {:field "pl-r-cum", :type "quantitative"}
                                        :color {:value "blue"}
                                        }}]
                    }
                   {:height 100
                    :width width
                    :mark "bar"
                    :encoding {:x {:field "date" :type "temporal"
                                   :axis {:labels false :description ""}}
                               :y {:field "pl-u", :type "quantitative"}}}
                   {:height 100
                    :width width
                    :mark "bar"
                    :encoding {:x {:field "date" :type "temporal"
                                   :axis {:labels false}}
                               :y {:field "net$", :type "quantitative"}}}
                   {:height 100
                    :width width
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