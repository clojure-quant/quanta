
;; heatmap

(defn heatmap-page [{:keys [route-params query-params handler] :as route}]
  [:div
   [link-href "/" "main"]
   [:div.text-green-300 "vega..."]

   [vegalite {:box :sm
              :spec vega-spec-heatmap-url}]
   ;[:div "spec: " (pr-str {:spec s2})]
   [:div "cor matrix: " (pr-str demo-data-heatmap-cor-matrix)]
   [heatmap-corr-matrix demo-data-heatmap-cor-matrix]])

(add-page heatmap-page :heatmap)

;; histogram

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

(add-page histogram-page :histogram)

;; vega misc

#_(defn link-href [href text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])

(defn vega-page [{:keys [route-params query-params handler] :as route}]
  [:div
   [link-href "/" "main"]
   [:div.text-green-300 "experiments..."]
   [:p "add code here..."]
   [vega-nav-plot vega-nav-plot-test-data]
   [:p "end of vega nav plot"]])

(add-page vega-page :vegatest)