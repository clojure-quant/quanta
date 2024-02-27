(ns ta.viz.publish
  (:require
   [taoensso.timbre :refer [info warn error]]
   [tablecloth.api :as tc]
   [ta.viz.ds.highchart :refer [highstock-render-spec]]
   [ta.viz.ds.rtable :refer [rtable-render-spec]]))

(defonce topics (atom {}))

(defn topic-keys []
  (keys @topics))

(defn get-topic [k]
  (get @topics k))

(defn publish [env spec render-spec]
  (let [topic (:topic spec)]
    (assert topic "publish needs to have :topic spec")
    (info "publishing topic: " topic)
    (swap! topics assoc topic render-spec)
    render-spec))

(defn publish-table [env spec ds]
  (let [cols (:columns spec)]
    (assert cols "publish-dataset needs to have :columns spec")
    (publish env spec (rtable-render-spec env spec ds))))

(defn publish-highchart [env spec ds]
  (let [cols (:charts spec)]
    (assert cols "publish-dataset needs to have :charts spec")
    (publish env spec (highstock-render-spec env spec ds))))


(comment
  (publish nil {:topic :test-vector} {:fun 'ui.vector/basdf
                                      :data [1 2 3]
                                      :spec {:x 3}})

  (publish-table nil {:columns [:date :close]
                      :topic :test-ds}
                 (tc/dataset [{:date :yesterday :close 100.0}
                              {:date :today :close 101.0}
                              {:date :tomorrow :close 103.0}]))
  (topic-keys)

  (get-topic :test-vector)

  (get-topic :test-ds)

  (get-topic [:juan :daily-history])

 ; 
  )
