(ns notebook.datascience.dataset-meta
  (:require
   [tablecloth.api :as tc]
   [tech.v3.datatype.functional :as fun]
   [tech.v3.dataset.print :as print]))

(def ds1
  (let [n 10]
    (-> {:w (map #(>= % (quot n 2)) (range n))
         :x (range n)
         :y (fun/+ 0.1 (range n))
         :z (map str (range n))}
        (tc/dataset {:dataset-name "ds1"}))))
ds1

(-> ds1
    meta)

(->> ds1
     tc/columns
     (map meta))

(->> ds1
     tc/info)

; [10 4]
(-> ds1
    tc/shape)

(-> ds1
    (update :x #(vary-meta % assoc :hidden? true))
    :x
    meta)

(-> [4 1 :A "v" 2]
    (with-meta {:hidden? true})
    meta)

(-> [4 1 :A "v" 2]
    (with-meta {:hidden? true})
    (conj 99)
    meta)

(-> ds1
    meta)

(-> ds1
    (tc/group-by [:w]))

(-> ds1
    (tc/group-by [:w])
    meta)

(-> ds1
    (tc/group-by [:w])
    (print/print-policy :repl))

(-> ds1
    (tc/group-by [:w])
    (print/print-policy :markdown))

(-> ds1
    (->> (repeat 10)
         (apply tc/bind))
    (tc/set-dataset-name :many-ds1)
    (print/print-range 4))

(-> ds1
    (->> (repeat 10)
         (apply tc/bind))
    (tc/set-dataset-name :many-ds1)
    (print/print-range :all))

(-> ds1
    (->> (repeat 10)
         (apply tc/bind))
    (tc/set-dataset-name :many-ds1)
    (print/print-range (concat (range 4)
                               (range 96 100))))

(-> ds1
    (print/print-types true))

(-> ds1
    (print/print-width 2))

