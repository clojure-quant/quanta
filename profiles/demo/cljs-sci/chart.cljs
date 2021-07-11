

(defonce chart-state
  (r/atom {:symbol-loaded nil
           :symbol nil
           :symbols "loading symbols"}))

(run-a chart-state [:symbols] :ta/symbols "test")
;(run-a chart-state [:t] :ta/table-spec "FSDAX")

(defn table [data]
  (if data
    #_[:div.bg-red-500 (pr-str data)]
    #_[frisk data]
    [aggrid {:data data
             :box :fl
             :pagination :true
             :paginationAutoPageSize true
             }]
    [:div "no data "]))

(defn load-data [symbol symbol-loaded]
  (if symbol
    (when (not (=  symbol symbol-loaded))
      (info (str "loading: " symbol))
      (swap! chart-state assoc :symbol-loaded symbol)
      (run-a chart-state [:data] :ta/table symbol)
      nil)
    (do (swap! chart-state assoc :data nil)
        nil)))

(defn chart-page [route]
  (let [{:keys [symbols symbol symbol-loaded data]} @chart-state]
    (do (load-data symbol symbol-loaded)
        nil)
    [:div.h-screen.w-screen.bg-red-500
     [:div.flex.flex-col.h-full.w-full

     ; "menu"
      [:div.flex.flex-row.bg-blue-500
       [link/href "/" "main"]
       [input/select {:nav? true
                      :items (or symbols [])}
        chart-state [:symbol]]]
     ; "main"
      [table data]]]))

(add-page chart-page :user/chart)

;(defmethod reagent-page :user/chart [{:keys [route-params query-params handler] :as route}]
;  [chart-page])