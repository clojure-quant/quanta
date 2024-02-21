(ns notebook.datascience.window
  (:require
   [tablecloth.api :as tc]
   [tech.v3.datatype :refer [->reader]]))

(defn col [ds col-kw]
  (let [col-buf (->reader (col-kw ds))]
    (fn [idx-current]
      (fn [idx-ago]
        (let [idx (- idx-current idx-ago)]
          (.readDouble col-buf idx))))))

(def ds
  ; idx      0    1    2    3
  (-> {:x [1.1  2.2  3.3  5.5]
       :y [4.5  6.5  7.7   99]
       :z [-2    4   -4    3]}
      tc/dataset))

(let [x (col ds :x)
      y (col ds :y)
      z (col ds :z)]
  (let [idx-row 1
        x (x idx-row)
        y (y idx-row)
        z (z idx-row)]
    (let [a (+ (x 1) (y 1))
          b (* (y 0) (z 1))]
      {:a a
       :b b})))
 ;;{:a 5.6, :b -13.0}






