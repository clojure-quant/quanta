(ns ta.interact.page.live
  (:require
   [reagent.core :as r]
   [re-frame.core :as rf]
   [input]
   [ta.viz.lib.loader :refer [clj->p]]
   [ta.viz.lib.ui :refer [link-href]]
   [ta.viz.renderfn :refer [render render-spec]]
   [ta.interact.view.live-result :refer [viz-result-view]]))


(defn topic-subscriber [topic-kw]
  (if topic-kw
    (let [render-spec-a (clj->p 'ta.interact.subscription/subscribe-live topic-kw)]
      (reset! current-sub topic-kw)
      [:p "subscribed to: " topic-kw])
    [:p "not subscribed!"]))

(defn topic-selector [topics-a topic-a]
  (case (:status @topics-a)
    :loading [:p "loading"]
    :error [:p "error!"]
    :data [input/select
           {:nav? false
            :items (:data @topics-a)}
           topic-a [:topic]]
    [:p "unknown: status:" (pr-str @topics-a)]))

(defn header [topics-a topic-a]
  [:div.flex.flex-row.h-42.w-full.bg-blue-300
   [link-href "/" "main"]
   [:h1.text-bold.bg-green-500.p-2.m-2 "topics"]
   [topic-selector topics-a topic-a]
   [topic-subscriber (:topic @topic-a)]])

(defn live-view [_route]
  (let [topic-a (r/atom {:topic nil})
        topics-a (clj->p 'ta.interact.template/available-templates)]
    (fn [_route]
      [:div.h-screen.w-screen.bg-red-500
       [:div.flex.flex-col.h-full.w-full
        [header topics-a topic-a]
        [viz-result-view]
        ]])))

(defn live-page [_route]
  [live-view])
