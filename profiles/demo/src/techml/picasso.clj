(ns techml.picasso
  (:require
   [picasso.protocols :refer [Renderable render make]]))

(defn text-render
  [o comment]
  (make :pinkie ; :reagent ; :hiccup
        [:span {:class "bg-blue-500"}
         comment
         [:p/text
          (pr-str o)]]))

(extend-type tech.v3.dataset.impl.column.Column
  Renderable
  (render [self]
    (text-render self "techml column! ")))

(extend-type tech.v3.dataset.impl.dataset.Dataset
  Renderable
  (render [self]
    (text-render self "techml yippie! ")))