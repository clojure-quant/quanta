(ns demo.playground.cljplot
  (:require
   [cljplot.core :as cljplotl]
   [cljplot.render :as r]
   [cljplot.build :as b]
   [cljplot.common :as cpc]
   [ta.notebook.repl :refer [save show clear url]]
   [ta.notebook.persist :as p]))

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

;; generate plot-image, and show it in browser

(clear)

(show
 (text {:class "text-xl text-blue-500 text-bold"}
       "Clojure Rocks!\nBabashka reashes!!"))

(show
  (img {:box :lg}
       (-> {:A 10 :B 55 :C 43 :D 91 :E 81 :F 53 :G 19 :H 87 :I 52}
           vega-clj)))


(show
 (:div
  (text {:class "text-xl text-red-500 text-bold"}
        "I wos born with grosse ohrn!")
  (img {:box :lg}
       (-> {:A 10 :B 55 :C 43 :D 91 :E 81 :F 53 :G 19 :H 87 :I 52}
           vega-clj))))

(url "1.png")

(comment

; save plot 
  (-> {:A 28 :B 55 :C 43 :D 91 :E 81 :F 53 :G 19 :H 87 :I 52}
      vega-clj
      (save "item-plot" :png))

 ; show url
  (url "item-plot.png")

  ; test - can we determine the format?
  (p/filename->extension "item-plot.png")
  (p/filename->format "item-plot.png")

  ; vega-clj works with sorted map too
  (def data2 (sorted-map :A 28 :B 55 :C 43 :D 91 :E 81 :F 53 :G 19 :H 87 :I 52))
  (vega-clj data2)

;
  )

