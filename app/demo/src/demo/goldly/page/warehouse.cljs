(ns demo.goldly.page.warehouse
  (:require
   [reagent.core :as r]
   [goldly.page :as page]
   [goldly.service :refer [run-a]]
   [demo.goldly.lib.ui :refer [link-href]]
   [demo.goldly.view.aggrid :refer [table]]))

(defonce warehouse-state
  (r/atom {:w :crypto
           :frequency "D"
           :data nil
           :loading false}))

(defn load-data [w f data]
  (if symbol
    (when (not data)
      ; (warehouse-overview :stocks "D")
      (run-a warehouse-state [:data]
             'ta.warehouse.overview/overview-map w f)
      nil)
    (do (swap! warehouse-state assoc :data nil)
        nil)))

(defn warehouse-page [_route]
  (let [{:keys [w frequency data]} @warehouse-state]
    (load-data w frequency data)
    [:div.h-screen.w-screen.bg-red-500
     [:div.flex.flex-col.h-full.w-full

     ; "menu"
      [:div.flex.flex-row.bg-blue-500
       [link-href "/" "main"]]
     ; "main"
      (if data
        [:div
         ;(pr-str data)
         [table data]]

        [:div "no data "])]]))

(page/add warehouse-page :user/warehouse)
