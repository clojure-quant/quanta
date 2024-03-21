(ns ta.tradingview.goldly.page.tradingview-algo
  (:require
   [reagent.core :as r]
   [goldly.service.core :refer [run-a]]
   [input]
   [ta.tradingview.goldly.algo.context :as c]
   [ta.tradingview.goldly.dialog :refer [show-algo-dialog show-table-dialog]]
   [ta.tradingview.goldly.algo.tradingview :refer [tradingview-algo]]))

(defonce algo-ctx
  (c/create-algo-context "moon" {:symbol "QQQ" :frequency "D"}))

;; symbol/algo switcher

(defonce menu-ctx
  (r/atom {:algos []
           :symbols ["TLT" "SPY" "QQQ" "EURUSD"
                     "RIVN" "GOOGL" "FCEL" "NKLA" "INTC" "FRC" "AMZN" "WFC" "PLTR"
                     "BZ0" "NG0"  "RB0"  "ZC0"  "MES0"  "M2K0"  "MNQ0"  "MYM0"]}))

(run-a menu-ctx [:algos]
       'ta.algo.manager/algo-names) ; get once the names of all available algos

(defn algo-info [algo]
  (let [algo-loaded (r/atom nil)]
    (when algo
      (when-not (= @algo-loaded algo)
        (run-a menu-ctx [:algoinfo] 'ta.algo.manager/algo-info algo)
        nil))))

(defn algo-menu []
  (let [algo-input (c/get-algo-input-atom algo-ctx)]
    (fn []
      [:div.flex.flex-row.bg-blue-500
   ;[link-href "/" "main"]
       [input/select {:nav? false
                      :items (or (:algos @menu-ctx) [])}
        algo-input [:algo]]
       [input/select {:nav? false
                      :items (:symbols @menu-ctx)}
        algo-input [:opts :symbol]]
       [input/button {:on-click #(show-algo-dialog algo-ctx)} "options"]
   ;[input/button {:on-click #(reset-data)} "R!"]
       [input/button {:on-click #(show-table-dialog algo-ctx)} "table"]
   ;[input/button {:on-click get-window-demo} "get window"]
       ])))

(defn algo-ui []
  (fn []
    (let [{:keys [_algos algo algoinfo]} @menu-ctx]
      [:div.flex.flex-col.h-full.w-full
       [algo-menu]
       [algo-info algo]
       [:div.h-full.w-full
        [tradingview-algo algo-ctx]]])))

(defn tvalgo-page [_route]
  [:div.h-screen.w-screen.bg-red-500
   [algo-ui]])
