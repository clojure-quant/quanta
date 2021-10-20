
(defonce highchart-state
  (r/atom {:symbol "ETHUSD"
           :w :crypto
           :frequency "D"
           :data nil
           :mode
           :highchart
            ;:pr-str
           }))
(defn load-chart [w f symbol data]
  (if symbol
    (when (not data)
      (info (str "loading: " symbol))
      (run-a highchart-state [:data] :ta/highchart w f symbol)
      nil)
    (do (swap! backtest-state assoc :data nil)
        nil)))

(defn highchart-page [route]
  (let [{:keys [w frequency symbol data mode]} @highchart-state]
    (do (load-chart w frequency symbol data)
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
                                 :data data}])
        [:div "no data "])]]))

(add-page highchart-page :user/highchart)