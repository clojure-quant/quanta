(ns ta.viz.page.publish
  (:require
   [reagent.core :as r]
   [options.edit :as edit]
   [ta.viz.lib.loader :refer [clj->p]]
   [ta.viz.lib.ui :refer [link-href]]
   [ta.viz.renderfn :refer [render render-spec]]))

(defn keyword->spec [kw]
  {:id  kw
   :name (str kw)})

(defn keywords->spec [kws]
  (map keyword->spec kws))

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
            [render-spec (:data @render-spec-a)]])])

(defn topic-view [topic-kw]
  (let [render-spec-a (clj->p 'ta.viz.publish/get-topic topic-kw)]
    [show-topic topic-kw render-spec-a]))

(defn header [topics-a topic-a]
  (let [topics (or (:data @topics-a) [])
        topics (into [] topics)
        ;topics [:a :b :c]
        ;topic-a (r/atom :b)
        ]
    [:div.flex.flex-row.h-42.w-full.bg-blue-300
     [link-href "/" "main"]
     [:h1.text-bold.bg-green-500.p-2.m-2 "topics"]
   ;[:p "keys: " (pr-str topics) " count: " (count topics) " topic: " @topic-a]                  
     [edit/select
      {:set-fn (fn [topic]
                 (println "topic selected: " topic)
                 (reset! topic-a topic))
       :options {:spec (keywords->spec topics)
                 :class "bg-green-500"}}
      (:data @topic-a)]]))

(defn data [topic-a]
  (if-let [topic @topic-a]
    [topic-view topic]
    [:p.p-5.bg-red-500 "please select a topic you want to see!"]))

(defn publish-view [_rote]
  (let [topic-a (r/atom nil)
        topics-a (clj->p 'ta.viz.publish/topic-keys)]
    (fn []
      (println "topics-a: " @topics-a)
      [:div.h-screen.w-screen.bg-red-500
       (println "current status: " (:status @topics-a))
       (case (:status @topics-a)
         :loading [:p "loading"]
         :error [:p "error!"]
         :data [:div.flex.flex-col.h-full.w-full
                [header topics-a topic-a]
                [data topic-a]]
         [:p "unknown: status:" (pr-str @topics-a #_(:status @topics))])])))

(defn publish-page [_route]
  [publish-view])
