(ns ta.viz.ds.hiccup)

(defn hiccup-render-spec
  "returns a render specification {:render-fn :spec :data} . 
   for hiccup"
  [hiccup]
  ^{:render-fn 'ta.viz.renderfn/render-spec} ; needed for notebooks
  {:render-fn 'ta.viz.renderfn.hiccup/hiccup
   :data []
   :spec hiccup})