

(defonce chart-state
  (r/atom {:symbol-loaded nil
           :symbol nil
           :symbols "loading symbols"
           :mode :table}))

(run-a chart-state [:symbols] :ta/symbols "test")
;(run-a chart-state [:t] :ta/table-spec "FSDAX")

(defn table [data]
  [aggrid {:data data
           :box :fl
           :pagination :true
           :paginationAutoPageSize true}])

(defn histogram [data]
  [:div "histogram not implemented"])

(defn chart [data]
  [:div "chart not implemented"])

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
  (let [{:keys [symbols symbol symbol-loaded data mode]} @chart-state]
    (do (load-data symbol symbol-loaded)
        nil)
    [:div.h-screen.w-screen.bg-red-500
     [:div.flex.flex-col.h-full.w-full

     ; "menu"
      [:div.flex.flex-row.bg-blue-500
       [link/href "/" "main"]
       [input/select {:nav? true
                      :items (or symbols [])}
        chart-state [:symbol]]
       [input/select {:nav? false
                      :items [:table :chart :histogram :frisk :pr-str]}
        chart-state [:mode]]]
     ; "main"
      (if data
        (case mode
          :pr-str [:div.bg-red-500 (pr-str data)]
          :frisk [frisk data]
          :table [table data]
          :histogram [histogram data]
          :chart [chart data])
        [:div "no data "])]]))

(add-page chart-page :user/chart)

;(defmethod reagent-page :user/chart [{:keys [route-params query-params handler] :as route}]
;  [chart-page])