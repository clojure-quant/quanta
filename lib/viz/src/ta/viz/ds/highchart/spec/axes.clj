(ns ta.viz.ds.highchart.spec.axes
  (:require
   [ta.viz.chart-spec :refer [axes-count]]))

;; AXES SPEC

(def axes-default
  {:resize {:enabled true}
   :lineWidth 2
   :labels {:align "right" :x -3}})

(defn ohlc-axis [ohlc-height]
  (assoc axes-default
         :height ohlc-height ; "60%"      
         :title {:text "OHLC"}))

(defn other-axis [ohlc-height other-height axis-idx]
  (assoc axes-default
  ;:title {:text "Volume"}
   ;:top "65%"
         :top (+ ohlc-height (* axis-idx other-height)) ; first additional axes starts at no = 0
         :height other-height ; "35%"
   ;:offset 0
         ))

(defn y-axis [chart panes]
  (let [nr (axes-count panes)
        ohlc-height (:ohlc-height chart)
        other-height (:other-height chart)]
    (into []
          (-> (map #(other-axis ohlc-height other-height %) (range nr))
              (conj (ohlc-axis ohlc-height))))))
