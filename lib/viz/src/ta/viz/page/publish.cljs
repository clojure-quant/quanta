(ns ta.viz.page.publish
  (:require
   [reagent.core :as r]
   [input]
   [ta.viz.lib.loader :refer [clj->p]]
   [ta.viz.lib.ui :refer [link-href]]
   [ta.viz.renderfn :refer [render]]))

(defn topic-view [topic-kw]
  (let [render-spec-a (clj->p 'ta.viz.publish/get-topic topic-kw)]
    (fn [topic-kw]
      [:div
         (case (:status @render-spec-a)
          :loading [:p "loading"]
          :error [:p "error!"]
          :data [:div 
                  [render (:data @render-spec-a)]])])))

(defn publish-topic-view []
  (let [topic (r/atom {:topic nil})
        topics-a (clj->p 'ta.viz.publish/topic-keys)]
    (fn []
      [:div
        [:h1.text-bold.bg-green-500 "topics"]
        (case (:status @topics-a)
          :loading [:p "loading"]
          :error [:p "error!"]
          :data [:div 
                  [input/select
                    {:nav? false
                     :items (:topic @topic)}
                     topic [:topic]]
                  (when (:topic @topic) 
                    [topic-view (:data @topics-a)])]
          [:p "unknown: status:" (pr-str @topics-a #_(:status @topics))])])))

(defn publish-page [_route]
  [:div.h-screen.w-screen.bg-red-500
    [:div.flex.flex-col.h-full.w-full
      ;[:div.flex.flex-row.bg-blue-500
        [link-href "/" "main"] 
        [publish-topic-view]
   ;    ]
   ]])

