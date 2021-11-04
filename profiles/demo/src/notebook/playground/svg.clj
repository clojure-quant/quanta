(ns notebook.playground.svg
  (:require [svg-clj.utils :as utils]
            [svg-clj.elements :as el]
            [svg-clj.transforms :as tf]
            [svg-clj.composites :as comp :refer [svg]]
            [svg-clj.path :as path]
            [svg-clj.parametric :as p]
            [svg-clj.layout :as lo]
            [svg-clj.tools :as tools]
            [goldly.scratchpad :refer [show! show-as clear!]]))



(show!
 [:svg {:class "bg-blue-200"
        :width 500
        :height 500
        :xmlns "http://www.w3.org/2000/svg"}
  ;[:path {:d "M 200 200" :stroke "green" :stroke-width 5}]
  [:path {:d "M 50 50 H 290 V 90 H 50 L 50 50"
          :stroke-width 5
          :stroke "white"
          :fill "none"}]
   ;[:circle {:cx "0" :cy "0" :r 150 :fill "red"}]
  ])

(def basic-group
  (el/g
   (el/rect 20 20)
   (-> (el/rect 20 20) (tf/translate [20 0]))
   (-> (el/rect 20 20) (tf/translate [0 20]))
   (-> (el/rect 20 20) (tf/translate [20 20]))))

basic-group

(-> (el/rect 20 20) (tf/translate [20 0]))


(defn line [a b]
  (-> (el/line a b)
      (tf/style {:stroke
                 (str "green")
                 :stroke-width "2px"
                 :fill "none"})))

(defn a-1-1 [w h]
  (line [0 h] [w 0]))

(defn a-2-1 [w h]
  (line [0 h] [(/ w 2) 0]))

(defn a-3-1 [w h]
  (line [0 h] [(int (/ w 3)) 0]))

(defn a-4-1 [w h]
  (line [0 h] [(/ w 4) 0]))

(defn b-1-1 [w h]
  (line [0 0] [w h]))

(defn b-2-1 [w h]
  (line [0 0] [(/ w 2) h]))

(defn b-3-1 [w h]
  (line [0 0] [(int (/ w 3)) h]))

(defn b-4-1 [w h]
  (line [0 0] [(/ w 4) h]))

(defn c-1-1 [w h]
  (line [w 0] [0 0]))

(defn c-2-1 [w h]
  (line [w 0] [(/ w 2) h]))

(defn c-3-1 [w h]
  (line [w 0] [(- w (int (/ w 3))) h]))

(defn c-4-1 [w h]
  (line [w 0] [(- w (/ w 4)) h]))

(defn d-1-1 [w h]
  (line [w h] [0 0]))

(defn d-2-1 [w h]
  (line [w h] [(/ w 2) 0]))

(defn d-3-1 [w h]
  (line [w h] [(- w (int (/ w 3))) 0]))

(defn d-4-1 [w h]
  (line [w h] [(- w (/ w 4)) 0]))


(defn circle [s]
  (-> (path/circle s)
      (tf/style {:stroke
                 (str "red")
                 :stroke-width "2px"
                 :fill "red"})))

(defn vola-a [gw gh]
  (circle 100))

(defn vola-b [gw gh]
  (-> (circle 100)
      (tf/translate [gw  0])))

(defn vola-c [gw gh]
  (-> (circle 100)
      (tf/translate [gw  gh])))

(defn vola-d [gw gh]
  (-> (circle 100)
      (tf/translate [0  gh])))



(def gw 600)
(def gh 600)


(defn gann-box [gw gh]
  [(a-1-1 gw gh)
   (a-2-1 gw gh)
   (a-3-1 gw gh)
   (a-4-1 gw gh)
   (b-1-1 gw gh)
   (b-2-1 gw gh)
   (b-3-1 gw gh)
   (b-4-1 gw gh)
   (c-1-1 gw gh)
   (c-2-1 gw gh)
   (c-3-1 gw gh)
   (c-4-1 gw gh)
   (d-1-1 gw gh)
   (d-2-1 gw gh)
   (d-3-1 gw gh)
   (d-4-1 gw gh)

   (vola-a gw gh)
   (vola-b gw gh)
   (vola-c gw gh)
   (vola-d gw gh)])


(show!
 (into [:svg  {:width (* 2 gw)
               :height (* 2 gh)}
        (-> (el/rect gw gh)
            (tf/translate [(/ gw 2) (/ gh 2)])
            (tf/style {:stroke
                       (str "orange")
                       :stroke-width "2px"
                  ;:fill "none"
                       }))]
       (concat
        (gann-box gw gh)
        (-> (gann-box gw gh)
            (tf/translate [gw 0]))
        (-> (gann-box gw gh)
            (tf/translate [0  gh]))
        (-> (gann-box gw gh)
            (tf/translate [gw gh]))
        
        )))


(show!
 (into [:svg  {:width gw
               :height gh}]))


(show!
 [:svg  {:width gw
         :height gh}
  (-> (el/rect 20 20) (tf/translate [100 0]))
  (-> (el/rect 20 20)
      (tf/translate [200 150])
      (tf/style {:stroke
                 (str "green")
                 :stroke-width "2px"
                 :fill "none"}))
  #_(-> (el/line [0 0]
                 [100 300])
        (tf/style {:stroke
                   (str "green")
                   :stroke-width "2px"
                   :fill "none"}))])




(defn draw-box [])


