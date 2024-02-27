(ns ta.viz.notebook.publish
  (:require
   [tablecloth.api :as tc]
   [ta.viz.publish :as p]))

; 1. publish demo

 (p/publish nil {:topic :demo} 
            {:fun 'ta.viz.renderfn.demo
             :data [1 2 3]
             :spec {:x 3}})
 
; 2. publish dataset

(def data-ds
  (tc/dataset [{:date :yesterday :close 100.0}
               {:date :today :close 101.0}
               {:date :tomorrow :close 103.0}]))
(def table-spec
  {:topic :test-ds
   :columns [{:path :date}
             {:path :close}]
   })

(p/publish-table nil table-spec data-ds)


; 3. inspect published data.

  (p/topic-keys)

  (p/get-topic :demo)

  (p/get-topic :test-ds)

  (p/get-topic [:juan :daily-history])

 