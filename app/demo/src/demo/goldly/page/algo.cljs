(defonce algo-state
  (r/atom {:algos []
           :algo nil
           :opts {:symbol "SPY"}
           :data-loaded nil
           :data-tradingview nil
           :data {}
           :page :metrics}))

(def symbol-list ["TLT" "SPY" "QQQ" "EURUSD"])

(defn pr-data [context data]
  [:div.bg-red-500 (pr-str data)])

(defn pr-highchart [context data]
  (if data
    [highstock {:box :fl ; :lg
                :data data}]
     ;[pr-data data]
    [:div "no data."]))

(defn study-table [context data]
  (if data
    [:div.w-full.h-full
     ;[:div.bg-red-500 (pr-str data)]
     [:div {:style {:width "100%" ;"40cm"
                    :height "100%" ;"70vh" ;  
                    :background-color "blue"}}
      [aggrid {:data data
               :columns (study-columns context data)
               :box :fl
               :pagination :true
               :paginationAutoPageSize true}]]]
    [:div "no data "]))

(defn tv-events [algo opts data-tradingview]
  (when algo
    (when (not (= [algo opts] data-tradingview))
      (info (str "changing tv data for running algo for: " algo "opts: " opts))
      (swap! algo-state assoc :data-tradingview [algo opts])
      (wrap-chart-ready
       (fn []
         (set-symbol (:symbol opts) "1D")))
      nil)))

(defn tv-page [context data]
  (let [{:keys [algo opts data-tradingview]} @algo-state]
    [:div.h-full.w-full
     (when @tv-widget-atom
       [tv-events algo opts data-tradingview])
     [tradingview-chart {:feed :ta
                         :options {:autosize true}}]]))

(defonce pages
  {;:pr-str [pr-data []] 
   :metrics  [metrics-view [:stats]]
   :roundtrips [roundtrips-view [:ds-roundtrips]]
   :nav-table [navs-view [:stats :nav]]
   :nav-chart [navs-chart [:stats :nav]]
   :highchart [pr-highchart [:highchart]]
   :study-table [study-table [:ds-study]]
   ;:study-table-tradeonly]
   :tradingview [tv-page [:tradingview]]})

(run-a algo-state [:algos] :algo/names) ; get once the names of all available algos

(defn run-algo [algo opts data-loaded]
  (when algo
    ;(info (str "run-algo check: " algo " opts: " opts))
    (when (not (= [algo opts] data-loaded))
      (info (str "running algo for: " algo "opts: " opts))
      (swap! algo-state assoc :data {})
      (swap! algo-state assoc :data-loaded [algo opts])
      (run-a algo-state [:data] :algo/run algo opts)
      nil)))

(defn context [data]
  (:study-extra-cols data))

(defn page-renderer [data page]
  (if data
    (let [[view-fn view-data] (page pages)]
      (println "page renderer context: " context)
      (if view-fn
        (if view-data
          [view-fn (context data) (get-in data view-data)]
          [:div "no data for view: " page])
        [:div "no view-fn for view: " page]))
    [:div "no data "]))

(defn algo-menu []
  [:div.flex.flex-row.bg-blue-500
   [link-href "/" "main"]
   [input/select {:nav? false
                  :items (or (:algos @algo-state) [])}
    algo-state [:algo]]
   [input/select {:nav? false
                  :items symbol-list}
    algo-state [:opts :symbol]]
   [input/select {:nav? false
                  :items (keys pages)}
    algo-state [:page]]])

(defn algo-ui []
  (fn []
    (let [{:keys [algos algo opts data-loaded data page]} @algo-state]
      [:div.flex.flex-col.h-full.w-full
       (do (run-algo algo opts data-loaded)
           nil)
       [algo-menu]
       [page-renderer data page]])))

(defn algo-page [route]
  [:div.h-screen.w-screen.bg-red-500
   [algo-ui]])

(add-page algo-page :algo/backtest)

