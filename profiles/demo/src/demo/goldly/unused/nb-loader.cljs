
(defn notebook-list [notebook-ns-list]
  [:table
   (for [n notebook-ns-list]
     [:tr
      [:a {:href (str "/viewer/notebook/" n)}
       [:td n]]])])

(defmethod reagent-page :viewer/notebooks [{:keys [route-params query-params handler] :as route}]
  ;(get-notebooks-once)
  [:div
   [link-href "/" "main"]
   [:div.text-green-300 "notebooks..."]
   [:p "there are nr notebooks: " (count (get-in @viewer-state [:notebooks :data]))]
   [:p.text-red-300 (pr-str @viewer-state)]
   [notebook-list (get-in @viewer-state [:notebooks :data])]
   [:p "add code here..."]])

;:resources [["1" :edn]]

(defn print-status [x]
  (println "status: " x))
(rf/dispatch [:ws/send [:ws/status []] print-status 5000])

;(def nb-clear-a
;  (rf/subscribe [:notebook/clear]))
