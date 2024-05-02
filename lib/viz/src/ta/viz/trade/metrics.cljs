(ns ta.viz.trade.metrics)

(defn to-fixed [n d]
  (.toFixed n d))

(defn round-number-digits
  [digits number] ; digits is first parameter, so it can easily be applied (data last)
  (try
    (if (nil? number) "" (to-fixed number digits))
    (catch js/Error e
      (println "round-numnber-digits exception: " e)
      number)))

(defn metrics-view [{:keys [class style roundtrip nav]
                     :or {class "w-full h-full"
                          style {}}}]
  [:div {:class class :style style}
   [:h1.bg-blue-300.text-xl "performance-metrics"]
   [:table
    [:tr
     [:td "cum-pl"]
     [:td (:cum-pl (round-number-digits 0 nav))]]
    [:tr
     [:td "max-dd"]
     [:td (:max-dd (round-number-digits 0 nav))]]
    [:tr
     [:td "# trades"]
     [:td (:trades roundtrip)]]]
   [:table
    [:tr
     [:td {:style {:width "3cm"}} " "]
     [:td "win"]
     [:td "loss"]]
    [:tr
     [:td "%winner"]
     [:td (round-number-digits 0 (:win-nr-prct roundtrip))]
     [:td ""]]
    [:tr
     [:td "avg pl"]
     [:td (round-number-digits 4 (:avg-win-log roundtrip))]
     [:td (round-number-digits 4 (:avg-loss-log roundtrip))]]
    [:tr
     [:td "avg bars"]
     [:td (round-number-digits 1 (:avg-bars-win roundtrip))]
     [:td (round-number-digits 1 (:avg-bars-loss roundtrip))]]]])