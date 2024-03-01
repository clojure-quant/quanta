(ns ta.viz.notebook.rtable
  (:require
   [ta.viz.renderfn.rtable :refer [rtable]]))

(def spec
  {:class "table-head-fixed padding-sm table-red table-striped table-hover"
   :style {:width "50vw"
           :height "40vh"
           :border "3px solid green"}
   :columns [{:path :close
              :format 'ta.viz.lib.format/fmt-nodigits}
             {:path :sma
              ;:format (fn [v] (str "#" v))
              }]})

(def data
  [{:close 10.444 :sma 10}
   {:close 14 :sma 12}
   {:close 9 :sma 11}
   {:close 16 :sma 13}
   {:close 11 :sma 12}
   {:close 10 :sma 11}])

(rtable spec data)
