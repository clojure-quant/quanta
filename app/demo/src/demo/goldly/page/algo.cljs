(defonce algo-state
  (r/atom {:algos []
           :symbol nil
           :symbol-loaded nil
           :data {}
           :page :metrics}))
  
(defn pr-data [data]
  [:div.bg-red-500 (pr-str data)])
 
(defn pr-highchart [data]
  (if data
     [highstock {:box :fl ; :lg
                 :data data}]  
     ;[pr-data data]
     [:div "no data."]))

(defn study-table [data]
  (if data
    [:div.w-full.h-full
     ;[:div.bg-red-500 (pr-str data)]
     [:div {:style {:width "100%" ;"40cm"
                    :height "100%" ;"70vh" ;  
                      :background-color "blue"}}
        [aggrid {:data data
                 :box :fl
                 :pagination :true
                 :paginationAutoPageSize true}]]]
      [:div "no data "]))

          ;:frisk [frisk data]
          ;:table [table data]
          ;:histogram [histogram data]
          ;:chart [chart data]
 

(defonce pages
  {;:pr-str [pr-data []] 
   :metrics  [metrics-view [:stats]]
   :roundtrips [roundtrips-view [:ds-roundtrips]] 
   :nav-table [navs-view [:stats :nav]]
   :nav-chart [navs-chart [:stats :nav]]
   :highchart [pr-highchart [:highchart]]
   :study-table [study-table [:ds-study]]
   ;:study-table-tradeonly]
  })

(run-a algo-state [:algos] :algo/names) ; get once the names of all available algos




(defn run-algo [symbol symbol-loaded]
  (if symbol
    (when (not (=  symbol symbol-loaded))
      (info (str "running algo for: " symbol))
      (swap! algo-state assoc :data nil)
      (swap! algo-state assoc :symbol-loaded symbol)
      (run-a algo-state [:data] :algo/run symbol {})
      nil)
    nil))

(defn page-renderer [data page]
  (if data
    (let [[view-fn view-data] (page pages)]
      (if view-fn 
        (if view-data
          [view-fn (get-in data view-data)]
          [:div "no data for view: " page])
        [:div "no view-fn for view: " page]))
    [:div "no data "]))


(defn algo-page [route]
  (let [{:keys [algos symbol symbol-loaded data page]} @algo-state]
    (do (run-algo symbol symbol-loaded)
        nil)
    [:div.h-screen.w-screen.bg-red-500
     [:div.flex.flex-col.h-full.w-full
     ; "menu"
      [:div.flex.flex-row.bg-blue-500
       [link-href "/" "main"]
       [input/select {:nav? false
                      :items (or algos [])}
        algo-state [:symbol]]
       [input/select {:nav? false
                      :items (keys pages)}
        algo-state [:page]]]
     ; "main"
    [page-renderer data page]
        ]]))

(add-page algo-page :algo/backtest)

