

(defmethod reagent-page :test/heatmap [{:keys [route-params query-params handler] :as route}]
  [:div
   [link-href "/" "main"]
   [:div.text-green-300 "vega..."]

   [vegalite {:box :sm
              :spec vega-spec-heatmap-url}]
   ;[:div "spec: " (pr-str {:spec s2})]
   [:div "cor matrix: " (pr-str demo-data-heatmap-cor-matrix)]
   [heatmap-corr-matrix demo-data-heatmap-cor-matrix]])



