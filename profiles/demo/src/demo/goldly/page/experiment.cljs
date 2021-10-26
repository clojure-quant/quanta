(defn link-href [href text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])

(defmethod reagent-page :test/experiment [{:keys [route-params query-params handler] :as route}]
  [:div
   [link-href "/" "main"]
   [:div.text-green-300 "experiments..."]
   [:p "add code here..."]
   [vega-nav-plot vega-nav-plot-test-data]
   [:p "end of vega nav plot"]])