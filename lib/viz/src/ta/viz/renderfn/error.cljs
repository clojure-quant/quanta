(ns ta.viz.renderfn.error)

(defn nom-error [spec data]
  (with-meta
    [:div {:class "bg-blue-500"}
     [:h1.bg-red-500 "error!"]
     [:p 
      (pr-str spec)
      (pr-str data)]]
    {:R true}))
