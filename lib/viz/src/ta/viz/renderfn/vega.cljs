(ns ta.viz.renderfn.vega
  (:require
   [ui.vega]))

(defn vega-lite [spec data]
  ; rtable needs 3 parameters: opts, cols, data
  ; our spec format only uses two parameters; we moved the 
  ; columns definition to a :columns key in opts
  (let [data {:values data}
        opts {:box (or (:box spec) :md)
              :spec (assoc spec :data data)}]
    (with-meta
      (if (empty? data)
        [:div.h-full.w-full.p-10 "No data in this chart."]
        [ui.vega/vegalite opts])
      {:R true})))

