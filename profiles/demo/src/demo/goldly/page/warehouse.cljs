(defonce warehouse-state
  (r/atom {:w :crypto
           :frequency "D"
           :data nil
           :loading false}))

(defn load-data [w f data]
  (if symbol
    (when (not data)
      (info (str "loading: " symbol))
      ; (warehouse-overview :stocks "D")
      (run-a warehouse-state [:data] :ta/warehouse-overview w f)
      nil)
    (do (swap! warehouse-state assoc :data nil)
        nil)))

(defn warehouse-page [route]
  (let [{:keys [w frequency data]} @warehouse-state]
    (do (load-data w frequency data)
        nil)
    [:div.h-screen.w-screen.bg-red-500
     [:div.flex.flex-col.h-full.w-full

     ; "menu"
      [:div.flex.flex-row.bg-blue-500
       [link-href "/" "main"]]
     ; "main"
      (if data
        [:div
         ;(pr-str data)
         [table data]]

        [:div "no data "])]]))

(add-page warehouse-page :user/warehouse)