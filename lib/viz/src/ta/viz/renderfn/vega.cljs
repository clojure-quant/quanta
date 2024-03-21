(ns ta.viz.renderfn.vega
  (:require
   [ui.vega]))

(defn vega-lite [spec data]
  (let [opts (assoc spec
                    :data data
                    :box (or (:box spec) :md))]
    (with-meta
      (if (empty? data)
        [:div.h-full.w-full.p-10 "No data in this chart."]
        [ui.vega/vegalite opts])
      {:R true})))

