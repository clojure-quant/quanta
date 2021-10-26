(ns demo.playground.cljplot
  (:require
   ;[clojure.string :as str]
  ;[java-time :as dt]

   [cljplot.core :refer :all]
   [cljplot.render :as r]
   [cljplot.build :as b]
   [cljplot.common :refer :all]
   ;[cljplot.scale :as s]

   ;[clojure2d.color :as c]
   ;[clojure2d.core :as c2d]
   ;[clojure2d.pixels :as p]

   ;[fastmath.core :as m]
   ;[fastmath.random :as rnd]
   ;[fastmath.complex :as cx]
   ;[fastmath.fields :as f]
   ;[fastmath.vector :as v]
   ;[fastmath.optimization :as opt]
   ;[fastmath.gp :as gp]
   ;[fastmath.distance :as dist]
   ;[fastmath.kernel :as k]
   ;[fastmath.interpolation :as in]
   ;[fastmath.stats :as stats]
   ))

(defn vega-clj [data]
  (-> (b/series
       [:grid nil {:x nil}]
       [:stack-vertical [:bar data {:padding-out 0.1}]])
      (b/preprocess-series)
      (b/update-scale :x :fmt name)
      (b/add-axes :bottom)
      (b/add-axes :left)
      (b/add-label :bottom "a")
      (b/add-label :left "b")
      (r/render-lattice {:width 400 :height 400})
      ;(save "bar.jpg")
      ;(show)
      :buffer))

(comment
  (vega-clj {:A 28 :B 55 :C 43 :D 91 :E 81 :F 53 :G 19 :H 87 :I 52})

  (vega-clj (sorted-map :A 28 :B 55 :C 43 :D 91 :E 81 :F 53 :G 19 :H 87 :I 52))
  ;
  )

