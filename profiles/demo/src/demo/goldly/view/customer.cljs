


(defn customer [{:keys [name country]}]
  [:div.bg-red-500
   [:p "name: " name]
   [:p "country: " country]])

(pinkie/register-tag :p/customer customer)