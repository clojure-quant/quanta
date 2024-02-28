(ns ta.viz.renderfn.highcharts
  (:require
   [ui.highcharts]))

(defn add-data-to-spec
  "render-spec contains :spec and :data separately. 
   this function merges both. This really should be done in cljs, 
   but it is handy to have it here for testing."
  [render-spec]
  (let [{:keys [data spec]} render-spec
        series (:series spec)
        series (map (fn [series d]
                      (assoc series :data d)) series data)
        series (into [] series)]
    (assoc spec :series series)))


(defn highstock [spec data]
  ; highcharts has data inside the spec; we need to merge it
  ; into the spec
  (let [opts (add-data-to-spec {:spec spec :data data})]
    (with-meta
      ;[:div (pr-str opts)]
      [ui.highcharts/highstock {:data opts :box :md}]
      {:R true})))

