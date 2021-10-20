
; main page 


(defmethod reagent-page :user/main [{:keys [route-params query-params handler] :as route}]
  [:div
   [:h1.text-xl.text-red-600 "trateg demo"]

   [link/href "/goldly/about" "goldly developer tools"]
   [link/href "/backtest" "backtest"]
   [link/href "/highchart" "highchart"]
   [link/href "/highchartstudy" "highchart-study"]

   [link/href "/chart" "chart"]

   [link/href "/test/heatmap" "test - heatmap"]
   [link/href "/test/histogram" "test - histogram"]
   [link/href "/test/experiment" "test - experiment"]
   [link/href "/error" "error"]])