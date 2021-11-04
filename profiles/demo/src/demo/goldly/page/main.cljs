
; main page 


(defmethod reagent-page :user/main [{:keys [route-params query-params handler] :as route}]
  [:div
   [:h1.text-xl.text-red-600 "trateg demo"]

   [:p.text-blue.text-xl "dev tools"]
   [link-dispatch [:bidi/goto :viewer :query-params {}] "notebook viewer"]
   [link-dispatch [:bidi/goto :scratchpad] "scratchpad"]
   [link-dispatch [:bidi/goto :environment] "environment"]
   [link-dispatch [:bidi/goto :devtools] "devtools help"]

   [:p.text-blue.text-xl "warehouse"]
   [link-href "/warehouse" "warehouse"]

   [:p.text-blue.text-xl "backtest"]
   [link-href "/algo/backtest" "backtest"]

   [:p.text-blue.text-xl "backtest components"]
   [link-href "/algo/chart" "highchart gann"]
   [link-href "/algo/table" "table s&p"]

   [:p.text-blue.text-xl "docs"]
   [video {:url "https://www.youtube.com/watch?v=JGhOa9TZYx8"}]
   
   
   ])