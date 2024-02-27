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

(defn publish-table 
  "publishes a dataset, the columns that will be displayed, 
   and its formatting depend on the spec."
  [env spec ds]
  (let [cols (:columns spec)]
    (assert cols "publish-dataset needs to have :columns spec")
    (publish env spec (rtable-render-spec env spec ds))))

(defn publish-highchart [env spec ds]
  (let [cols (:charts spec)]
    (assert cols "publish-dataset needs to have :charts spec")
    (publish env spec (highstock-render-spec env spec ds))))


