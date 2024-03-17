(ns ta.viz.trade.roundtrip-table2
   (:require
    [ui.aggrid :refer [aggrid]]
    [goldly.js :refer [to-fixed]]
    [ta.viz.trade-metrics.vega-nav :refer [vega-nav-plot]]))

(defn round-number-digits
  [digits number] ; digits is first parameter, so it can easily be applied (data last)
  (if (nil? number) "" (to-fixed number digits)))

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

(defn rt-flat? [{:keys [trade] :as _roundtrip}]
  ;(println "trade: " trade)
  (= trade :flat))

(defn remove-flat [roundtrips]
  ;(println "filtering flat rts: " roundtrips)
  (remove rt-flat? roundtrips))

(defn roundtrips-view [_context roundtrips]
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