(ns ta.viz.renderfn.demo)

(defn demo [spec data]
  (with-meta
    [:div {:class "bg-blue-500"}
     [:h1.bg-red-500 "demo render fn: "]
     [:table
      [:tr
       [:td.font-bold.p-5 "spec"]
       [:td.p-5 (pr-str spec)]]
      [:tr
       [:td.font-bold.p-5 "data"]
       [:td.p-5 (pr-str data)]]]]
    {:R true}))
