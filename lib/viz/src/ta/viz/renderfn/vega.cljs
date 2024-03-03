(ns ta.viz.renderfn.vega
  (:require
   [ui.vega]))

(defn vega-lite [spec data]
  ; rtable needs 3 parameters: opts, cols, data
  ; our spec format only uses two parameters; we moved the 
  ; columns definition to a :columns key in opts
  (let [data-values {:values data}
        opts (assoc spec 
                    :data data-values
                    :box (or (:box spec) :md))]
    (with-meta
      (if (empty? data)
        [:div.h-full.w-full.p-10 "No data in this chart."]
        [ui.vega/vegalite opts])
      {:R true})))

