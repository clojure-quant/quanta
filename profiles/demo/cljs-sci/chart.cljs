
(defn link-href [href text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])
(defonce chart-state (r/atom {:symbol-loaded nil
                        :symbol nil
                        :symbols "loading symbols"}))

(run-a chart-state [:symbols] :ta/symbols)
(run-a chart-state [:t] :ta/table-spec "FSDAX")

(defmethod reagent-page :user/chart [{:keys [route-params query-params handler] :as route}]
  ^:R
  [:div
   [link-href "/" "main"]
   (when (not (= (:symbol @chart-state) (:symbol-loaded @chart-state)))
     (swap! chart-state assoc :symbol-loaded (:symbol @chart-state))
     (run-a chart-state [:table-spec] :ta/table-spec (:symbol @chart-state)))
   [input/select
    {:nav? true
     :items (:symbols @chart-state)}
    chart-state [:symbol]]
   (when (:table-spec @chart-state)
     [aggrid (:t @chart-state)])
   [:p "current symbol: " (pr-str (:symbol @chart-state))]])