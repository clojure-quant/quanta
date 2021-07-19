

(def d [1 3 5 7 9 5 4 6 9 8 3 5 6])

(def hdata
  (into [] (repeatedly 1000 #(rand 10))))

;(def-ui hist
;(compose
  ;
  ;(plot (constantly 0.1) [0 10])
 ; )

;
#_(compose
   (list-plot (map #(vector % (rand %)) (range 0 10 0.01)) :opacity 0.3 :symbol-size 50)
   (plot (fn [x] (* x (Math/pow (Math/sin x) 2))) [0 10]))

(def list-plot
  [:div
   [:h1 "Vega charts"]
   [:h4 "generated via gorilla-plot dsl"]
   [:div.flex.flex-row.content-between
    [:div.flex.flex-col.justify-start
     [plot/list-plot d :joined true
      :plot-size 400
      :color "red"
      :aspect-ratio 1.6
      :plot-range [:all :all]
      :opacity 0.5]]]])

(defn histogram-page [h]
  [:div
   [:h1 "list plot"]
   list-plot
   [plot/list-plot d :joined true
    :plot-size 400
    :color "red"
    :aspect-ratio 1.6
    :plot-range [:all :all]
    :opacity 0.5]])

(add-page histogram-page :test/histogram)

#_[:p/composeplot
   [:p/listplot d]
   [:p/listplot {:joined true
                 :color "blue"
                 :plot-range [1 5]} d]]
#_[:p/histogram {:color "steelblue"
                 :bins 100
                 :normalize :probability-density} hdata]
#_[:p/barchart (range (count d)) d]
#_[:p/plot {:color "orange"
            :plot-points 50}
   (fn [x] (sin x)) [0 10]]