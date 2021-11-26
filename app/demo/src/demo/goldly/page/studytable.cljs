(defonce studytable-state
  (r/atom {:name-loaded nil
           :data nil}))

(defn load-studytable [name filter-signal name-loaded]
  (if name
    (when (not (= name name-loaded))
      (info (str "loading: " name))
      (swap! studytable-state assoc :name-loaded name)
      (run-a studytable-state [:data] :algo/table name)
      nil)
    (do (swap! studytable-state assoc :data nil)
        nil)))

(defn study-table [name filter-signal]
  (let [{:keys [name-loaded data mode]} @studytable-state]
    (do (load-studytable name filter-signal name-loaded)
        nil)
    (if data
      [:div
        ;[:div.bg-red-500 (pr-str (:table data))]
       [:div {:style {:width "40cm"
                      :height "70vh"
                      :background-color "blue"}}
        [aggrid {:data (:table data)
                 :box :fl
                 :pagination :true
                 :paginationAutoPageSize true}]]]
      [:div "no data "])))

(defn studytable-page [route]
  [:div.h-screen.w-screen.bg-red-500
   [:div.flex.flex-col.h-full.w-full
     ; "menu"
    [:div.flex.flex-row.bg-blue-500
     [link-href "/" "main"]]
     ; "main"
    [study-table "gann BTC" false] ; "buy-hold s&p"
    ]])

(add-page studytable-page :algo/table)