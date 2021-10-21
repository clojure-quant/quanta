
; main page 


(defmethod reagent-page :user/main [{:keys [route-params query-params handler] :as route}]
  [:div
   [:h1.text-xl.text-red-600 "trateg demo"]

   [link/href "/goldly/about" "goldly developer tools"]

   [:p.text-blue.text-xl "warehouse"]
   [link/href "/warehouse" "warehouse"]

   [:p.text-blue.text-xl "backtest"]
   [link/href "/algo/backtest" "backtest"]

   [:p.text-blue.text-xl "backtest components"]
   [link/href "/algo/chart" "highchart gann"]
   [link/href "/algo/table" "table s&p"]

   [:p.text-blue.text-xl "TEST"]
   [link/href "/test/heatmap" "test - heatmap"]
   [link/href "/test/histogram" "test - histogram"]
   [link/href "/test/experiment" "test - experiment"]
   [link/href "/error" "error"]])