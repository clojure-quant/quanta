(defn link-href [href text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])

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

(defn vega-nav-plot [data]
  [vegalite
   {:box :lg
    :spec {:width "1000"
           :description "NAV Plot"
           :data {:values data} ;data
           :mark "line"
           :encoding  {;:x "ordinal" ;{:field "index" :type "quantitative"}
                       :x {:field :index
                           :type "ordinal"}
                       ;:x date-axes
                       :y {:field "nav", :type "quantitative"}
                       ;:color "blue"
                       }}}])

(def test-data
  [{:nav 100.0 :index 1}
   {:nav 120.0 :index 2}
   {:nav 150.0 :index 3}])

(defmethod reagent-page :test/experiment [{:keys [route-params query-params handler] :as route}]
  [:div
   [link-href "/" "main"]
   [:div.text-green-300 "experiments..."]
   [:p "add code here..."]
   [vega-nav-plot test-data]
   [:p "end of vega nav plot"]])