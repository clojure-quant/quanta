(ns demo.studies.helper.techml
  (:require
   [picasso.protocols :refer [Renderable render make]]))


(defn text-render
  [o]
  (make :pinkie ; :reagent ; :hiccup
        [:span {:class "bg-blue-200"}
         [:p/text
          (pr-str o)]]))

(extend-type tech.v3.dataset.impl.column.Column
  Renderable
  (render [self]
    (text-render self)))


(extend-type tech.v3.dataset.impl.dataset.Dataset
  Renderable
  (render [self]
    (text-render self)))