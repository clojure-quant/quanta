
; main page 


(defmethod reagent-page :user/main [{:keys [route-params query-params handler] :as route}]
  [:div

   ; trateg web ui
   [:div.bg-blue-300.m-5
    [:h1.text-xl.text-red-600 "trateg "]

    [:p.text-blue.text-xl "tradingview"]
    [link-dispatch [:bidi/goto :tradingview] "tradingview-chart"]

    [:p.text-blue.text-xl "backtest"]
    [link-href "/algo/backtest" "backtester"]

    [:p.text-blue.text-xl "warehouse"]
    [link-href "/warehouse" "warehouse"]]

   ; trateg demos
   [:div.bg-blue-300.m-5
    [:h1.text-xl.text-red-600 "some demos to show what you could do too.."]
    [:p.text-blue.text-xl "backtest components"]
    [link-href "/algo/chart" "highchart gann"]
    [link-href "/algo/table" "table s&p"]
    [:p.text-blue.text-xl "gann chart"]
    [link-href "/gann" "gann"]]

   ; trateg docs
   [:div.bg-blue-300.m-5
    [:p.text-blue.text-xl "trateg docs"]
    [:p "with luck, some docs might be added soon"]
    ;[video {:url "https://www.youtube.com/watch?v=JGhOa9TZYx8"}]
    ]

   ; goldly developer tools
   [:div.bg-blue-300.m-5
    [:p.text-blue.text-xl "goldly developer tools"]
    [link-dispatch [:bidi/goto :viewer :query-params {}] "notebook viewer"]
    [link-dispatch [:bidi/goto :scratchpad] "scratchpad"]
    [link-dispatch [:bidi/goto :environment] "environment"]
    [link-dispatch [:bidi/goto :devtools] "devtools help"]]

;
   ])