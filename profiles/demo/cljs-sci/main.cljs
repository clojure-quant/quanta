
; main page 
(defn link-href [href text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])

(defmethod reagent-page :user/main [{:keys [route-params query-params handler] :as route}]
  [:div
   [:h1.text-xl.text-red-600 "trateg demo app"]
   [link-href "/goldly/about" "goldly developer tools"]
   [link-href "/chart" "chart"]
   [link-href "/experiment" "experiment"]])