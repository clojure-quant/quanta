







(defn gann-page [{:keys [route-params query-params handler] :as route}]
  (let [*state (r/atom {:params {:symbol "BTCUSD"
                                 ;:wh :crypto
                                 ;:dt-start (parse-date "2021-01-01")
                                 ;:dt-end (parse-date "2021-12-31")
                                 }
                        :data [:div "data not yet loaded."]})
        get-data (fn [& args]
                   (let [p (-> (:params @*state)
                               (assoc :wh
                                      (if (= "BTCUSD" (get-in @*state [:params :symbol]))
                                        :crypto
                                        :stocks)))]
                     (run-a *state [:data] :gann/chart p)))]
    (fn [{:keys [route-params query-params handler] :as route}]
      [:div
       [:div.flex.flex-cols
        [:div.text-green-500.mt-5.text-xl.mb-5 "gann"]
        [:div.w-64
         [input/select
          {:nav? false
           :items ["BTCUSD" "SPY" "GLD"]}
          *state [:params :symbol]]]
        [input/button {:on-click get-data} "show gann"]
        [link-href "/" "main"]]
       (:data @*state)
       [:div.bg-gray-500.mt-12 "params:" (pr-str (:params @*state))]])))

(add-page gann-page :gann)