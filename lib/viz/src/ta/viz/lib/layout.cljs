(ns ta.viz.lib.layout
  (:require
   [spaces.core]))

(defn left-right-top [{:keys [top left right]}]
  [spaces.core/viewport
   [spaces.core/top-resizeable {:size 50} top]
   [spaces.core/fill
    [spaces.core/left-resizeable {:size "50%" :scrollable false} left]
    [spaces.core/fill {:scrollable false} right]]])

(defn main-top [{:keys [top main]}]
  [spaces.core/viewport
   [spaces.core/top-resizeable {:size 50} top]
   [spaces.core/fill
    main]])