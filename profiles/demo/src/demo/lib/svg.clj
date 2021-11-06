(ns demo.lib.svg
  (:require [svg-clj.utils :as utils]
            [svg-clj.elements :as el]
            [svg-clj.transforms :as tf]
            [svg-clj.composites :as comp :refer [svg]]
            [svg-clj.path :as path]
            [svg-clj.parametric :as p]
            [svg-clj.layout :as lo]
            [svg-clj.tools :as tools]
            [ta.data.date :refer [parse-date]]
            [goldly.scratchpad :refer [show! show-as clear!]]
            [wadogo.scale :refer [scale]]))


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

(defn series [{:keys [color]
               :or {color "blue"}
               :as opts} series]
  (map #(point opts %) series))

(comment
  (show!
   (svg! 500 500
         #_[:path {:d "M 50 50 H 290 V 90 H 50 L 50 50"
                   :stroke-width 5
                   :stroke "white"
                   :fill "none"}]
       ;[:path {:d "M 200 200" :stroke "green" :stroke-width 5}]
       ;[:circle {:cx "0" :cy "0" :r 150 :fill "red"}]
         (point {:color "red"} [400 400])
         (point {:color "blue"}  [300 300])
         (line {:color "green"} [200 200] [500 700])
         (series {:color "black"} [[10 10] [20 40] [120 60] [220 200] [320 100] [420 400]])))

;
  )



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

(defn add-point [{:keys [px-scale time-scale] :as plot-ctx}
                 [{:keys [color]
                   :or {color "blue"}}
                  [dt px]]]
  [:circle {:cx (time-scale dt) :cy (px-scale px) :r 5 :fill color}])

(defn add-line [{:keys [px-scale time-scale] :as plot-ctx}
                [opts [dt-a px-a] [dt-b px-b]]]
  (println "add line for: " dt-a dt-b)
  (line
   opts
   [(time-scale dt-a) (px-scale px-a)]
   [(time-scale dt-b) (px-scale px-b)]))

(defn add-series [{:keys [px-scale time-scale] :as plot-ctx}
                  [opts series-vec]]
  (let [scale-point (fn [[dt px]]
                      [(time-scale dt) (px-scale px)])]
    (series
     opts
     (map scale-point series-vec))))

(defn plot-item [ctx spec]
  (let [[kw & args] spec]
    (println "processing: " kw)
    (case kw
      :point (add-point ctx args)
      :line (add-line ctx args)
      :series (add-series ctx args))))



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



(comment


  (show!
   (svg-view {:min-px 500
              :max-px 1000
              :min-dt (parse-date "2021-01-01")
              :max-dt (parse-date "2021-12-31")
              :svg-width 500
              :svg-height 500}
             [[:point {:color "red"}   [(parse-date "2021-04-01") 600]]
              [:point {:color "blue"}  [(parse-date "2021-05-01") 700]]
              [:point {:color "green"} [(parse-date "2021-09-01") 900]]
              [:point {:color "blue"}  [(parse-date "2021-03-01") 600]]
              [:line {:color "green"}
               [(parse-date "2021-03-01") 500]
               [(parse-date "2021-12-01") 900]]
              [:series {:color "black"}
               [[(parse-date "2021-01-01") 600]
                [(parse-date "2021-02-01") 500]
                [(parse-date "2021-03-01") 400]
                [(parse-date "2021-04-01") 300]
                [(parse-date "2021-05-01") 600]
                [(parse-date "2021-06-01") 700]
                [(parse-date "2021-07-01") 800]
                [(parse-date "2021-08-01") 900]
                [(parse-date "2021-09-01") 700]
                [(parse-date "2021-10-01") 600]
                [(parse-date "2021-11-01") 500]
                [(parse-date "2021-12-01") 600]]]]))
  


;  
  )




