(ns ta.viz.page.series
  (:require
   [reagent.core :as r]
   [options.edit :as edit]
   [ta.tradingview.goldly.view.aggrid :refer [bars-table]]
   [ta.viz.lib.loader :refer [clj->a]]
   [ta.viz.lib.ui :refer [link-href]]))

(defn pickable-series-view []
  (let [state (r/atom {:location :duckdb
                       :exchange :forex
                       :interval :d
                       :asset "MSFT"})
        bars (r/atom {})]
    (fn []
      [:div.flex.flex-col.h-full.w-full
       [:div.flex-row.w-full.h-12
        [edit/string
         {:set-fn #(swap! state assoc :asset %)
          :options {:class "placeholder-gray-400 text-gray-700 relative bg-white rounded text-sm border border-gray-400 outline-none focus:outline-none focus:shadow-outline"}}
         (:asset @state)]
        [edit/select
         {:set-fn #(swap! state assoc :location %)
          :options {:class "placeholder-gray-400 text-gray-700 relative bg-white rounded text-sm border border-gray-400 outline-none focus:outline-none focus:shadow-outline"
                    :spec [:duckdb :nippy]}}
         (:location @state)]
        [edit/select
         {:set-fn #(swap! state assoc :exchange %)
          :options {:class "placeholder-gray-400 text-gray-700 relative bg-white rounded text-sm border border-gray-400 outline-none focus:outline-none focus:shadow-outline"
                    :spec [:us :forex :crypto]}}
         (:exchange @state)]
        [edit/select
         {:set-fn #(swap! state assoc :interval %)
          :options {:class "placeholder-gray-400 text-gray-700 relative bg-white rounded text-sm border border-gray-400 outline-none focus:outline-none focus:shadow-outline"
                    :spec [:d :m]}}
         (:interval @state)]
        [edit/button
         {:options {:class "text-gray-700 relative bg-white rounded text-sm border border-gray-400 outline-none focus:outline-none focus:shadow-outline"
                    :name " GO! "
                    :on-click (fn []
                                (println "loading series..")
                                (clj->a bars 'ta.db.bars.sources/get-bars-source
                                        (:location @state)
                                        {:asset (:asset @state)
                                         :calendar [(:exchange @state) (:interval @state)]}
                                        {}))}}
         ""]]
       (case (:status @bars)
         :loading [:div.w-full.h-full "loading"]
         :error [:div.w-full.h-full "error!"]
         :data [:div.w-full.h-full
                [bars-table (:data @bars)]]
         [:div.w-full.h-full "unknown: status:" (pr-str @bars)])])))

(defn series-page [_route]
  [:div.h-screen.w-screen.bg-red-500
   [:div.flex.flex-col.h-full.w-full
      ;[:div.flex.flex-row.bg-blue-500
    [link-href "/" "main"]
    ;[series-view  "BTCUSD" "D"]
    [pickable-series-view]
   ;    ]
    ]])


