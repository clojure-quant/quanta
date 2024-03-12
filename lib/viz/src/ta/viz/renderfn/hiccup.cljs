(ns ta.viz.renderfn.hiccup)

(defn hiccup [spec _data]
  (with-meta
    [:div {:class "w-full.h-full"}
     spec]
    {:R true}))
