


(defn viewer-page [{:keys [route-params query-params handler] :as route}]
  [:div.bg-green-300.w-screen.h-screen
   [link-href "/" "main"]
   [ws-status]
   [viewer-app]])

(add-page viewer-page :viewer)
