

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

(defn scratchpad []
  (let [nb (:scratchpad @viewer-state)]
    [:div.bg-indigo-300.p-7
     [:h1.text-xl.text-blue-800.text-xl "scratchpad "]
     [view-notebook nb]]))

(defn debug-viewer-state []
  [:div.bg-gray-500.pt-10
   [:p "viewer state: " (-> @viewer-state pr-str)]])

(defmethod reagent-page :viewer/scratchpad [{:keys [route-params query-params handler] :as route}]
  (let [{:keys [nbns]} route-params]
    [:div.bg-green-300.w-screen.h-screen
   ;[:p "route-params: " (pr-str route-params)]
     [link-href "/" "main"]
     [:span.bg-blue-300 "ws connected: " (pr-str @connected-a)]
     [:div "notebooks:" (pr-str (viewer-notebooks))]
     ;[text-url "asdf" "asdf"]
     [scratchpad]
     [debug-viewer-state]]))

(defmethod reagent-page :viewer/notebook [{:keys [route-params query-params handler] :as route}]
  (let [{:keys [nbns]} route-params]
    [:div
   ;[:p "route-params: " (pr-str route-params)]
     [link-href "/" "main"]
     [:div.text-blue-700.text-xl "notebook viewer: " nbns]

     [:div.bg-blue-300 "ws connected: " (pr-str @connected-a)]
 ;  [:div.bg-blue-300 "clear: " (pr-str @nb-clear-a)]

     [:p "add code here..."]]))

