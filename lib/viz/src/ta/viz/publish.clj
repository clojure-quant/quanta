(ns ta.viz.publish
  (:require
   [taoensso.timbre :refer [info warn error]]
   [tablecloth.api :as tc]))

(defonce topics (atom {}))

(defn topic-keys []
  (keys @topics))

(defn get-topic [k]
  (get @topics k))

(defn publish [env spec data ui]
  (let [data-ui {:data data :ui ui :spec spec}
        topic (:topic spec)]
    (assert topic "publish needs to have :topic spec")
    (info "publishing topic: " topic)
    (swap! topics assoc topic data-ui)
    data))

(defn publish-dataset [env spec ds]
  (let [cols (:columns spec)]
    (assert cols "publish-dataset needs to have :columns spec")
    (publish env spec
             (tc/select-columns ds cols)
             'ta.web.ui/dataset)
    ds))

(comment
  (publish nil {:topic :test-vector}
           [1 2 3]
           'ui.vector)

  (publish-dataset nil {:columns [:date :close]
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
