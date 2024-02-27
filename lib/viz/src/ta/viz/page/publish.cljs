(ns ta.viz.page.publish
  (:require
   [reagent.core :as r]
   [input]
   [ta.viz.lib.loader :refer [clj->p]]
   [ta.viz.lib.ui :refer [link-href]]
   [ta.viz.renderfn :refer [render render-spec]]))

(defn show-topic [topic-kw render-spec-a]
    [:div
     (case (:status @render-spec-a)
       :loading [:p "loading selected topic.."]
       :error [:p "error loading selected topic!"]
       :data [:div
              [:p "topic: " 
                  (pr-str topic-kw)
                  "  render-fn: "
                  (pr-str (get-in @render-spec-a [:data :render-fn]))]
              ;[:p "raw spec:"]
              ;[:p (pr-str (get-in @render-spec-a [:data :spec]))]
              ;[:p "raw data:"]
              ;[:p (pr-str (get-in @render-spec-a [:data :data]))]
              ;[:p "ui:"]
              [render-spec (:data @render-spec-a)]
              ])])

(defn topic-view [topic-kw]
  (let [render-spec-a (clj->p 'ta.viz.publish/get-topic topic-kw)]
    [show-topic topic-kw render-spec-a]))

(defn header [topics-a topic-a]
   [:div.flex.flex-row.h-42.w-full.bg-blue-300
   [link-href "/" "main"]
   [:h1.text-bold.bg-green-500.p-2.m-2 "topics"]
   ;[:p "keys: " (pr-str (:data @topics-a))]                  
   [input/select
    {:nav? false
     :items (:data @topics-a)}
      topic-a [:topic]]])

(defn data [topic-a]
  (if (:topic @topic-a)
    [topic-view (:topic @topic-a)]
    [:p.p-5.bg-red-500 "please select a topic you want to see!"])
  )

(defn publish-view [_route]
  (let [topic-a (r/atom {:topic nil})
        topics-a (clj->p 'ta.viz.publish/topic-keys)]
    (fn []
      [:div.h-screen.w-screen.bg-red-500
        (case (:status @topics-a)
          :loading [:p "loading"]
          :error [:p "error!"]
          :data [:div.flex.flex-col.h-full.w-full
                  [header topics-a topic-a]
                  [data topic-a]]
          [:p "unknown: status:" (pr-str @topics-a #_(:status @topics))])])))

(defn publish-page [_route]
  [publish-view])
