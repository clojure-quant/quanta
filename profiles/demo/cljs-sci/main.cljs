
; main page 


(defmethod reagent-page :user/main [{:keys [route-params query-params handler] :as route}]
  [:div
   [:h1.text-xl.text-red-600 "trateg demo app"]
   [link/href "/goldly/about" "goldly developer tools"]
   [link/href "/error" "error"]
   [link/href "/chart" "chart"]
   [link/href "/experiment" "experiment"]])