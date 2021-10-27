

(def connected-a
  (rf/subscribe [:ws/connected?]))


(defn ws-status []
    (fn []
      [:span.bg-blue-300 "ws connected: " (pr-str @connected-a)]
    ))