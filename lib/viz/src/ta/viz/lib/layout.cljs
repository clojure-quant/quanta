(ns ta.viz.lib.layout
  (:require
    [spaces]))

(defn left-right-top [{:keys [top left right]}]
  [spaces/viewport
   [spaces/top-resizeable {:size 50} top]
   [spaces/fill 
     [spaces/left-resizeable {:size "50%" :scrollable false} left]
     [spaces/fill {:scrollable false} right]]])

(defn main-top [{:keys [top main]}]
  [spaces/viewport
   [spaces/top-resizeable {:size 50} top]
   [spaces/fill
     main]])