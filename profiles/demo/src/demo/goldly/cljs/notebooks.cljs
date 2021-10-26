(defn link-href [href text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])

(def test-data
  [{:nav 100.0 :index 1}
   {:nav 120.0 :index 2}
   {:nav 150.0 :index 3}])

(defonce viewer-state
  (atom {:notebooks []}))

(defn get-notebooks-once []
  (when (empty? (get-in @viewer-state [:notebooks :data]))
    (get-edn "/api/notebook/ns" viewer-state [:notebooks])))

(defn notebook-list [notebook-ns-list]
  [:table
   (for [n notebook-ns-list]
     [:tr
      [:a {:href (str "/viewer/notebook/" n)}
       [:td n]]])])

(defmethod reagent-page :viewer/notebooks [{:keys [route-params query-params handler] :as route}]
  (get-notebooks-once)
  [:div
   [link-href "/" "main"]
   [:div.text-green-300 "notebooks..."]
   [:p "there are nr notebooks: " (count (get-in @viewer-state [:notebooks :data]))]
   [:p.text-red-300 (pr-str @viewer-state)]
   [notebook-list (get-in @viewer-state [:notebooks :data])]
   [:p "add code here..."]])

(defonce document-state
  (atom {:resources [["1" :edn]]
         :form ["line-plot" ["1" :edn]]
         :ns-nb "xxx" ;#namespace[ta.notebook.repl]
         }))

;:resources [["1" :edn]]

(def connected-a
  (rf/subscribe [:ws/connected?]))

site/message-button

(rf/reg-event-db
 :bongo
 (fn [db _]
   db))

(rf/reg-event-fx
 :viewer/update
 (fn [{:keys [db]} [_ data]]
   (println "YESS --- viewer update received: " data)
   nil))

(defn print-status [x]
  (println "status: " x))
(rf/dispatch [:ws/send [:ws/status []] print-status 5000])

;(def nb-clear-a
;  (rf/subscribe [:notebook/clear]))

(defmethod reagent-page :viewer/scratchpad [{:keys [route-params query-params handler] :as route}]
  (let [{:keys [nbns]} route-params]
    [:div
   ;[:p "route-params: " (pr-str route-params)]
     [link-href "/" "main"]
     [:div.text-blue-700.text-xl "notebook scratchpad "]
     [:div.bg-blue-300 "ws connected: " (pr-str @connected-a)]
 ;  [:div.bg-blue-300 "clear: " (pr-str @nb-clear-a)]
     [:p "add code here..."]]))

(defmethod reagent-page :viewer/notebook [{:keys [route-params query-params handler] :as route}]
  (let [{:keys [nbns]} route-params]
    [:div
   ;[:p "route-params: " (pr-str route-params)]
     [link-href "/" "main"]
     [:div.text-blue-700.text-xl "notebook viewer: " nbns]
     [:div.bg-red-100 (pr-str @document-state)]
     [:div.bg-blue-300 "ws connected: " (pr-str @connected-a)]
 ;  [:div.bg-blue-300 "clear: " (pr-str @nb-clear-a)]

     [:p "add code here..."]]))

; python: stop eval => python no longer works.