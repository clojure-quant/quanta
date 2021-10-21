(defonce highchartstudy-state
  (r/atom {:name-loaded nil
           :data nil}))

(defn load-studychart [name name-loaded]
  (if name
    (when (not (= name name-loaded))
      (info (str "loading: " name))
      (swap! highchartstudy-state assoc :name-loaded name)
      (run-a highchartstudy-state [:data] :algo/chart name)
      nil)
    (do (swap! highchartstudy-state assoc :data nil)
        nil)))

(defn study-chart [name]
  (let [{:keys [name-loaded data mode]} @highchartstudy-state]
    (do (load-studychart name name-loaded)
        nil)
    (if data
      [:div
        ;[:div.bg-red-500 (pr-str data)]
       [highstock {:box :fl ; :lg
                   :data (:highchart data)}]]
      [:div "no data "])))

(defn highchartstudy-page [route]
  [:div.h-screen.w-screen.bg-red-500
   [:div.flex.flex-col.h-full.w-full
     ; "menu"
    [:div.flex.flex-row.bg-blue-500
     [link/href "/" "main"]]
     ; "main"
    [study-chart "gann BTC"] ; "buy-hold s&p"
    ]])

(add-page highchartstudy-page :algo/chart)