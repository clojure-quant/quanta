(ns ta.viz.trade.metrics
   (:require
    [goldly.js :refer [to-fixed]]))

(defn round-number-digits
  [digits number] ; digits is first parameter, so it can easily be applied (data last)
  (if (nil? number) "" (to-fixed number digits)))

(defn metrics-view [{:keys [roundtrip nav]}]
  [:div
   [:h1.bg-blue-300.text-xl "performance-metrics"]
   [:table
    [:tr
     [:td "cum-pl"]
     [:td (:cum-pl nav)]]
    [:tr
     [:td "max-dd"]
     [:td (:max-dd nav)]]
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