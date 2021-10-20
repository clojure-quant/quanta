

(defonce backtest-state
  (r/atom {:algos []
           :symbol nil
           :symbol-loaded nil
           :data {}
           :mode :pr-str}))

(defonce ui-options [:pr-str :metrics :roundtrips :navs :navs-chart])

(run-a backtest-state [:algos] :ta/algos)

(defn run-backtest [symbol symbol-loaded]
  (if symbol
    (when (not (=  symbol symbol-loaded))
      (info (str "loading: " symbol))
      (swap! backtest-state assoc :symbol-loaded symbol)
      (run-a backtest-state [:data] :ta/run-algo symbol)
      nil)
    (do (swap! backtest-state assoc :data nil)
        nil)))

(defn metrics-view [{:keys [rt-metrics nav-metrics options comment]}]
  [:div
   [:h1.bg-blue-300.text-xl "comment:" comment]
   [:p "options:" (pr-str options)]
   [:table
    [:tr
     [:td "cum-pl"]
     [:td (:cum-pl nav-metrics)]]
    [:tr
     [:td "max-dd"]
     [:td (:max-dd nav-metrics)]]
    [:tr
     [:td "# trades"]
     [:td (:trades rt-metrics)]]]
   [:table
    [:tr
     [:td {:style {:width "3cm"}} " "]
     [:td "win"]
     [:td "loss"]]
    [:tr
     [:td "%winner"]
     [:td (:win-nr-prct rt-metrics)]
     [:td ""]]
    [:tr
     [:td "avg pl"]
     [:td (:avg-win-log rt-metrics)]
     [:td (:avg-loss-log rt-metrics)]]
    [:tr
     [:td "avg bars"]
     [:td (:avg-bars-win rt-metrics)]
     [:td (:avg-bars-loss rt-metrics)]]]])

(defn roundtrips-view [roundtrips]
  [:div
   [:h1 "roundtrips " (count roundtrips)]
   (when (> (count roundtrips) 0)
     [:div {:style {:width "40cm"
                    :height "70vh"
                    :background-color "blue"}}
      [aggrid {:data roundtrips
               :box :fl
               :pagination :true
               :paginationAutoPageSize true}]])])

(defn navs-view [navs]
  [:div
   [:h1 "navs " (count navs)]
   (when (> (count navs) 0)
     [:div {:style {:width "40cm"
                    :height "70vh"
                    :background-color "blue"}}
      [aggrid {:data navs
               :box :fl
               :pagination :true
               :paginationAutoPageSize true}]])])

(defn navs-chart [navs]
  (let [navs-with-index (map-indexed (fn [i v]
                                       {:index i
                                        :nav (:nav v)}) navs)
        navs-with-index (into [] navs-with-index)]
    [:div
     [:h1 "navs " (count navs-with-index)]
     (when (> (count navs) 0)
       [:div ; {:style {:width "50vw"}}
        ;(pr-str navs-with-index)
        [vega-nav-plot navs-with-index]])]))

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
          :navs (navs-view (get-in @backtest-state [:data :nav]))
          :navs-chart (navs-chart (get-in @backtest-state [:data :nav]))
          ;:frisk [frisk data]
          ;:table [table data]
          ;:histogram [histogram data]
          ;:chart [chart data]
          )
        [:div "no data "])]]))

(add-page backtest-page :user/backtest)