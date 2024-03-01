(ns ta.viz.notebook.publish.api
  (:require
   [ta.viz.publish :as p]))

; inspect published data.

(p/topic-keys)

(p/get-topic :demo)

(->  (p/get-topic :test-ds)
     :data
     type)

(p/get-topic [:juan :daily-history])

