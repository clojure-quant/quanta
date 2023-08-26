(ns ta.tradingview.goldly.page
  (:require
    [goldly.page :as page]
    [ta.tradingview.goldly.tradingview :refer [tradingview-chart]]
    [ta.tradingview.goldly.feed.udf :refer [get-tradingview-options-udf-feed]]
    [ta.tradingview.goldly.interact :as interact]
  ))

(defn goto-symbol [s]
  [:a.pr-5.bg-blue-300 {:on-click #(interact/set-symbol s "1D")} s])

(defn tradingview-page [_route]
  [:div.w-screen.h-screen.m-0.p-0
   [:div.h-full.w-full.flex.flex-col
    [:div.h-64-w-full
     [goto-symbol "BTCUSD"]
     [goto-symbol "SPY"]
     [goto-symbol "TLT"]]
    [tradingview-chart {:feed (get-tradingview-options-udf-feed :ta)
                        :options {:autosize true}}]]])

(page/add tradingview-page :tradingview)
