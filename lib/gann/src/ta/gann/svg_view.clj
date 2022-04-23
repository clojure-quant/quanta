(ns ta.gann.svg-view
  (:require
   [svg-clj.utils :as utils]
   [svg-clj.elements :as el]
   [svg-clj.transforms :as tf]
   [svg-clj.composites :as comp :refer [svg]]
   [svg-clj.path :as path]
   [svg-clj.parametric :as p]
   [svg-clj.layout :as lo]
   [svg-clj.tools :as tools]
   [ta.helper.date :refer [parse-date]]
   [cljc.java-time.duration :as duration]
   [tick.core :as tick :refer [>>]]
   [tick.alpha.interval :as t.i]
   [wadogo.scale :refer [scale]]))

;; SVG is XML based, which means that every element is available within the SVG DOM. 
;; You can attach JavaScript event handlers for an element.

(defn svg! [width height & body]
  (into
   [:svg {:class "bg-blue-200"
          :width width
          :height height
          :xmlns "http://www.w3.org/2000/svg"}]
   body))

(defn point [{:keys [color]
              :or {color "blue"}} [x y]]
  [:circle {:cx x :cy y :r 5 :fill color}])

(defn line [{:keys [color]
             :or {color "blue"}} a b]
  (-> (el/line a b)
      (tf/style {:stroke color
                 :stroke-width "2px"
                 :fill "none"})))

(defn ellipse [{:keys [style rx ry cx cy]
                :or {cx 0
                     cy 0
                     style {}}
                :as opts}]
  (-> [:ellipse {:cx cx :cy cy :rx rx :ry ry}]
      (tf/style (merge {:stroke "green"
                        :stroke-width "2px"
                        :fill "none"} style))))

(defn series [{:keys [color]
               :or {color "blue"}
               :as opts} series]
  (map #(point opts %) series))

(comment
  ; scale test
  (let [min-px 500
        max-px 1000
        min-dt (parse-date "2021-01-01")
        max-dt (parse-date "2021-12-31")
        svg-width 500
        svg-height 500
        px-scale (scale :linear {:domain [min-px max-px] :range [svg-height 0]})
        time-scale (scale :datetime {:domain [min-dt max-dt] :range [0 svg-width]})]
   ;(px-scale 1000)
  ;(px-scale 400)
    (time-scale (parse-date "2021-07-01"))))

;; SCALING VERSIONS

(defn scale-point [{:keys [px-scale time-scale] :as plot-ctx}
                   [{:keys [color]
                     :or {color "blue"}}
                    [dt px]]]
  [:circle {:cx (time-scale dt) :cy (px-scale px) :r 5 :fill color}])

(defn scale-line [{:keys [px-scale time-scale] :as plot-ctx}
                  [opts [dt-a px-a] [dt-b px-b]]]
  ;(println "scale-line for: " dt-a dt-b)
  (line
   opts
   [(time-scale dt-a) (px-scale px-a)]
   [(time-scale dt-b) (px-scale px-b)]))

(defn scale-ellipse [{:keys [px-scale time-scale] :as plot-ctx}
                     [{:keys [rx ry cx cy] :as opts}]]
  (let [; price
        y* (px-scale (+ ry cy))
        cy (px-scale cy)
        ;ry 70
        ry (- cy y*) ; y is an inverse scale.
        ; time
        x* (time-scale (tick/>> cx rx))
        cx (time-scale cx)
        ;rx 70 ; (time-scale rx)
        rx (- x* cx) ; x is NOT an inverse scale
        ]
    ;(println "cy (scaled): " cy "y* (scaled): " y*  "ry (scaled): " ry)
    ;(println "cx (scaled): " cx "x* (scaled): " x*  "rx (scaled): " rx)
    (ellipse
     (merge opts {:rx rx :ry ry :cx cx :cy cy}))))

(defn scale-series [{:keys [px-scale time-scale] :as plot-ctx}
                    [opts series-vec]]
  (let [scale-point (fn [[dt px]]
                      [(time-scale dt) (px-scale px)])]
    (series
     opts
     (map scale-point series-vec))))

(defn plot-item [ctx spec]
  (let [[kw & args] spec]
    ;(println "processing: " kw)
    (case kw
      :point (scale-point ctx args)
      :line (scale-line ctx args)
      :ellipse (scale-ellipse ctx args)
      :series (scale-series ctx args))))

(defn svg-view [{:keys [svg-width svg-height min-px max-px min-dt max-dt]} plots]
  (let [px-scale (scale :linear {:domain [min-px max-px] :range [svg-height 0]})
        time-scale (scale :datetime {:domain [min-dt max-dt] :range [0 svg-width]})
        ctx {:px-scale px-scale
             :time-scale time-scale}]
    (into
     [:svg {:class "bg-blue-200"
            :width svg-width
            :height svg-height
            :xmlns "http://www.w3.org/2000/svg"}]
     (map #(plot-item ctx %) plots))))





