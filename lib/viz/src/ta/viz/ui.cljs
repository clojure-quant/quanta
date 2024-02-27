(ns ta.viz.ui)

(defn get-rfn [args])

(defn show-render-spec [{:keys [render-fn spec data]}]
  ((get-rfn render-fn) spec data))


(defn rtable [spec data]
   (let [opts (merge spec data)]
     ['ui.rtable/rtable-hacked opts]))
  

(defn highstock [spec data]
  (let [opts (merge spec data)]
    ['ui.highcharts/highstock opts]))



