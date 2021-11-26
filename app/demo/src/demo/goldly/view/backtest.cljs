

(defn metrics-view [{:keys [rt-metrics nav-metrics options comment]}]
  [:div
   [:h1.bg-blue-300.text-xl "comment:" comment]
   [:p "options:" (pr-str options)]
   [:table
    [:tr
     [:td "cum-pl"]
     [:td (:cum-pl nav-metrics)]]
    [:tr
     [:td "max-dd"]
     [:td (:max-dd nav-metrics)]]
    [:tr
     [:td "# trades"]
     [:td (:trades rt-metrics)]]]
   [:table
    [:tr
     [:td {:style {:width "3cm"}} " "]
     [:td "win"]
     [:td "loss"]]
    [:tr
     [:td "%winner"]
     [:td (:win-nr-prct rt-metrics)]
     [:td ""]]
    [:tr
     [:td "avg pl"]
     [:td (:avg-win-log rt-metrics)]
     [:td (:avg-loss-log rt-metrics)]]
    [:tr
     [:td "avg bars"]
     [:td (:avg-bars-win rt-metrics)]
     [:td (:avg-bars-loss rt-metrics)]]]])

(defn roundtrips-view [roundtrips]
  [:div
   [:h1 "roundtrips " (count roundtrips)]
   (when (> (count roundtrips) 0)
     [:div {:style {:width "40cm"
                    :height "70vh"
                    :background-color "blue"}}
      [aggrid {:data roundtrips
               :box :fl
               :pagination :true
               :paginationAutoPageSize true}]])])

(defn navs-view [navs]
  [:div
   [:h1 "navs " (count navs)]
   (when (> (count navs) 0)
     [:div {:style {:width "40cm"
                    :height "70vh"
                    :background-color "blue"}}
      [aggrid {:data navs
               :box :fl
               :pagination :true
               :paginationAutoPageSize true}]])])

(defn navs-chart [navs]
  (let [navs-with-index (map-indexed (fn [i v]
                                       {:index i
                                        :nav (:nav v)}) navs)
        navs-with-index (into [] navs-with-index)]
    [:div
     [:h1 "navs " (count navs-with-index)]
     (when (> (count navs) 0)
       [:div ; {:style {:width "50vw"}}
        ;(pr-str navs-with-index)
        [vega-nav-plot navs-with-index]])]))