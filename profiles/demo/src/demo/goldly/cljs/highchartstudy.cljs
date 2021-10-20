
(defonce highchartstudy-state
  (r/atom {;:name "buy-hold s&p"
           :name "gann BTC"
           :name-loaded nil
           :data nil
           :mode :highchart
            ;:pr-str
           }))
(defn load-studychart [name name-loaded]
  (if name
    (when (not (= name name-loaded))
      (info (str "loading: " name))
      (swap! highchartstudy-state assoc :name-loaded name)
      (run-a highchartstudy-state [:data] :ta/chart-algo name)
      nil)
    (do (swap! highchartstudy-state assoc :data nil)
        nil)))

(defn highchartstudy-page [route]
  (let [{:keys [name name-loaded data mode]} @highchartstudy-state]
    (do (load-studychart name name-loaded)
        nil)
    [:div.h-screen.w-screen.bg-red-500
     [:div.flex.flex-col.h-full.w-full

     ; "menu"
      [:div.flex.flex-row.bg-blue-500
       [link/href "/" "main"]]
     ; "main"
      (if data
        (case mode
          :pr-str [:div.bg-red-500 (pr-str data)]
          :highchart [highstock {:box :fl ; :lg
                                 :data (:highchart data)}])
        [:div "no data "])]]))

(add-page highchartstudy-page :user/highchartstudy)