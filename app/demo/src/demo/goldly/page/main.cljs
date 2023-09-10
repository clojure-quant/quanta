(ns demo.goldly.page.main
  (:require
   [reagent.core :as r]
   [goldly.page :as page]
   [demo.goldly.lib.ui :refer [link-dispatch link-href]]))

; main page 

(defn main-page [{:keys [_route-params _query-params _handler] :as _route}]
  [:div

   ; trateg web ui
   [:div.bg-blue-300.m-5
    [:h1.text-xl.text-red-600 "trateg "]

    [:p.text-blue.text-xl "tradingview"]
    [link-dispatch [:bidi/goto :tradingview/algo] "tradingview-algo"]
    [link-dispatch [:bidi/goto :tradingview/udf] "tradingview-udf"]

    [:p.text-blue.text-xl "backtest"]
    [link-href "/algo/backtest" "backtester"]

    [:p.text-blue.text-xl "warehouse"]
    [link-href "/warehouse" "warehouse"]
    [link-href "/series" "series"]]
    
   ; trateg demos
   [:div.bg-blue-300.m-5
    [:h1.text-xl.text-red-600 "gann tools"]
    [link-href "/gann" "gann chart"]
    [link-href "/joseph" "joseph-tradingview"]
    [link-dispatch [:bidi/goto :joseph/nav]  "joseph-nav"]
    ]

   ; goldly developer tools
   [:div.bg-blue-300.m-5
     [link-dispatch [:bidi/goto :user/test] "test-page"]
    [:p.text-blue.text-xl "goldly developer tools"]
    [link-dispatch [:bidi/goto :viewer :query-params {}] "notebook viewer"]
    [link-dispatch [:bidi/goto :scratchpad] "scratchpad"]
    [link-dispatch [:bidi/goto :environment] "environment"]
    [link-dispatch [:bidi/goto :devtools] "devtools help"]]

;
   ])

(page/add main-page :user/main)
