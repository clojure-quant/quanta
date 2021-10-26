(defonce backtest-state
  (r/atom {:algos []
           :symbol nil
           :symbol-loaded nil
           :data {}
           :mode :metrics}))

(defonce ui-options [:pr-str
                     :metrics
                     :roundtrips
                     :nav-table
                     :nav-chart
                     :study-chart
                     :study-table
                     :study-table-tradeonly])

(run-a backtest-state [:algos] :algo/names)

(defn run-backtest [symbol symbol-loaded]
  (if symbol
    (when (not (=  symbol symbol-loaded))
      (info (str "loading: " symbol))
      (swap! backtest-state assoc :symbol-loaded symbol)
      (run-a backtest-state [:data] :algo/metrics symbol)
      nil)
    (do (swap! backtest-state assoc :data nil)
        nil)))

(defn backtest-page [route]
  (let [{:keys [algos symbol symbol-loaded data mode]} @backtest-state]
    (do (run-backtest symbol symbol-loaded)
        nil)
    [:div.h-screen.w-screen.bg-red-500
     [:div.flex.flex-col.h-full.w-full

     ; "menu"
      [:div.flex.flex-row.bg-blue-500
       [link/href "/" "main"]
       [input/select {:nav? true
                      :items (or algos [])}
        backtest-state [:symbol]]
       [input/select {:nav? false
                      :items ui-options}
        backtest-state [:mode]]]
     ; "main"
      (if data
        (case mode
          :pr-str [:div.bg-red-500 (pr-str data)]
          :metrics (metrics-view (:data @backtest-state))
          :roundtrips (roundtrips-view (get-in @backtest-state [:data :roundtrips]))
          :nav-table (navs-view (get-in @backtest-state [:data :nav]))
          :nav-chart (navs-chart (get-in @backtest-state [:data :nav]))
          :study-chart (study-chart symbol)
          :study-table (study-table symbol false)
          :study-table-tradeonly (study-table symbol true)
          ;:frisk [frisk data]
          ;:table [table data]
          ;:histogram [histogram data]
          ;:chart [chart data]
          )
        [:div "no data "])]]))

(add-page backtest-page :algo/backtest)