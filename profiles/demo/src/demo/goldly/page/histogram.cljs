

(def d [1 3 5 7 9 5 4 6 9 8 3 5 6])

(def hdata
  (into [] (repeatedly 1000 #(rand 10))))

(defn histogram-page [{:keys [route-params query-params handler] :as route}]
  [:div
   [:h1 "list plot"]
   (plot/list-plot d :joined true
                   :plot-size 400
                   :color "red"
                   :aspect-ratio 1.6
                   :plot-range [:all :all]
                   :opacity 0.5)
   ;[vega-list-plot d]
   ])

(add-page histogram-page :test/histogram)
