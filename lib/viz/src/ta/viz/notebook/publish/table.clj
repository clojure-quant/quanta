(ns ta.viz.notebook.publish.table
  (:require
   [tablecloth.api :as tc]
   [ta.viz.publish :as p]))

; publish dataset as table

(def data-ds
  (tc/dataset [{:date :yesterday :open 100.0 :high 100.0 :low 100.0 :close 100.0 :volume 100.0}
               {:date :today :open 100.0 :high 100.0 :low 100.0 :close 101.0 :volume 100.0}
               {:date :tomorrow :open 100.0 :high 100.0 :low 100.0 :close 103.0 :volume 100.0}]))
 
(def table-spec
  {:topic :test-ds
   :class "table-head-fixed padding-sm table-red table-striped table-hover"
   :style {:width "50vw"
           :height "40vh"
           :border "3px solid green"}
   :columns [{:path :date}
             {:path :close}]})
 
(p/publish-table nil table-spec data-ds)