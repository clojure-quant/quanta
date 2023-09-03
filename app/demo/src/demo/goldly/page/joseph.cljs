(ns demo.goldly.page.joseph
  (:require
   [reagent.core :as r]
   [goldly.page :as page]
   [demo.goldly.lib.loader :refer [clj->a]]
   [demo.goldly.lib.ui :refer [link-href]]
   [demo.goldly.view.aggrid :refer [table]]
   ;[demo.goldly.view.tsymbol :refer [symbol-picker]]
   ))


(defn trades-view []
  (let [trades (r/atom {})]
    (fn []
      (clj->a trades 'demo.algo.joseph/load-trades)
      [:div.w-full.h-full
       ;[:h1.text-bold.bg-green-500 "Bars for symbol: " s " f: " f]
       (case (:status @trades)
         :loading [:p "loading"]
         :error [:p "error!"]
         :data [:div.w-full.h-full 
                [table (:data @trades)]]
         [:p "unknown: status:" (pr-str @trades)])])))


(defn trades-page [_route]
  [:div.h-screen.w-screen.bg-red-500
   [:div.flex.flex-col.h-full.w-full
      ;[:div.flex.flex-row.bg-blue-500
    [link-href "/" "main"]
    ;[series-view  "BTCUSD" "D"]
    [trades-view]
   ;    ]
    ]])

(page/add trades-page :joseph/trades)
