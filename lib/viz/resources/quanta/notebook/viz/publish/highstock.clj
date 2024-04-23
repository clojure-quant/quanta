(ns quanta.notebook.viz.publish.highstock
  (:require
   [tick.core :as t]
   [tablecloth.api :as tc]
   [ta.viz.publish :as p]))

(def ds
  (tc/dataset [{:date (t/date-time) :open 1 :high 2 :low 3 :close 4 :volume 5}
               {:date (t/date-time) :open 1 :high 2 :low 3 :close 4 :volume 5}
               {:date (t/date-time) :open 1 :high 2 :low 3 :close 4 :volume 5}
               {:date (t/date-time) :open 1 :high 2 :low 3 :close 4 :volume 5}]))

ds

(def spec {:topic :demo-highstock
           :charts  [{:open "line"
                      :low "line"
                          ;:close :flags
                      }
                     {:volume "column"}]})

ds

(p/publish-ds->highstock nil spec ds)