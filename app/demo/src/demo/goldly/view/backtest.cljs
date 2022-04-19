

(defn digits [nr v]
  (when v
    (round-number-digits nr v)))

(defn metrics-view [context {:keys [rt-metrics nav-metrics options comment]}]
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
     [:td (digits 0 (:win-nr-prct rt-metrics))]
     [:td ""]]
    [:tr
     [:td "avg pl"]
     [:td (digits 4 (:avg-win-log rt-metrics))]
     [:td (digits 4 (:avg-loss-log rt-metrics))]]
    [:tr
     [:td "avg bars"]
     [:td (digits 1 (:avg-bars-win rt-metrics))]
     [:td (digits 1 (:avg-bars-loss rt-metrics))]]]])

(def roundtrip-cols
  [:rt-no
   ;:index-open
   :date-open
   {:field :price-open :format (partial round-number-digits 2)}
   ;:index-close
   :date-close
   {:field :price-close :format (partial round-number-digits 2)}
   :bars
   ;:trades
   :trade
   ;:position
   {:field :pl-log :format (partial round-number-digits 2)}
   :win])

(defn rt-flat? [{:keys [trade position] :as roundtrip}]
  ;(println "trade: " trade)
  (= trade :flat))

(defn remove-flat [roundtrips]
  ;(println "filtering flat rts: " roundtrips)
  (remove rt-flat? roundtrips))

(defn roundtrips-view [context roundtrips]
  (let [roundtrips (remove-flat roundtrips)]
    [:div.h-full.w-full.flex.flex-col
     [:h1 "roundtrips " (count roundtrips)]
     (when (> (count roundtrips) 0)
       [:div {:style {:width "100%" ;"40cm"
                      :height "100%" ; "70vh"
                      :background-color "blue"}}
        [aggrid {:data roundtrips
                 :columns roundtrip-cols
                 :box :fl
                 :pagination :true
                 :paginationAutoPageSize true}]])]))

(def nav-cols
  [:year
   :month
   {:field :nav :format (partial round-number-digits 2)}
   {:field :drawdown :format (partial round-number-digits 5)}
   {:field :cum-pl-t :format (partial round-number-digits 5)}
   {:field :pl-log-cum :format (partial round-number-digits 5)}
   :trades])

(defn navs-view [context navs]
  [:div.h-full.w-full.flex.flex-col
   [:h1 "navs " (count navs)]
   (when (> (count navs) 0)
     [:div {:style {:width "100%" ; "40cm"
                    :height "100%" ; "70vh"
                    :background-color "blue"}}
      [aggrid {:data navs
               :columns nav-cols
               :box :fl
               :pagination :true
               :paginationAutoPageSize true}]])])

(defn navs-chart [context navs]
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