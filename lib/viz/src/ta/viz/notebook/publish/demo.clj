(ns ta.viz.notebook.publish.demo
  (:require
   [ta.viz.publish :as p]))

; 1. publish demo

(p/publish nil {:topic :demo}
           {:render-fn 'ta.viz.renderfn.demo/demo
            :data [1 2 3]
            :spec {:x 3}})